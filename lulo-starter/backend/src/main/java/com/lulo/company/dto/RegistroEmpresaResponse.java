package com.lulo.company.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistroEmpresaResponse {

    private UUID empresaId;
    private String empresaNombre;
    private UUID usuarioId;
    private String emailAdmin;
    private String poolDefault;
    private String mensaje;
}
