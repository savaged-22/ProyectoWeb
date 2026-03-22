package com.lulo.company.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistroEmpresaResponse {

    private Integer empresaId;
    private String empresaNombre;
    private Integer usuarioId;
    private String emailAdmin;
    private String poolDefault;
    private String mensaje;
}
