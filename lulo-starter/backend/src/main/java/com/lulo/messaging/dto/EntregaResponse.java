package com.lulo.messaging.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EntregaResponse {

    private UUID id;
    private UUID mensajeId;
    private UUID suscripcionId;
    private UUID procesoDestinoId;
    private String nombreMensaje;
    private String correlationKey;
    private String estado;
    private LocalDateTime createdAt;
    private LocalDateTime confirmadoAt;
}
