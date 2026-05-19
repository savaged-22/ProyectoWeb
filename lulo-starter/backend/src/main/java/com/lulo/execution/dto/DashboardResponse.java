package com.lulo.execution.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    private int totalActivos;
    private int tareasPendientes;
    private int procesosInstanciadosHoy;
    private String tasaError;

    private List<CasoResumen> casosActivos;
    private List<ProcesoMetrica> metricasProcesos;
    private List<LogEntry> ultimosLogs;
}
