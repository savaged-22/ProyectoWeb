package com.lulo.diagram.arc.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CrearArcoRequestTest {

    @Test
    void testSettersAndGetters() {
        CrearArcoRequest request = new CrearArcoRequest();

        UUID creadoPorId = UUID.randomUUID();
        UUID fromNodoId = UUID.randomUUID();
        UUID toNodoId = UUID.randomUUID();

        request.setCreadoPorId(creadoPorId);
        request.setFromNodoId(fromNodoId);
        request.setToNodoId(toNodoId);
        request.setCondicionExpr("x > 10");
        request.setPropsJson("{\"color\":\"blue\"}");

        assertEquals(creadoPorId, request.getCreadoPorId());
        assertEquals(fromNodoId, request.getFromNodoId());
        assertEquals(toNodoId, request.getToNodoId());
        assertEquals("x > 10", request.getCondicionExpr());
        assertEquals("{\"color\":\"blue\"}", request.getPropsJson());
    }

    @Test
    void testAllArgsConstructor() {
        UUID creadoPorId = UUID.randomUUID();
        UUID fromNodoId = UUID.randomUUID();
        UUID toNodoId = UUID.randomUUID();

        CrearArcoRequest request = new CrearArcoRequest(
                creadoPorId,
                fromNodoId,
                toNodoId,
                "aprobado == true",
                "{\"tipo\":\"default\"}"
        );

        assertEquals(creadoPorId, request.getCreadoPorId());
        assertEquals(fromNodoId, request.getFromNodoId());
        assertEquals(toNodoId, request.getToNodoId());
        assertEquals("aprobado == true", request.getCondicionExpr());
        assertEquals("{\"tipo\":\"default\"}", request.getPropsJson());
    }

    @Test
    void testConstructorVacio() {
        CrearArcoRequest request = new CrearArcoRequest();

        assertNotNull(request);
        assertNull(request.getCreadoPorId());
        assertNull(request.getFromNodoId());
        assertNull(request.getToNodoId());
        assertNull(request.getCondicionExpr());
        assertNull(request.getPropsJson());
    }

    @Test
    void testToString() {
        CrearArcoRequest request = new CrearArcoRequest();
        assertNotNull(request.toString());
    }
}