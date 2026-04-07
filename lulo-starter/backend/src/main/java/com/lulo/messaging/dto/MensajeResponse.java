package com.lulo.messaging.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MensajeResponse {

    private Integer id;
    private Integer empresaId;
    private Integer procesoOrigenId;
    private String nombreMensaje;
    private String payloadJson;
    private String correlationKey;
    private String estado;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private List<EntregaResponse> entregas;
}
