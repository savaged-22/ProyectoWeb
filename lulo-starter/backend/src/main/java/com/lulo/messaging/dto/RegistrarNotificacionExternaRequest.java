package com.lulo.messaging.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RegistrarNotificacionExternaRequest {

    @NotNull
    private UUID empresaId;

    @NotBlank
    private String nombreMensaje;

    /** webhook | email | queue */
    @NotBlank
    @Pattern(regexp = "webhook|email|queue",
             message = "El tipo debe ser 'webhook', 'email' o 'queue'")
    private String tipo;

    /** URL del webhook, dirección de email o nombre del queue */
    @NotBlank
    private String destino;
}
