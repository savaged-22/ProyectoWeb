package com.lulo.diagram.activity.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EditarActividadRequestTest {

    @Test
    void testSettersAndGetters() {
        EditarActividadRequest request = new EditarActividadRequest();

        UUID editadoPorId = UUID.randomUUID();
        UUID laneId = UUID.randomUUID();

        request.setEditadoPorId(editadoPorId);
        request.setLabel("Actualizar actividad");
        request.setTipoActividad("manual");
        request.setLaneId(laneId);
        request.setPosX(150.5f);
        request.setPosY(300.25f);
        request.setPropsJson("{\"estado\":\"ok\"}");

        assertEquals(editadoPorId, request.getEditadoPorId());
        assertEquals("Actualizar actividad", request.getLabel());
        assertEquals("manual", request.getTipoActividad());
        assertEquals(laneId, request.getLaneId());
        assertEquals(150.5f, request.getPosX());
        assertEquals(300.25f, request.getPosY());
        assertEquals("{\"estado\":\"ok\"}", request.getPropsJson());
    }

    @Test
    void testAllArgsConstructor() {
        UUID editadoPorId = UUID.randomUUID();
        UUID laneId = UUID.randomUUID();

        EditarActividadRequest request = new EditarActividadRequest(
                editadoPorId,
                "Editar tarea",
                "tarea",
                laneId,
                10.0f,
                20.0f,
                "{\"prioridad\":\"alta\"}"
        );

        assertEquals(editadoPorId, request.getEditadoPorId());
        assertEquals("Editar tarea", request.getLabel());
        assertEquals("tarea", request.getTipoActividad());
        assertEquals(laneId, request.getLaneId());
        assertEquals(10.0f, request.getPosX());
        assertEquals(20.0f, request.getPosY());
        assertEquals("{\"prioridad\":\"alta\"}", request.getPropsJson());
    }

    @Test
    void testConstructorVacio() {
        EditarActividadRequest request = new EditarActividadRequest();
        assertNotNull(request);
    }

    @Test
    void testToString() {
        EditarActividadRequest request = new EditarActividadRequest();
        assertNotNull(request.toString());
    }
}