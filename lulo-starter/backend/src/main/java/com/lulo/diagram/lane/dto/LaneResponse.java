package com.lulo.diagram.lane.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LaneResponse {

    private UUID id;
    private UUID rolProcesoId;
    private String        rolProcesoNombre;
    private String        nombre;
    private int           orden;
    private LocalDateTime createdAt;
}
