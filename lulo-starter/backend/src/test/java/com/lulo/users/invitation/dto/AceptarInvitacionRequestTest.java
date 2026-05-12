package com.lulo.users.invitation.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AceptarInvitacionRequestTest {

    @Test
    void constructorVacio_settersYGetters_funcionanCorrectamente() {
        AceptarInvitacionRequest request = new AceptarInvitacionRequest();

        request.setPassword("password123");

        assertEquals("password123", request.getPassword());
    }

    @Test
    void constructorConParametros_asignaValoresCorrectamente() {
        AceptarInvitacionRequest request =
                new AceptarInvitacionRequest("password123");

        assertEquals("password123", request.getPassword());
    }

    @Test
    void equalsHashCodeYToString_funcionanCorrectamente() {
        AceptarInvitacionRequest request1 =
                new AceptarInvitacionRequest("password123");

        AceptarInvitacionRequest request2 =
                new AceptarInvitacionRequest("password123");

        AceptarInvitacionRequest request3 =
                new AceptarInvitacionRequest("otraPassword");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());

        assertNotEquals(request1, request3);
        assertNotEquals(request1, null);
        assertNotEquals(request1, new Object());

        assertNotNull(request1.toString());
        assertTrue(request1.toString().contains("password123"));
    }
}