package com.lulo.diagram.gateway.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearGatewayRequest {

    // TODO: reemplazar por el usuario extraído del token JWT (HU-Auth)
    @NotNull(message = "El usuario creador es obligatorio")
    private Integer creadoPorId;

    private Integer laneId;
    private String label;
    private Float posX;
    private Float posY;

    @NotNull(message = "El tipo de gateway es obligatorio")
    @Pattern(
            regexp = "exclusivo|paralelo|inclusivo",
            message = "El tipo debe ser: exclusivo, paralelo o inclusivo"
    )
    private String tipoGateway;

    private String configJson;
}
