package com.lulo.process.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EliminarProcesoRequestTest {

    @Test
    void constructorVacio_settersYGetters_funcionanCorrectamente() {
        EliminarProcesoRequest request = new EliminarProcesoRequest();

        UUID eliminadoPorId = UUID.randomUUID();

        request.setEliminadoPorId(eliminadoPorId);
        request.setConfirmar(true);

        assertEquals(eliminadoPorId, request.getEliminadoPorId());
        assertTrue(request.isConfirmar());
    }

    @Test
    void constructorConParametros_asignaValoresCorrectamente() {
        UUID eliminadoPorId = UUID.randomUUID();

        EliminarProcesoRequest request = new EliminarProcesoRequest(
                eliminadoPorId,
                true
        );

        assertEquals(eliminadoPorId, request.getEliminadoPorId());
        assertTrue(request.isConfirmar());
    }

    @Test
    void equalsHashCodeYToString_funcionanCorrectamente() {
        UUID eliminadoPorId = UUID.randomUUID();

        EliminarProcesoRequest request1 = new EliminarProcesoRequest(
                eliminadoPorId,
                true
        );

        EliminarProcesoRequest request2 = new EliminarProcesoRequest(
                eliminadoPorId,
                true
        );

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    void confirmarPuedeSerFalse() {
        UUID eliminadoPorId = UUID.randomUUID();

        EliminarProcesoRequest request = new EliminarProcesoRequest(
                eliminadoPorId,
                false
        );

        assertEquals(eliminadoPorId, request.getEliminadoPorId());
        assertFalse(request.isConfirmar());
    }
}