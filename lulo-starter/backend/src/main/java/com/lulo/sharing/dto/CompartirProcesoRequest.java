package com.lulo.sharing.dto;

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
    private Integer poolDestinoId;

    @NotNull(message = "El usuario que comparte es obligatorio")
    private Integer creadoPorId;

    @Pattern(regexp = "lectura|edicion", message = "El permiso debe ser 'lectura' o 'edicion'")
    private String permiso = "lectura";
}
