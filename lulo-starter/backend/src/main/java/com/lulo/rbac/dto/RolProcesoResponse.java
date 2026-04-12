package com.lulo.rbac.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class RolProcesoResponse {

    private Integer id;
    private UUID empresaId;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private LocalDateTime createdAt;
}
