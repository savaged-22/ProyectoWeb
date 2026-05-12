package com.lulo.diagram.activity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActividadTest {

    @Test
    void gettersYSetters_funcionanCorrectamente() {
        Actividad actividad = new Actividad();

        actividad.setTipoActividad("manual");
        actividad.setPropsJson("{\"clave\":\"valor\"}");

        assertEquals("manual", actividad.getTipoActividad());
        assertEquals("{\"clave\":\"valor\"}", actividad.getPropsJson());
    }

    @Test
    void constructorVacio_creaObjetoCorrectamente() {
        Actividad actividad = new Actividad();

        assertNotNull(actividad);
        assertNull(actividad.getTipoActividad());
        assertNull(actividad.getPropsJson());
    }
}