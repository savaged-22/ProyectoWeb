package com.lulo.messaging.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DashboardResponse(
        List<MensajeItem> mensajes,
        List<SuscripcionItem> suscripciones,
        List<EntregaItem> entregas) {

    public record MensajeItem(
            String nombre_mensaje,
            String estado,
            String correlation_key,
            LocalDateTime created_at,
            int payload_size) {}

    public record SuscripcionItem(
            String nombre_mensaje,
            boolean is_active) {}

    public record EntregaItem(
            String estado,
            LocalDateTime delivered_at) {}
}
