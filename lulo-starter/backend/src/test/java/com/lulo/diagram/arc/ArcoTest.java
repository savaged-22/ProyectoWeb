package com.lulo.diagram.arc;

import com.lulo.diagram.node.Nodo;
import com.lulo.process.Proceso;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ArcoTest {

    @Test
    void testSettersAndGetters() {
        Arco arco = new Arco();

        UUID id = UUID.randomUUID();
        Proceso proceso = new Proceso();
        Nodo fromNodo = new Nodo();
        Nodo toNodo = new Nodo();

        arco.setId(id);
        arco.setProceso(proceso);
        arco.setFromNodo(fromNodo);
        arco.setToNodo(toNodo);
        arco.setCondicionExpr("monto > 1000");
        arco.setPropsJson("{\"color\":\"blue\"}");
        arco.setActivo(false);

        assertEquals(id, arco.getId());
        assertEquals(proceso, arco.getProceso());
        assertEquals(fromNodo, arco.getFromNodo());
        assertEquals(toNodo, arco.getToNodo());
        assertEquals("monto > 1000", arco.getCondicionExpr());
        assertEquals("{\"color\":\"blue\"}", arco.getPropsJson());
        assertFalse(arco.isActivo());
    }

    @Test
    void testConstructorVacio() {
        Arco arco = new Arco();

        assertNotNull(arco);
        assertNull(arco.getId());
        assertNull(arco.getProceso());
        assertNull(arco.getFromNodo());
        assertNull(arco.getToNodo());
        assertNull(arco.getCondicionExpr());
        assertNull(arco.getPropsJson());
        assertTrue(arco.isActivo()); // valor por defecto
    }
}