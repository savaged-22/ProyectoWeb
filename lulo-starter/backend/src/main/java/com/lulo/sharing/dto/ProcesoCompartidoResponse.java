package com.lulo.sharing.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProcesoCompartidoResponse {

    private UUID procesoId;
    private UUID poolDestinoId;
    private String poolDestinoNombre;
    private UUID creadoPorId;
    private String creadoPorEmail;
    private String permiso;
    private LocalDateTime createdAt;
}
