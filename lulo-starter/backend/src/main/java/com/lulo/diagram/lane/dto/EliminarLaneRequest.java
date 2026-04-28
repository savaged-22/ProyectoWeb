package com.lulo.diagram.lane.dto;

import java.util.UUID;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EliminarLaneRequest {

    @NotNull(message = "El usuario que elimina es obligatorio")
    private UUID eliminadoPorId;

    @AssertTrue(message = "Debe confirmar explícitamente la eliminación enviando confirmar: true")
    private boolean confirmar;
}
