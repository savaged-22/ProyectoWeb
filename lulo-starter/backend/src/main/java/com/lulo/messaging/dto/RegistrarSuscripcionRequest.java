package com.lulo.messaging.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RegistrarSuscripcionRequest {

    @NotNull
    private UUID empresaId;

    @NotBlank
    private String nombreMensaje;

    /**
     * Clave de correlación para filtrar mensajes dirigidos a esta instancia específica (HU-28).
     * Dejar en null para recibir todos los mensajes con ese nombre sin importar la correlación.
     */
    private String correlationKey;
}
