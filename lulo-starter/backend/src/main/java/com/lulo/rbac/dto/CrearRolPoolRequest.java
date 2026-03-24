package com.lulo.rbac.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearRolPoolRequest {

    @NotNull(message = "El pool es obligatorio")
    private Integer poolId;

    @NotNull(message = "El usuario creador es obligatorio")
    private Integer creadoPorId;

    @NotBlank(message = "El nombre del rol es obligatorio")
    private String nombre;

    private String descripcion;
    private List<String> codigosPermiso;
}
