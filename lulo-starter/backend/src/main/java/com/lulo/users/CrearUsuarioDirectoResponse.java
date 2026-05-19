package com.lulo.users;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CrearUsuarioDirectoResponse {
    private UUID   usuarioId;
    private String email;
    private String rolAsignado;
    private String empresaNombre;
    private String mensaje;
}
