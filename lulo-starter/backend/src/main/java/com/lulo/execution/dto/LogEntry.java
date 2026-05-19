package com.lulo.execution.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogEntry {
    private String tiempo;
    private String nivel;
    private String claseCss;
    private String mensaje;
    private boolean isErrorBlock;
    private String referencia;
}
