package com.lulo.process.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EditarProcesoRequestTest {

    @Test
    void constructorVacio_settersYGetters_funcionanCorrectamente() {
        EditarProcesoRequest request = new EditarProcesoRequest();

        UUID editadoPorId = UUID.randomUUID();

        request.setEditadoPorId(editadoPorId);
        request.setNombre("Proceso Actualizado");
        request.setDescripcion("Nueva descripción");
        request.setCategoria("Operativo");
        request.setEstado("publicado");

        assertEquals(editadoPorId, request.getEditadoPorId());
        assertEquals("Proceso Actualizado", request.getNombre());
        assertEquals("Nueva descripción", request.getDescripcion());
        assertEquals("Operativo", request.getCategoria());
        assertEquals("publicado", request.getEstado());
    }

    @Test
    void constructorConParametros_asignaValoresCorrectamente() {
        UUID editadoPorId = UUID.randomUUID();

        EditarProcesoRequest request = new EditarProcesoRequest(
                editadoPorId,
                "Proceso Test",
                "Descripción Test",
                "Administrativo",
                "borrador"
        );

        assertEquals(editadoPorId, request.getEditadoPorId());
        assertEquals("Proceso Test", request.getNombre());
        assertEquals("Descripción Test", request.getDescripcion());
        assertEquals("Administrativo", request.getCategoria());
        assertEquals("borrador", request.getEstado());
    }

    @Test
    void equalsHashCodeYToString_funcionanCorrectamente() {
        UUID editadoPorId = UUID.randomUUID();

        EditarProcesoRequest request1 = new EditarProcesoRequest(
                editadoPorId,
                "Proceso Test",
                "Descripción",
                "Categoría",
                "publicado"
        );

        EditarProcesoRequest request2 = new EditarProcesoRequest(
                editadoPorId,
                "Proceso Test",
                "Descripción",
                "Categoría",
                "publicado"
        );

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    void camposOpcionales_puedenSerNull() {
        UUID editadoPorId = UUID.randomUUID();

        EditarProcesoRequest request = new EditarProcesoRequest(
                editadoPorId,
                null,
                null,
                null,
                null
        );

        assertEquals(editadoPorId, request.getEditadoPorId());
        assertNull(request.getNombre());
        assertNull(request.getDescripcion());
        assertNull(request.getCategoria());
        assertNull(request.getEstado());
    }
}