package com.lulo.users.invitation.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InvitarUsuarioResponse {

    private Integer invitacionId;
    private String  emailInvitado;
    private String  rolAsignado;
    private String  token;           // TODO: en producción esto va por email, no en la respuesta
    private LocalDateTime expiresAt;
    private String  mensaje;
}
