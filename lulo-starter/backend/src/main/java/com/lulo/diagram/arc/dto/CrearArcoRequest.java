package com.lulo.diagram.arc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearArcoRequest {

    // TODO: reemplazar por el usuario extraído del token JWT (HU-Auth)
    @NotNull(message = "El usuario creador es obligatorio")
    private Integer creadoPorId;

    @NotNull(message = "El nodo origen es obligatorio")
    private Integer fromNodoId;

    @NotNull(message = "El nodo destino es obligatorio")
    private Integer toNodoId;

    private String condicionExpr;
    private String propsJson;
}
