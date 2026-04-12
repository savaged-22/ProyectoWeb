package com.lulo.messaging.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class NotificacionExternaResponse {

    private Integer id;
    private UUID empresaId;
    private Integer procesoId;
    private String nombreMensaje;
    private String tipo;
    private String destino;
    private boolean activo;
    private LocalDateTime createdAt;
}
