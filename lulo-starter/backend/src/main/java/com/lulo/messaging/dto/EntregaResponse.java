package com.lulo.messaging.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EntregaResponse {

    private Integer id;
    private Integer mensajeId;
    private Integer suscripcionId;
    private Integer procesoDestinoId;
    private String nombreMensaje;
    private String correlationKey;
    private String estado;
    private LocalDateTime createdAt;
    private LocalDateTime confirmadoAt;
}
