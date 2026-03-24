package com.lulo.pool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPoolRequest {

    @NotNull(message = "La empresa es obligatoria")
    private Integer empresaId;

    @NotNull(message = "El usuario creador es obligatorio")
    private Integer creadoPorId;

    @NotBlank(message = "El nombre del pool es obligatorio")
    private String nombre;

    private String configJson;
}
