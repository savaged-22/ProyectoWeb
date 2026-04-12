package com.lulo.company.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class RegistroEmpresaResponse {

    private UUID empresaId;
    private String empresaNombre;
    private Integer usuarioId;
    private String emailAdmin;
    private String poolDefault;
    private String mensaje;
}
