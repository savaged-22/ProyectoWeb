package com.lulo.diagram.arc.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarArcoRequest {

    // TODO: reemplazar por el usuario extraído del token JWT (HU-Auth)
    @NotNull(message = "El usuario que edita es obligatorio")
    private UUID editadoPorId;

    private UUID fromNodoId;
    private UUID toNodoId;
    private String condicionExpr;
    private String propsJson;
}
