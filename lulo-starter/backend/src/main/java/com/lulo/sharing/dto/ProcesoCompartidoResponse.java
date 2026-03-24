package com.lulo.sharing.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProcesoCompartidoResponse {

    private Integer procesoId;
    private Integer poolDestinoId;
    private String poolDestinoNombre;
    private Integer creadoPorId;
    private String creadoPorEmail;
    private String permiso;
    private LocalDateTime createdAt;
}
