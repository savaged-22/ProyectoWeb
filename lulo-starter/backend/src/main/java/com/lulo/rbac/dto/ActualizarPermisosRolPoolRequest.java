package com.lulo.rbac.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarPermisosRolPoolRequest {

    @NotNull(message = "El usuario que actualiza permisos es obligatorio")
    private UUID actualizadoPorId;

    @NotNull(message = "La lista de permisos es obligatoria")
    private List<String> codigosPermiso;
}
