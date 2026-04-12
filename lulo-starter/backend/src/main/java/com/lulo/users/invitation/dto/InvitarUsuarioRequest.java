package com.lulo.users.invitation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitarUsuarioRequest {

    @NotNull(message = "La empresa es obligatoria")
    private UUID empresaId;

    @NotNull(message = "El pool es obligatorio")
    private Integer rolPoolId;

    @NotNull(message = "El usuario que invita es obligatorio")
    private Integer invitadoPorId;

    @NotBlank(message = "El correo del invitado es obligatorio")
    @Email(message = "El correo del invitado no es válido")
    private String emailInvitado;
}
