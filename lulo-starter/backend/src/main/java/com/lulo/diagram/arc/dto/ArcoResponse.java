package com.lulo.diagram.arc.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ArcoResponse {

    private Integer       id;
    private Integer       fromNodoId;
    private Integer       toNodoId;
    private String        condicionExpr;
    private String        propsJson;
    private boolean       activo;
    private LocalDateTime createdAt;
}
