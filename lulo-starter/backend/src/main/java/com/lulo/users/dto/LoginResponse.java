package com.lulo.users.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private String token;
    private String message;
    private UUID usuarioId;
    private UUID empresaId;
    private String empresaNombre;
    private String email;
    /** PROPIETARIO | COLABORADOR */
    private String rol;
}
