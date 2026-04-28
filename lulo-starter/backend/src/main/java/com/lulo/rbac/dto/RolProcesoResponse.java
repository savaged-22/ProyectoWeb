package com.lulo.rbac.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RolProcesoResponse {

    private UUID id;
    private UUID empresaId;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private LocalDateTime createdAt;
}
