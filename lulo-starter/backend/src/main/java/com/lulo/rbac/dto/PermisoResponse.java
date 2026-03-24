package com.lulo.rbac.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermisoResponse {

    private Integer id;
    private String codigo;
    private String descripcion;
}
