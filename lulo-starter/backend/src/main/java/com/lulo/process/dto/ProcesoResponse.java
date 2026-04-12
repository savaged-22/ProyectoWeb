package com.lulo.process.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ProcesoResponse {

    private Integer       id;
    private UUID          empresaId;
    private String        empresaNombre;
    private Integer       poolId;
    private String        poolNombre;
    private Integer       creadoPorId;
    private String        creadoPorEmail;
    private String        nombre;
    private String        descripcion;
    private String        categoria;
    private String        estado;
    private boolean       activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
