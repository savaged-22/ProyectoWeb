package com.lulo.process.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EliminarProcesoRequest {

    // TODO: reemplazar por el usuario extraído del token JWT (HU-Auth)
    @NotNull(message = "El usuario que elimina es obligatorio")
    private Integer eliminadoPorId;

    @AssertTrue(message = "Debe confirmar explícitamente la eliminación enviando confirmar: true")
    private boolean confirmar;
}
