package com.lulo.rbac.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearRolProcesoRequest {

    @NotNull(message = "La empresa es obligatoria")
    private UUID empresaId;

    // TODO: reemplazar por el usuario extraído del token JWT (HU-Auth)
    @NotNull(message = "El usuario creador es obligatorio")
    private UUID creadoPorId;

    @NotBlank(message = "El nombre del rol es obligatorio")
    private String nombre;

    private String descripcion;
}
