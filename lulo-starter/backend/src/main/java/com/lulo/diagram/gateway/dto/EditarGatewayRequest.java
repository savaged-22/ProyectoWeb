package com.lulo.diagram.gateway.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarGatewayRequest {

    // TODO: reemplazar por el usuario extraído del token JWT (HU-Auth)
    @NotNull(message = "El usuario que edita es obligatorio")
    private UUID editadoPorId;

    private UUID laneId;
    private String label;
    private Float posX;
    private Float posY;

    @Pattern(
            regexp = "exclusivo|paralelo|inclusivo",
            message = "El tipo debe ser: exclusivo, paralelo o inclusivo"
    )
    private String tipoGateway;

    private String configJson;
}
