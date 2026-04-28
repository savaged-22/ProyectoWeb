package com.lulo.messaging;

import java.util.UUID;

import com.lulo.messaging.dto.NotificacionExternaResponse;
import com.lulo.messaging.dto.RegistrarNotificacionExternaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procesos/{procesoId}/notificaciones-externas")
@RequiredArgsConstructor
@Tag(name = "Notificaciones externas",
     description = "HU-26 · Gestión de destinos webhook, email y queue para eventos de mensajes")
public class NotificacionExternaController {

    private final NotificacionExternaService notificacionExternaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar notificación externa (HU-26)",
               description = "Registra un destino (webhook, email o queue) al que se notificará "
                       + "cuando el proceso lance un mensaje con el nombre indicado.")
    public NotificacionExternaResponse registrar(
            @PathVariable UUID procesoId,
            @Valid @RequestBody RegistrarNotificacionExternaRequest request) {
        return notificacionExternaService.registrar(procesoId, request);
    }

    @GetMapping
    @Operation(summary = "Listar notificaciones externas (HU-26)",
               description = "Lista los destinos de notificación activos de un proceso.")
    public List<NotificacionExternaResponse> listar(@PathVariable UUID procesoId) {
        return notificacionExternaService.listar(procesoId);
    }

    @DeleteMapping("/{notificacionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desactivar notificación externa (HU-26)",
               description = "Desactiva un destino de notificación.")
    public void desactivar(@PathVariable UUID procesoId,
                           @PathVariable UUID notificacionId) {
        notificacionExternaService.desactivar(notificacionId);
    }
}
