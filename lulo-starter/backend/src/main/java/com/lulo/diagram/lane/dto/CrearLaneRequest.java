package com.lulo.diagram.lane.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearLaneRequest {

    @NotNull(message = "El usuario creador es obligatorio")
    private UUID creadoPorId;

    private UUID rolProcesoId;

    @NotBlank(message = "El nombre de la lane es obligatorio")
    private String nombre;

    private Integer orden = 0;
}
