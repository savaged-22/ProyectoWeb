package com.lulo.pool.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EditarPoolRequestTest {

    @Test
    void constructorVacio_settersYGetters_funcionanCorrectamente() {
        EditarPoolRequest request = new EditarPoolRequest();

        request.setNombre("Nuevo Pool");
        request.setConfigJson("{\"theme\":\"dark\"}");

        assertEquals("Nuevo Pool", request.getNombre());
        assertEquals("{\"theme\":\"dark\"}", request.getConfigJson());
    }

    @Test
    void constructorConParametros_asignaValoresCorrectamente() {
        EditarPoolRequest request = new EditarPoolRequest();

        request.setNombre("Pool Editado");
        request.setConfigJson("{\"modo\":\"avanzado\"}");

        assertEquals("Pool Editado", request.getNombre());
        assertEquals("{\"modo\":\"avanzado\"}", request.getConfigJson());
    }

    @Test
    void equalsHashCode_yToString_funcionanCorrectamente() {
        EditarPoolRequest request1 = new EditarPoolRequest();
        request1.setNombre("Pool Editado");
        request1.setConfigJson("{}");

        EditarPoolRequest request2 = new EditarPoolRequest();
        request2.setNombre("Pool Editado");
        request2.setConfigJson("{}");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }
}