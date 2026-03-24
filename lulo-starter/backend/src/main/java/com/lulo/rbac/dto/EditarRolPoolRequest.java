package com.lulo.rbac.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarRolPoolRequest {

    @NotNull(message = "El usuario que edita es obligatorio")
    private Integer editadoPorId;

    private String nombre;
    private String descripcion;
}
