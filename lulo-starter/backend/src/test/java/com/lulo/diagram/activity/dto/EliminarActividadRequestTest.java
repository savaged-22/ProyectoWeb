package com.lulo.diagram.activity.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EliminarActividadRequestTest {

    @Test
    void testSettersAndGetters() {
        EliminarActividadRequest request = new EliminarActividadRequest();

        UUID eliminadoPorId = UUID.randomUUID();

        request.setEliminadoPorId(eliminadoPorId);
        request.setConfirmar(true);

        assertEquals(eliminadoPorId, request.getEliminadoPorId());
        assertTrue(request.isConfirmar());
    }

    @Test
    void testAllArgsConstructor() {
        UUID eliminadoPorId = UUID.randomUUID();

        EliminarActividadRequest request = new EliminarActividadRequest(
                eliminadoPorId,
                true
        );

        assertEquals(eliminadoPorId, request.getEliminadoPorId());
        assertTrue(request.isConfirmar());
    }

    @Test
    void testConstructorVacio() {
        EliminarActividadRequest request = new EliminarActividadRequest();

        assertNotNull(request);
        assertNull(request.getEliminadoPorId());
        assertFalse(request.isConfirmar());
    }

    @Test
    void testToString() {
        EliminarActividadRequest request = new EliminarActividadRequest();
        assertNotNull(request.toString());
    }
}