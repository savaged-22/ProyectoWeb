package com.lulo.messaging;

import com.lulo.messaging.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Mensajería entre procesos",
     description = "HU-25 Message Throw · HU-27 Message Catch · HU-28 Correlación")
public class MensajeController {

    private final MensajeService mensajeService;

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
            @PathVariable Integer procesoId,
            @Valid @RequestBody RegistrarSuscripcionRequest request) {
        return mensajeService.registrarSuscripcion(procesoId, request);
    }

    @GetMapping("/api/procesos/{procesoId}/suscripciones-mensaje")
    @Operation(summary = "Listar suscripciones de mensaje (HU-27)",
               description = "Lista las suscripciones activas de un proceso.")
    public List<SuscripcionResponse> listarSuscripciones(@PathVariable Integer procesoId) {
        return mensajeService.listarSuscripciones(procesoId);
    }

    @DeleteMapping("/api/suscripciones-mensaje/{suscripcionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desactivar suscripción (HU-27)",
               description = "Desactiva una suscripción: el proceso deja de recibir ese mensaje.")
    public void desactivarSuscripcion(@PathVariable Integer suscripcionId) {
        mensajeService.desactivarSuscripcion(suscripcionId);
    }

    // ---- HU-28: Correlación — entregas ----

    @GetMapping("/api/procesos/{procesoId}/entregas-pendientes")
    @Operation(summary = "Listar entregas pendientes (HU-28)",
               description = "Lista los mensajes entregados a este proceso que aún no han sido confirmados.")
    public List<EntregaResponse> listarEntregasPendientes(@PathVariable Integer procesoId) {
        return mensajeService.listarEntregasPendientes(procesoId);
    }

    @PostMapping("/api/entregas/{entregaId}/confirmar")
    @Operation(summary = "Confirmar recepción de mensaje (HU-28)",
               description = "El proceso destino confirma que procesó la entrega. "
                       + "Implementa la correlación: solo el proceso correcto puede confirmar.")
    public EntregaResponse confirmarRecepcion(
            @PathVariable Integer entregaId,
            @Valid @RequestBody ConfirmarRecepcionRequest request) {
        return mensajeService.confirmarRecepcion(entregaId, request);
    }
}
