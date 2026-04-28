package com.lulo.messaging.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnviarMensajeRequest {

    @NotNull
    private UUID empresaId;

    @NotNull
    private UUID procesoOrigenId;

    @NotBlank
    private String nombreMensaje;

    /** JSON libre con la carga del mensaje (puede ser null) */
    private String payloadJson;

    /**
     * Clave de correlación para dirigir el mensaje a una instancia específica (HU-28).
     * Cuando se especifica, solo se entregan las suscripciones cuya correlationKey coincida.
     */
    private String correlationKey;
}
