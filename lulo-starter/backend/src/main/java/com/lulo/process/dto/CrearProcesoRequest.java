package com.lulo.process.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearProcesoRequest {

    @NotNull(message = "La empresa es obligatoria")
    private UUID empresaId;

    @NotNull(message = "El pool es obligatorio")
    private Integer poolId;

    @NotNull(message = "El usuario creador es obligatorio")
    private Integer creadoPorId;

    @NotBlank(message = "El nombre del proceso es obligatorio")
    private String nombre;

    private String descripcion;

    private String categoria;

    @Pattern(regexp = "borrador|publicado", message = "El estado debe ser 'borrador' o 'publicado'")
    private String estado = "borrador";
}
