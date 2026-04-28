package com.lulo.diagram.activity.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearActividadRequest {

    // TODO: reemplazar por el usuario extraído del token JWT (HU-Auth)
    @NotNull(message = "El usuario creador es obligatorio")
    private UUID creadoPorId;

    // Lane opcional: la actividad puede no estar asignada a un carril todavía
    private UUID laneId;

    @NotBlank(message = "El nombre de la actividad es obligatorio")
    private String label;

    @NotNull(message = "El tipo de actividad es obligatorio")
    @Pattern(
            regexp = "tarea|subproceso|manual|servicio|script",
            message = "El tipo debe ser: tarea, subproceso, manual, servicio o script"
    )
    private String tipoActividad;

    // Posición visual en el canvas (puede omitirse si el frontend la asigna después)
    private Float posX;
    private Float posY;

    // JSON libre para propiedades adicionales específicas del tipo de actividad
    private String propsJson;
}
