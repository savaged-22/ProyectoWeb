package com.lulo.rbac.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RolProcesoResponse {

    private Integer id;
    private Integer empresaId;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private LocalDateTime createdAt;
}
