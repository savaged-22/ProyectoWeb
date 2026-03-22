package com.lulo.diagram.activity.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarActividadRequest {

    // TODO: reemplazar por el usuario extraído del token JWT (HU-Auth)
    @NotNull(message = "El usuario que edita es obligatorio")
    private Integer editadoPorId;

    // Todos opcionales — solo actualiza lo que llega (semántica PATCH)
    private String label;

    @Pattern(
            regexp = "tarea|subproceso|manual|servicio|script",
            message = "El tipo debe ser: tarea, subproceso, manual, servicio o script"
    )
    private String tipoActividad;

    private Integer laneId;
    private Float   posX;
    private Float   posY;
    private String  propsJson;
}
