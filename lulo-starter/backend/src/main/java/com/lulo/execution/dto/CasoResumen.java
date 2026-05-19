package com.lulo.execution.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CasoResumen {
    private UUID id;
    private String procesoNombre;
    private String estado;
    private String actividadActual;
    private LocalDateTime fechaInicio;
}
