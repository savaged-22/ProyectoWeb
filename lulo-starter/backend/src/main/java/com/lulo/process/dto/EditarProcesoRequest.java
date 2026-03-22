package com.lulo.process.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarProcesoRequest {

    // TODO: reemplazar por el usuario extraído del token JWT (HU-Auth)
    @NotNull(message = "El usuario que edita es obligatorio")
    private Integer editadoPorId;

    // Campos opcionales: solo se actualiza lo que llega (semántica PATCH)
    private String nombre;

    private String descripcion;

    private String categoria;

    @Pattern(regexp = "borrador|publicado", message = "El estado debe ser 'borrador' o 'publicado'")
    private String estado;
}
