package com.lulo.pool.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PoolResponse {

    private Integer id;
    private Integer empresaId;
    private String empresaNombre;
    private String nombre;
    private String configJson;
    private LocalDateTime createdAt;
}
