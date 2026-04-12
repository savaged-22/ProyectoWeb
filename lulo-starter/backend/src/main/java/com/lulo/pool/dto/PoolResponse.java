package com.lulo.pool.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PoolResponse {

    private Integer id;
    private UUID empresaId;
    private String empresaNombre;
    private String nombre;
    private String configJson;
    private LocalDateTime createdAt;
}
