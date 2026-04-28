package com.lulo.messaging.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificacionExternaResponse {

    private UUID id;
    private UUID empresaId;
    private UUID procesoId;
    private String nombreMensaje;
    private String tipo;
    private String destino;
    private boolean activo;
    private LocalDateTime createdAt;
}
