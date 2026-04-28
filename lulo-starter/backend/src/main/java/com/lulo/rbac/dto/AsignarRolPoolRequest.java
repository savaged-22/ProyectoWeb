package com.lulo.rbac.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarRolPoolRequest {

    @NotNull(message = "El usuario a asignar es obligatorio")
    private UUID usuarioId;

    @NotNull(message = "El usuario que asigna es obligatorio")
    private UUID asignadoPorId;
}
