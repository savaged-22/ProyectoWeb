package com.lulo.diagram.lane.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LaneResponse {

    private Integer       id;
    private Integer       rolProcesoId;
    private String        rolProcesoNombre;
    private String        nombre;
    private int           orden;
    private LocalDateTime createdAt;
}
