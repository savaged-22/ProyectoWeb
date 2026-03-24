package com.lulo.sharing.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarComparticionProcesoRequest {

    @NotNull(message = "El usuario que actualiza es obligatorio")
    private Integer actualizadoPorId;

    @Pattern(regexp = "lectura|edicion", message = "El permiso debe ser 'lectura' o 'edicion'")
    private String permiso;
}
