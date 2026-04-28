package com.lulo.process.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProcesoResponse {

    private UUID id;
    private UUID empresaId;
    private String        empresaNombre;
    private UUID poolId;
    private String        poolNombre;
    private UUID creadoPorId;
    private String        creadoPorEmail;
    private String        nombre;
    private String        descripcion;
    private String        categoria;
    private String        estado;
    private boolean       activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
