package com.lulo.messaging;

import com.lulo.audit.AuditService;
import com.lulo.common.exception.ApiException;
import com.lulo.company.EmpresaRepository;
import com.lulo.messaging.dto.*;
import com.lulo.process.Proceso;
import com.lulo.process.ProcesoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * HU-25: Enviar mensaje entre procesos (Message Throw)
 * HU-27: Recibir mensaje y activar proceso (Message Catch)
 * HU-28: Correlación de mensajes con instancias de proceso
 */
@Service
@RequiredArgsConstructor
public class MensajeService {

    private final MensajeProcesoRepository mensajeRepository;
    private final SuscripcionMensajeRepository suscripcionRepository;
    private final EntregaMensajeRepository entregaRepository;
    private final NotificacionExternaService notificacionExternaService;
    private final ProcesoRepository procesoRepository;
    private final EmpresaRepository empresaRepository;
    private final AuditService auditService;

    // ------------------------------------------------------------------
    // HU-25: Message Throw — enviar mensaje
    // ------------------------------------------------------------------

    @Transactional
    public MensajeResponse enviar(EnviarMensajeRequest request) {
        var empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ApiException("Empresa no encontrada", HttpStatus.NOT_FOUND));

        Proceso procesoOrigen = procesoRepository.findByIdAndActivoTrue(request.getProcesoOrigenId())
                .orElseThrow(() -> new ApiException("Proceso origen no encontrado", HttpStatus.NOT_FOUND));

        if (!procesoOrigen.getEmpresa().getId().equals(empresa.getId())) {
            throw new ApiException("El proceso no pertenece a la empresa indicada", HttpStatus.FORBIDDEN);
        }

        MensajeProceso mensaje = new MensajeProceso();
        mensaje.setEmpresa(empresa);
        mensaje.setProcesoOrigen(procesoOrigen);
        mensaje.setNombreMensaje(request.getNombreMensaje().trim());
        mensaje.setPayloadJson(request.getPayloadJson());
        mensaje.setCorrelationKey(request.getCorrelationKey());
        mensaje.setEstado("pendiente");
        MensajeProceso mensajeGuardado = mensajeRepository.save(mensaje);

        // HU-28: correlación — buscar suscripciones que coincidan
        List<SuscripcionMensaje> suscripciones = resolverSuscripciones(
                empresa.getId(), mensajeGuardado.getNombreMensaje(), mensajeGuardado.getCorrelationKey());

        List<EntregaMensaje> entregas = suscripciones.stream()
                .map(suscripcion -> crearEntrega(mensajeGuardado, suscripcion))
                .toList();
        entregaRepository.saveAll(entregas);

        // Marcar como entregado si hay al menos una suscripción
        if (!entregas.isEmpty()) {
            mensajeGuardado.setEstado("entregado");
            mensajeGuardado.setDeliveredAt(LocalDateTime.now());
            mensajeRepository.save(mensajeGuardado);
        }

        // HU-26: disparar notificaciones externas
        notificacionExternaService.disparar(mensajeGuardado);

        auditService.registrar(
                empresa,
                procesoOrigen.getCreatedByUser(),
                "MENSAJE_PROCESO",
                mensajeGuardado.getId(),
                "CREAR",
                null,
                Map.of(
                        "nombreMensaje", mensajeGuardado.getNombreMensaje(),
                        "correlationKey", mensajeGuardado.getCorrelationKey() != null ? mensajeGuardado.getCorrelationKey() : "",
                        "entregasGeneradas", entregas.size()
                )
        );

        List<EntregaMensaje> entregasGuardadas = entregaRepository.findByMensajeId(mensajeGuardado.getId());
        return toResponse(mensajeGuardado, entregasGuardadas);
    }

    // ------------------------------------------------------------------
    // HU-27: Message Catch — registrar / desactivar suscripción
    // ------------------------------------------------------------------

    @Transactional
    public SuscripcionResponse registrarSuscripcion(Integer procesoId,
                                                    RegistrarSuscripcionRequest request) {
        var empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ApiException("Empresa no encontrada", HttpStatus.NOT_FOUND));

        Proceso proceso = procesoRepository.findByIdAndActivoTrue(procesoId)
                .orElseThrow(() -> new ApiException("Proceso no encontrado", HttpStatus.NOT_FOUND));

        if (!proceso.getEmpresa().getId().equals(empresa.getId())) {
            throw new ApiException("El proceso no pertenece a la empresa indicada", HttpStatus.FORBIDDEN);
        }

        SuscripcionMensaje suscripcion = new SuscripcionMensaje();
        suscripcion.setEmpresa(empresa);
        suscripcion.setProceso(proceso);
        suscripcion.setNombreMensaje(request.getNombreMensaje().trim());
        suscripcion.setCorrelationKey(request.getCorrelationKey());
        suscripcion = suscripcionRepository.save(suscripcion);

        return toSuscripcionResponse(suscripcion);
    }

    @Transactional(readOnly = true)
    public List<SuscripcionResponse> listarSuscripciones(Integer procesoId) {
        return suscripcionRepository.findByProcesoIdAndActivoTrue(procesoId)
                .stream()
                .map(this::toSuscripcionResponse)
                .toList();
    }

    @Transactional
    public void desactivarSuscripcion(Integer suscripcionId) {
        SuscripcionMensaje suscripcion = suscripcionRepository.findById(suscripcionId)
                .orElseThrow(() -> new ApiException("Suscripción no encontrada", HttpStatus.NOT_FOUND));
        suscripcion.setActivo(false);
        suscripcionRepository.save(suscripcion);
    }

    // ------------------------------------------------------------------
    // HU-28: Correlación — confirmar recepción de entrega
    // ------------------------------------------------------------------

    @Transactional
    public EntregaResponse confirmarRecepcion(Integer entregaId, ConfirmarRecepcionRequest request) {
        EntregaMensaje entrega = entregaRepository.findById(entregaId)
                .orElseThrow(() -> new ApiException("Entrega no encontrada", HttpStatus.NOT_FOUND));

        if (!"pendiente".equals(entrega.getEstado())) {
            throw new ApiException("La entrega ya fue confirmada", HttpStatus.CONFLICT);
        }

        if (!entrega.getSuscripcion().getProceso().getId().equals(request.getProcesoDestinoId())) {
            throw new ApiException("El proceso no coincide con el destinatario de la entrega", HttpStatus.FORBIDDEN);
        }

        entrega.setEstado("confirmado");
        entrega.setConfirmadoAt(LocalDateTime.now());
        entrega = entregaRepository.save(entrega);

        return toEntregaResponse(entrega);
    }

    @Transactional(readOnly = true)
    public List<EntregaResponse> listarEntregasPendientes(Integer procesoId) {
        return entregaRepository.findBySuscripcion_ProcesoIdAndEstado(procesoId, "pendiente")
                .stream()
                .map(this::toEntregaResponse)
                .toList();
    }

    // ------------------------------------------------------------------
    // Helpers privados
    // ------------------------------------------------------------------

    /**
     * HU-28: Lógica de correlación.
     * Si el mensaje tiene correlationKey → solo suscripciones con la misma clave.
     * Si el mensaje no tiene correlationKey → todas las suscripciones del nombre.
     */
    private List<SuscripcionMensaje> resolverSuscripciones(UUID empresaId,
                                                           String nombreMensaje,
                                                           String correlationKey) {
        List<SuscripcionMensaje> candidatas =
                suscripcionRepository.findByEmpresaIdAndNombreMensajeAndActivoTrue(empresaId, nombreMensaje);

        if (correlationKey == null || correlationKey.isBlank()) {
            return candidatas;
        }

        return candidatas.stream()
                .filter(s -> correlationKey.equals(s.getCorrelationKey()) || s.getCorrelationKey() == null)
                .toList();
    }

    private EntregaMensaje crearEntrega(MensajeProceso mensaje, SuscripcionMensaje suscripcion) {
        EntregaMensaje entrega = new EntregaMensaje();
        entrega.setMensaje(mensaje);
        entrega.setSuscripcion(suscripcion);
        entrega.setEstado("pendiente");
        return entrega;
    }

    private MensajeResponse toResponse(MensajeProceso mensaje, List<EntregaMensaje> entregas) {
        return MensajeResponse.builder()
                .id(mensaje.getId())
                .empresaId(mensaje.getEmpresa().getId())
                .procesoOrigenId(mensaje.getProcesoOrigen().getId())
                .nombreMensaje(mensaje.getNombreMensaje())
                .payloadJson(mensaje.getPayloadJson())
                .correlationKey(mensaje.getCorrelationKey())
                .estado(mensaje.getEstado())
                .createdAt(mensaje.getCreatedAt())
                .deliveredAt(mensaje.getDeliveredAt())
                .entregas(entregas.stream().map(this::toEntregaResponse).toList())
                .build();
    }

    private SuscripcionResponse toSuscripcionResponse(SuscripcionMensaje s) {
        return SuscripcionResponse.builder()
                .id(s.getId())
                .empresaId(s.getEmpresa().getId())
                .procesoId(s.getProceso().getId())
                .nombreMensaje(s.getNombreMensaje())
                .correlationKey(s.getCorrelationKey())
                .activo(s.isActivo())
                .createdAt(s.getCreatedAt())
                .build();
    }

    private EntregaResponse toEntregaResponse(EntregaMensaje e) {
        return EntregaResponse.builder()
                .id(e.getId())
                .mensajeId(e.getMensaje().getId())
                .suscripcionId(e.getSuscripcion().getId())
                .procesoDestinoId(e.getSuscripcion().getProceso().getId())
                .nombreMensaje(e.getSuscripcion().getNombreMensaje())
                .correlationKey(e.getSuscripcion().getCorrelationKey())
                .estado(e.getEstado())
                .createdAt(e.getCreatedAt())
                .confirmadoAt(e.getConfirmadoAt())
                .build();
    }
}
