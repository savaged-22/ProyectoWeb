package com.lulo.users.invitation.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AceptarInvitacionResponse {

    private UUID usuarioId;
    private String  email;
    private String  empresaNombre;
    private String  rolAsignado;
    private String  mensaje;
}
