package com.lulo.sharing.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompartirProcesoRequest {

    @NotNull(message = "El pool destino es obligatorio")
    private UUID poolDestinoId;

    @NotNull(message = "El usuario que comparte es obligatorio")
    private UUID creadoPorId;

    @Pattern(regexp = "lectura|edicion", message = "El permiso debe ser 'lectura' o 'edicion'")
    private String permiso = "lectura";
}
