package com.lulo.execution.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcesoMetrica {
    private String nombreProceso;
    private int casosActivos;
    private String tiempoPromedio;
    private String estadoProceso;
    private java.time.LocalDateTime fechaCreacion;
}
