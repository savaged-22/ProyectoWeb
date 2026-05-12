package com.lulo.users.invitation.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AceptarInvitacionResponseTest {

    @Test
    void builder_asignaValoresCorrectamente() {
        UUID usuarioId = UUID.randomUUID();

        AceptarInvitacionResponse response = AceptarInvitacionResponse.builder()
                .usuarioId(usuarioId)
                .email("usuario@test.com")
                .empresaNombre("Empresa Test")
                .rolAsignado("Administrador")
                .mensaje("Registro completado exitosamente")
                .build();

        assertNotNull(response);
        assertEquals(usuarioId, response.getUsuarioId());
        assertEquals("usuario@test.com", response.getEmail());
        assertEquals("Empresa Test", response.getEmpresaNombre());
        assertEquals("Administrador", response.getRolAsignado());
        assertEquals("Registro completado exitosamente", response.getMensaje());
    }

    @Test
    void builder_sinValores_funcionaCorrectamente() {
        AceptarInvitacionResponse response =
                AceptarInvitacionResponse.builder().build();

        assertNotNull(response);
        assertNull(response.getUsuarioId());
        assertNull(response.getEmail());
        assertNull(response.getEmpresaNombre());
        assertNull(response.getRolAsignado());
        assertNull(response.getMensaje());
    }

    @Test
    void toString_noEsNull() {
        AceptarInvitacionResponse response = AceptarInvitacionResponse.builder()
                .mensaje("Test")
                .build();

        assertNotNull(response.toString());
    }
}