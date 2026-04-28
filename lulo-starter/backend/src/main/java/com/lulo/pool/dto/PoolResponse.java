package com.lulo.pool.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PoolResponse {

    private UUID id;
    private UUID empresaId;
    private String empresaNombre;
    private String nombre;
    private String configJson;
    private LocalDateTime createdAt;
}
