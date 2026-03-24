package com.lulo.rbac.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RolPoolResponse {

    private Integer id;
    private Integer poolId;
    private String poolNombre;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private boolean esPropietario;
    private List<PermisoResponse> permisos;
    private LocalDateTime createdAt;
}
