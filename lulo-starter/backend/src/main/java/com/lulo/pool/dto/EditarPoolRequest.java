package com.lulo.pool.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarPoolRequest {

    @NotNull(message = "El usuario que edita es obligatorio")
    private Integer editadoPorId;

    private String nombre;
    private String configJson;
}
