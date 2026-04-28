package com.lulo.diagram.arc.dto;

import java.util.UUID;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EliminarArcoRequest {

    // TODO: reemplazar por el usuario extraído del token JWT (HU-Auth)
    @NotNull(message = "El usuario que elimina es obligatorio")
    private UUID eliminadoPorId;

    @AssertTrue(message = "Debe confirmar explícitamente la eliminación enviando confirmar: true")
    private boolean confirmar;
}
