package com.lulo.messaging;

import com.lulo.common.exception.ApiException;
import com.lulo.company.EmpresaRepository;
import com.lulo.messaging.dto.NotificacionExternaResponse;
import com.lulo.messaging.dto.RegistrarNotificacionExternaRequest;
import com.lulo.process.Proceso;
import com.lulo.process.ProcesoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * HU-26: Envío de notificaciones externas (webhook, email, queue)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionExternaService {

    private final NotificacionExternaRepository notificacionRepository;
    private final ProcesoRepository procesoRepository;
    private final EmpresaRepository empresaRepository;
    private final RestTemplate restTemplate;

    // ------------------------------------------------------------------
    // CRUD de destinos de notificación
    // ------------------------------------------------------------------

    @Transactional
    public NotificacionExternaResponse registrar(Integer procesoId,
                                                 RegistrarNotificacionExternaRequest request) {
        var empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ApiException("Empresa no encontrada", HttpStatus.NOT_FOUND));

        Proceso proceso = procesoRepository.findByIdAndActivoTrue(procesoId)
                .orElseThrow(() -> new ApiException("Proceso no encontrado", HttpStatus.NOT_FOUND));

        if (!proceso.getEmpresa().getId().equals(empresa.getId())) {
            throw new ApiException("El proceso no pertenece a la empresa indicada", HttpStatus.FORBIDDEN);
        }

        NotificacionExterna notificacion = new NotificacionExterna();
        notificacion.setEmpresa(empresa);
        notificacion.setProceso(proceso);
        notificacion.setNombreMensaje(request.getNombreMensaje().trim());
        notificacion.setTipo(request.getTipo());
        notificacion.setDestino(request.getDestino().trim());
        notificacion = notificacionRepository.save(notificacion);

        return toResponse(notificacion);
    }

    @Transactional(readOnly = true)
    public List<NotificacionExternaResponse> listar(Integer procesoId) {
        return notificacionRepository.findByProcesoIdAndActivoTrue(procesoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void desactivar(Integer notificacionId) {
        NotificacionExterna notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new ApiException("Notificación externa no encontrada", HttpStatus.NOT_FOUND));
        notificacion.setActivo(false);
        notificacionRepository.save(notificacion);
    }

    // ------------------------------------------------------------------
    // Disparo de notificaciones al enviar un mensaje (HU-26)
    // ------------------------------------------------------------------

    /**
     * Busca todos los destinos activos para el nombre de mensaje del evento
     * y dispara la notificación según el tipo (webhook / email / queue).
     * Esta operación no interrumpe el flujo principal aunque falle.
     */
    @Transactional(readOnly = true)
    public void disparar(MensajeProceso mensaje) {
        List<NotificacionExterna> destinos = notificacionRepository
                .findByEmpresaIdAndNombreMensajeAndActivoTrue(
                        mensaje.getEmpresa().getId(), mensaje.getNombreMensaje());

        for (NotificacionExterna destino : destinos) {
            try {
                switch (destino.getTipo()) {
                    case "webhook" -> dispararWebhook(destino, mensaje);
                    case "email"   -> dispararEmail(destino, mensaje);
                    case "queue"   -> dispararQueue(destino, mensaje);
                    default        -> log.warn("Tipo de notificación desconocido: {}", destino.getTipo());
                }
            } catch (Exception e) {
                log.error("Error al disparar notificación {} [id={}] para mensaje [id={}]: {}",
                        destino.getTipo(), destino.getId(), mensaje.getId(), e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------
    // Implementaciones por tipo
    // ------------------------------------------------------------------

    private void dispararWebhook(NotificacionExterna destino, MensajeProceso mensaje) {
        Map<String, Object> payload = buildPayload(mensaje);
        restTemplate.postForEntity(destino.getDestino(), payload, Void.class);
        log.info("Webhook disparado → {} para mensaje '{}' [id={}]",
                destino.getDestino(), mensaje.getNombreMensaje(), mensaje.getId());
    }

    private void dispararEmail(NotificacionExterna destino, MensajeProceso mensaje) {
        // En un entorno real se integraría con JavaMailSender o un servicio SMTP.
        // Por ahora se registra en el log como evidencia de la intención.
        log.info("EMAIL → {} | Asunto: Mensaje '{}' recibido [proceso_id={}]",
                destino.getDestino(), mensaje.getNombreMensaje(), mensaje.getProcesoOrigen().getId());
    }

    private void dispararQueue(NotificacionExterna destino, MensajeProceso mensaje) {
        // En un entorno real se publicaría en un broker (Kafka, RabbitMQ, etc.).
        // Por ahora se registra en el log como evidencia de la intención.
        log.info("QUEUE → {} | Mensaje: '{}' payload: {}",
                destino.getDestino(), mensaje.getNombreMensaje(), mensaje.getPayloadJson());
    }

    private Map<String, Object> buildPayload(MensajeProceso mensaje) {
        return Map.of(
                "mensajeId",      mensaje.getId(),
                "empresaId",      mensaje.getEmpresa().getId(),
                "procesoOrigenId", mensaje.getProcesoOrigen().getId(),
                "nombreMensaje",  mensaje.getNombreMensaje(),
                "correlationKey", mensaje.getCorrelationKey() != null ? mensaje.getCorrelationKey() : "",
                "payload",        mensaje.getPayloadJson() != null ? mensaje.getPayloadJson() : "{}",
                "createdAt",      mensaje.getCreatedAt().toString()
        );
    }

    private NotificacionExternaResponse toResponse(NotificacionExterna n) {
        return NotificacionExternaResponse.builder()
                .id(n.getId())
                .empresaId(n.getEmpresa().getId())
                .procesoId(n.getProceso().getId())
                .nombreMensaje(n.getNombreMensaje())
                .tipo(n.getTipo())
                .destino(n.getDestino())
                .activo(n.isActivo())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
