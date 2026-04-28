package com.lulo.diagram.lane.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarLaneRequest {

    @NotNull(message = "El usuario que edita es obligatorio")
    private UUID editadoPorId;

    private UUID rolProcesoId;
    private Boolean limpiarRolProceso;
    private String nombre;
    private Integer orden;
}
