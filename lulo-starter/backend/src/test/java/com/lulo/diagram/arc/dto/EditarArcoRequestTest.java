package com.lulo.diagram.arc.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EditarArcoRequestTest {

    @Test
    void testSettersAndGetters() {
        EditarArcoRequest request = new EditarArcoRequest();

        UUID editadoPorId = UUID.randomUUID();
        UUID fromNodoId = UUID.randomUUID();
        UUID toNodoId = UUID.randomUUID();

        request.setEditadoPorId(editadoPorId);
        request.setFromNodoId(fromNodoId);
        request.setToNodoId(toNodoId);
        request.setCondicionExpr("monto > 1000");
        request.setPropsJson("{\"color\":\"green\"}");

        assertEquals(editadoPorId, request.getEditadoPorId());
        assertEquals(fromNodoId, request.getFromNodoId());
        assertEquals(toNodoId, request.getToNodoId());
        assertEquals("monto > 1000", request.getCondicionExpr());
        assertEquals("{\"color\":\"green\"}", request.getPropsJson());
    }

    @Test
    void testAllArgsConstructor() {
        UUID editadoPorId = UUID.randomUUID();
        UUID fromNodoId = UUID.randomUUID();
        UUID toNodoId = UUID.randomUUID();

        EditarArcoRequest request = new EditarArcoRequest(
                editadoPorId,
                fromNodoId,
                toNodoId,
                "estado == 'OK'",
                "{\"tipo\":\"condicional\"}"
        );

        assertEquals(editadoPorId, request.getEditadoPorId());
        assertEquals(fromNodoId, request.getFromNodoId());
        assertEquals(toNodoId, request.getToNodoId());
        assertEquals("estado == 'OK'", request.getCondicionExpr());
        assertEquals("{\"tipo\":\"condicional\"}", request.getPropsJson());
    }

    @Test
    void testConstructorVacio() {
        EditarArcoRequest request = new EditarArcoRequest();

        assertNotNull(request);
        assertNull(request.getEditadoPorId());
        assertNull(request.getFromNodoId());
        assertNull(request.getToNodoId());
        assertNull(request.getCondicionExpr());
        assertNull(request.getPropsJson());
    }

    @Test
    void testToString() {
        EditarArcoRequest request = new EditarArcoRequest();
        assertNotNull(request.toString());
    }
}