package com.lulo.messaging;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.lulo.messaging.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Mensajería entre procesos",
     description = "HU-25 Message Throw · HU-27 Message Catch · HU-28 Correlación")
public class MensajeController {

    private final MensajeService mensajeService;
    private final MensajeProcesoRepository mensajeRepo;
    private final SuscripcionMensajeRepository suscripcionRepo;
    private final EntregaMensajeRepository entregaRepo;

    // ---- Dashboard de monitoring (vista agregada) ----

    @GetMapping("/api/mensajes/dashboard")
    @Operation(summary = "Dashboard de mensajería",
               description = "Devuelve listas agregadas (mensajes, suscripciones, entregas) "
                       + "para alimentar el panel de Monitoring del frontend.")
    public Map<String, Object> dashboard() {
        var sortByCreatedDesc = Sort.by(Sort.Direction.DESC, "createdAt");
        var top20 = PageRequest.of(0, 20, sortByCreatedDesc);

        List<Map<String, Object>> mensajes = mensajeRepo.findAll(top20).stream()
                .map(m -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", m.getId());
                    row.put("nombre_mensaje", m.getNombreMensaje());
                    row.put("estado", m.getEstado());
                    row.put("correlation_key", m.getCorrelationKey());
                    row.put("created_at", m.getCreatedAt());
                    row.put("payload_size", m.getPayloadJson() != null ? m.getPayloadJson().length() : 0);
                    return row;
                })
                .toList();

        List<Map<String, Object>> suscripciones = suscripcionRepo.findAll(top20).stream()
                .map(s -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", s.getId());
                    row.put("nombre_mensaje", s.getNombreMensaje());
                    row.put("correlation_key", s.getCorrelationKey());
                    row.put("is_active", s.isActivo());
                    row.put("created_at", s.getCreatedAt());
                    return row;
                })
                .toList();

        List<Map<String, Object>> entregas = entregaRepo.findAll(top20).stream()
                .map(e -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", e.getId());
                    row.put("estado", e.getEstado());
                    row.put("delivered_at", e.getCreatedAt());
                    row.put("confirmado_at", e.getConfirmadoAt());
                    return row;
                })
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("mensajes", mensajes);
        response.put("suscripciones", suscripciones);
        response.put("entregas", entregas);
        return response;
    }

    // ---- HU-25: Message Throw ----

    @PostMapping("/api/mensajes")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Enviar mensaje (HU-25)",
               description = "Lanza un mensaje desde un proceso origen. "
                       + "Resuelve correlación (HU-28) y dispara notificaciones externas (HU-26).")
    public MensajeResponse enviar(@Valid @RequestBody EnviarMensajeRequest request) {
        return mensajeService.enviar(request);
    }

    // ---- HU-27: Message Catch — suscripciones ----

    @PostMapping("/api/procesos/{procesoId}/suscripciones-mensaje")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar suscripción de mensaje (HU-27)",
               description = "Registra que un proceso desea recibir mensajes con un nombre dado. "
                       + "Opcionalmente con correlationKey para filtrar por instancia (HU-28).")
    public SuscripcionResponse registrarSuscripcion(
            @PathVariable UUID procesoId,
            @Valid @RequestBody RegistrarSuscripcionRequest request) {
        return mensajeService.registrarSuscripcion(procesoId, request);
    }

    @GetMapping("/api/procesos/{procesoId}/suscripciones-mensaje")
    @Operation(summary = "Listar suscripciones de mensaje (HU-27)",
               description = "Lista las suscripciones activas de un proceso.")
    public List<SuscripcionResponse> listarSuscripciones(@PathVariable UUID procesoId) {
        return mensajeService.listarSuscripciones(procesoId);
    }

    @DeleteMapping("/api/suscripciones-mensaje/{suscripcionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desactivar suscripción (HU-27)",
               description = "Desactiva una suscripción: el proceso deja de recibir ese mensaje.")
    public void desactivarSuscripcion(@PathVariable UUID suscripcionId) {
        mensajeService.desactivarSuscripcion(suscripcionId);
    }

    // ---- HU-28: Correlación — entregas ----

    @GetMapping("/api/procesos/{procesoId}/entregas-pendientes")
    @Operation(summary = "Listar entregas pendientes (HU-28)",
               description = "Lista los mensajes entregados a este proceso que aún no han sido confirmados.")
    public List<EntregaResponse> listarEntregasPendientes(@PathVariable UUID procesoId) {
        return mensajeService.listarEntregasPendientes(procesoId);
    }

    @PostMapping("/api/entregas/{entregaId}/confirmar")
    @Operation(summary = "Confirmar recepción de mensaje (HU-28)",
               description = "El proceso destino confirma que procesó la entrega. "
                       + "Implementa la correlación: solo el proceso correcto puede confirmar.")
    public EntregaResponse confirmarRecepcion(
            @PathVariable UUID entregaId,
            @Valid @RequestBody ConfirmarRecepcionRequest request) {
        return mensajeService.confirmarRecepcion(entregaId, request);
    }
}
