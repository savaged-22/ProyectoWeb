package com.lulo.users.invitation.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InvitarUsuarioResponseTest {

    @Test
    void builder_asignaValoresCorrectamente() {
        UUID invitacionId = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(72);

        InvitarUsuarioResponse response = InvitarUsuarioResponse.builder()
                .invitacionId(invitacionId)
                .emailInvitado("usuario@test.com")
                .rolAsignado("Administrador")
                .token("token-123")
                .expiresAt(expiresAt)
                .mensaje("Invitación creada exitosamente")
                .build();

        assertNotNull(response);
        assertEquals(invitacionId, response.getInvitacionId());
        assertEquals("usuario@test.com", response.getEmailInvitado());
        assertEquals("Administrador", response.getRolAsignado());
        assertEquals("token-123", response.getToken());
        assertEquals(expiresAt, response.getExpiresAt());
        assertEquals("Invitación creada exitosamente", response.getMensaje());
    }

    @Test
    void builder_sinValores_funcionaCorrectamente() {
        InvitarUsuarioResponse response =
                InvitarUsuarioResponse.builder().build();

        assertNotNull(response);
        assertNull(response.getInvitacionId());
        assertNull(response.getEmailInvitado());
        assertNull(response.getRolAsignado());
        assertNull(response.getToken());
        assertNull(response.getExpiresAt());
        assertNull(response.getMensaje());
    }

    @Test
    void toString_noEsNull() {
        InvitarUsuarioResponse response = InvitarUsuarioResponse.builder()
                .mensaje("Test")
                .build();

        assertNotNull(response.toString());
    }
}