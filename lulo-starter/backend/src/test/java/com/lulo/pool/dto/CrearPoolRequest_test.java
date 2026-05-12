package com.lulo.pool.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CrearPoolRequestTest {

    @Test
    void constructorVacio_settersYGetters_funcionanCorrectamente() {
        CrearPoolRequest request = new CrearPoolRequest();

        UUID empresaId = UUID.randomUUID();
        UUID creadoPorId = UUID.randomUUID();

        request.setEmpresaId(empresaId);
        request.setCreadoPorId(creadoPorId);
        request.setNombre("Pool Principal");
        request.setConfigJson("{\"color\":\"blue\"}");

        assertEquals(empresaId, request.getEmpresaId());
        assertEquals(creadoPorId, request.getCreadoPorId());
        assertEquals("Pool Principal", request.getNombre());
        assertEquals("{\"color\":\"blue\"}", request.getConfigJson());
    }

    @Test
    void constructorConParametros_asignaValoresCorrectamente() {
        UUID empresaId = UUID.randomUUID();
        UUID creadoPorId = UUID.randomUUID();

        CrearPoolRequest request = new CrearPoolRequest(
                empresaId,
                creadoPorId,
                "Pool Principal",
                "{\"tipo\":\"default\"}"
        );

        assertEquals(empresaId, request.getEmpresaId());
        assertEquals(creadoPorId, request.getCreadoPorId());
        assertEquals("Pool Principal", request.getNombre());
        assertEquals("{\"tipo\":\"default\"}", request.getConfigJson());
    }

    @Test
    void equalsHashCode_yToString_funcionanCorrectamente() {
        UUID empresaId = UUID.randomUUID();
        UUID creadoPorId = UUID.randomUUID();

        CrearPoolRequest request1 = new CrearPoolRequest(
                empresaId,
                creadoPorId,
                "Pool Principal",
                "{}"
        );

        CrearPoolRequest request2 = new CrearPoolRequest(
                empresaId,
                creadoPorId,
                "Pool Principal",
                "{}"
        );

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }
}