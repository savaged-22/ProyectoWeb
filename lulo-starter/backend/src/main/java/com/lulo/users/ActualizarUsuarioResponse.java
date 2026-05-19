package com.lulo.users;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActualizarUsuarioResponse {

    private UUID usuarioId;
    private String email;
    private String estado;
    private String rolAsignado;
}
