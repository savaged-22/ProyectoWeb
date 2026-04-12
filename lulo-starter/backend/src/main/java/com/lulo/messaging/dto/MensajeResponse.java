package com.lulo.messaging.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class MensajeResponse {

    private Integer id;
    private UUID empresaId;
    private Integer procesoOrigenId;
    private String nombreMensaje;
    private String payloadJson;
    private String correlationKey;
    private String estado;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private List<EntregaResponse> entregas;
}
