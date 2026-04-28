package com.lulo.rbac.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermisoResponse {

    private UUID id;
    private String codigo;
    private String descripcion;
}
