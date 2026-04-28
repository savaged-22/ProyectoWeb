package com.lulo.messaging.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MensajeResponse {

    private UUID id;
    private UUID empresaId;
    private UUID procesoOrigenId;
    private String nombreMensaje;
    private String payloadJson;
    private String correlationKey;
    private String estado;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private List<EntregaResponse> entregas;
}
