package com.lulo.users.invitation.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InvitarUsuarioRequestTest {

    @Test
    void constructorVacio_settersYGetters_funcionanCorrectamente() {
        InvitarUsuarioRequest request = new InvitarUsuarioRequest();

        UUID empresaId = UUID.randomUUID();
        UUID rolPoolId = UUID.randomUUID();
        UUID invitadoPorId = UUID.randomUUID();

        request.setEmpresaId(empresaId);
        request.setRolPoolId(rolPoolId);
        request.setInvitadoPorId(invitadoPorId);
        request.setEmailInvitado("usuario@test.com");

        assertEquals(empresaId, request.getEmpresaId());
        assertEquals(rolPoolId, request.getRolPoolId());
        assertEquals(invitadoPorId, request.getInvitadoPorId());
        assertEquals("usuario@test.com", request.getEmailInvitado());
    }

    @Test
    void constructorConParametros_asignaValoresCorrectamente() {
        UUID empresaId = UUID.randomUUID();
        UUID rolPoolId = UUID.randomUUID();
        UUID invitadoPorId = UUID.randomUUID();

        InvitarUsuarioRequest request = new InvitarUsuarioRequest(
                empresaId,
                rolPoolId,
                invitadoPorId,
                "usuario@test.com"
        );

        assertEquals(empresaId, request.getEmpresaId());
        assertEquals(rolPoolId, request.getRolPoolId());
        assertEquals(invitadoPorId, request.getInvitadoPorId());
        assertEquals("usuario@test.com", request.getEmailInvitado());
    }

    @Test
    void equalsHashCodeYToString_funcionanCorrectamente() {
        UUID empresaId = UUID.randomUUID();
        UUID rolPoolId = UUID.randomUUID();
        UUID invitadoPorId = UUID.randomUUID();

        InvitarUsuarioRequest request1 = new InvitarUsuarioRequest(
                empresaId,
                rolPoolId,
                invitadoPorId,
                "usuario@test.com"
        );

        InvitarUsuarioRequest request2 = new InvitarUsuarioRequest(
                empresaId,
                rolPoolId,
                invitadoPorId,
                "usuario@test.com"
        );

        InvitarUsuarioRequest request3 = new InvitarUsuarioRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "otro@test.com"
        );

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());

        assertNotEquals(request1, request3);
        assertNotEquals(request1, null);
        assertNotEquals(request1, new Object());

        assertNotNull(request1.toString());
        assertTrue(request1.toString().contains("usuario@test.com"));
    }
}