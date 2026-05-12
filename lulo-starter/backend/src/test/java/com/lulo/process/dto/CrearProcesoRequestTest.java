package com.lulo.process.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CrearProcesoRequestTest {

    @Test
    void constructorVacio_settersYGetters_funcionanCorrectamente() {
        CrearProcesoRequest request = new CrearProcesoRequest();

        UUID empresaId = UUID.randomUUID();
        UUID poolId = UUID.randomUUID();
        UUID creadoPorId = UUID.randomUUID();

        request.setEmpresaId(empresaId);
        request.setPoolId(poolId);
        request.setCreadoPorId(creadoPorId);
        request.setNombre("Proceso de Compras");
        request.setDescripcion("Descripción del proceso");
        request.setCategoria("Administrativo");
        request.setEstado("publicado");

        assertEquals(empresaId, request.getEmpresaId());
        assertEquals(poolId, request.getPoolId());
        assertEquals(creadoPorId, request.getCreadoPorId());
        assertEquals("Proceso de Compras", request.getNombre());
        assertEquals("Descripción del proceso", request.getDescripcion());
        assertEquals("Administrativo", request.getCategoria());
        assertEquals("publicado", request.getEstado());
    }

    @Test
    void constructorConParametros_asignaValoresCorrectamente() {
        UUID empresaId = UUID.randomUUID();
        UUID poolId = UUID.randomUUID();
        UUID creadoPorId = UUID.randomUUID();

        CrearProcesoRequest request = new CrearProcesoRequest(
                empresaId,
                poolId,
                creadoPorId,
                "Proceso Test",
                "Descripción Test",
                "Operativo",
                "borrador"
        );

        assertEquals(empresaId, request.getEmpresaId());
        assertEquals(poolId, request.getPoolId());
        assertEquals(creadoPorId, request.getCreadoPorId());
        assertEquals("Proceso Test", request.getNombre());
        assertEquals("Descripción Test", request.getDescripcion());
        assertEquals("Operativo", request.getCategoria());
        assertEquals("borrador", request.getEstado());
    }

    @Test
    void constructorVacio_estadoPorDefectoEsBorrador() {
        CrearProcesoRequest request = new CrearProcesoRequest();

        assertEquals("borrador", request.getEstado());
    }

    @Test
    void equalsHashCodeYToString_funcionanCorrectamente() {
        UUID empresaId = UUID.randomUUID();
        UUID poolId = UUID.randomUUID();
        UUID creadoPorId = UUID.randomUUID();

        CrearProcesoRequest request1 = new CrearProcesoRequest(
                empresaId,
                poolId,
                creadoPorId,
                "Proceso Test",
                "Descripción",
                "Categoría",
                "borrador"
        );

        CrearProcesoRequest request2 = new CrearProcesoRequest(
                empresaId,
                poolId,
                creadoPorId,
                "Proceso Test",
                "Descripción",
                "Categoría",
                "borrador"
        );

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }
}