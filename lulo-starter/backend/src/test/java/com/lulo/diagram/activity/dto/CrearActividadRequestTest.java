package com.lulo.diagram.activity.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CrearActividadRequestTest {

    @Test
    void constructorVacioYSettersFuncionanCorrectamente() {
        CrearActividadRequest request = new CrearActividadRequest();

        UUID creadoPorId = UUID.randomUUID();
        UUID laneId = UUID.randomUUID();

        request.setCreadoPorId(creadoPorId);
        request.setLaneId(laneId);
        request.setLabel("Aprobar solicitud");
        request.setTipoActividad("manual");
        request.setPosX(100.5f);
        request.setPosY(200.75f);
        request.setPropsJson("{\"color\":\"blue\"}");

        assertEquals(creadoPorId, request.getCreadoPorId());
        assertEquals(laneId, request.getLaneId());
        assertEquals("Aprobar solicitud", request.getLabel());
        assertEquals("manual", request.getTipoActividad());
        assertEquals(Float.valueOf(100.5f), request.getPosX());
        assertEquals(Float.valueOf(200.75f), request.getPosY());
        assertEquals("{\"color\":\"blue\"}", request.getPropsJson());
    }

    @Test
    void constructorConArgumentosFuncionaCorrectamente() {
        UUID creadoPorId = UUID.randomUUID();
        UUID laneId = UUID.randomUUID();

        CrearActividadRequest request = new CrearActividadRequest(
                creadoPorId,
                laneId,
                "Revisar documento",
                "tarea",
                50.0f,
                75.0f,
                "{\"prioridad\":\"alta\"}"
        );

        assertEquals(creadoPorId, request.getCreadoPorId());
        assertEquals(laneId, request.getLaneId());
        assertEquals("Revisar documento", request.getLabel());
        assertEquals("tarea", request.getTipoActividad());
        assertEquals(Float.valueOf(50.0f), request.getPosX());
        assertEquals(Float.valueOf(75.0f), request.getPosY());
        assertEquals("{\"prioridad\":\"alta\"}", request.getPropsJson());
    }

    @Test
    void toStringNoRetornaNull() {
        CrearActividadRequest request = new CrearActividadRequest();
        assertNotNull(request.toString());
    }
}