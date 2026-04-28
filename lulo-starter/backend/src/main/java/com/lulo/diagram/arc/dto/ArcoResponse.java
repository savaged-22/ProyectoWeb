package com.lulo.diagram.arc.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ArcoResponse {

    private UUID id;
    private UUID fromNodoId;
    private UUID toNodoId;
    private String        condicionExpr;
    private String        propsJson;
    private boolean       activo;
    private LocalDateTime createdAt;
}
