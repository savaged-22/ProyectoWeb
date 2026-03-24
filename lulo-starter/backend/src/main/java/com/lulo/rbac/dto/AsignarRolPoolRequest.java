package com.lulo.rbac.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarRolPoolRequest {

    @NotNull(message = "El usuario a asignar es obligatorio")
    private Integer usuarioId;

    @NotNull(message = "El usuario que asigna es obligatorio")
    private Integer asignadoPorId;
}
