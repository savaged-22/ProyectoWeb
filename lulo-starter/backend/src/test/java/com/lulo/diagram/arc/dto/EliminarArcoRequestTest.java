package com.lulo.diagram.arc.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EliminarArcoRequestTest {

    @Test
    void testSettersAndGetters() {
        EliminarArcoRequest request = new EliminarArcoRequest();

        UUID eliminadoPorId = UUID.randomUUID();

        request.setEliminadoPorId(eliminadoPorId);
        request.setConfirmar(true);

        assertEquals(eliminadoPorId, request.getEliminadoPorId());
        assertTrue(request.isConfirmar());
    }

    @Test
    void testAllArgsConstructor() {
        UUID eliminadoPorId = UUID.randomUUID();

        EliminarArcoRequest request = new EliminarArcoRequest(
                eliminadoPorId,
                true
        );

        assertEquals(eliminadoPorId, request.getEliminadoPorId());
        assertTrue(request.isConfirmar());
    }

    @Test
    void testConstructorVacio() {
        EliminarArcoRequest request = new EliminarArcoRequest();

        assertNotNull(request);
        assertNull(request.getEliminadoPorId());
        assertFalse(request.isConfirmar());
    }

    @Test
    void testToString() {
        EliminarArcoRequest request = new EliminarArcoRequest();
        assertNotNull(request.toString());
    }
}