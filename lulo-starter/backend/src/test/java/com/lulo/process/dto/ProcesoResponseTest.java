package com.lulo.process.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProcesoResponseTest {

    @Test
    void builder_asignaValoresCorrectamente() {
        UUID id = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();
        UUID poolId = UUID.randomUUID();
        UUID creadoPorId = UUID.randomUUID();

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        ProcesoResponse response = ProcesoResponse.builder()
                .id(id)
                .empresaId(empresaId)
                .empresaNombre("Empresa Test")
                .poolId(poolId)
                .poolNombre("Pool Principal")
                .creadoPorId(creadoPorId)
                .creadoPorEmail("admin@test.com")
                .nombre("Proceso Test")
                .descripcion("Descripción del proceso")
                .categoria("Operativo")
                .estado("publicado")
                .activo(true)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertEquals(id, response.getId());
        assertEquals(empresaId, response.getEmpresaId());
        assertEquals("Empresa Test", response.getEmpresaNombre());
        assertEquals(poolId, response.getPoolId());
        assertEquals("Pool Principal", response.getPoolNombre());
        assertEquals(creadoPorId, response.getCreadoPorId());
        assertEquals("admin@test.com", response.getCreadoPorEmail());
        assertEquals("Proceso Test", response.getNombre());
        assertEquals("Descripción del proceso", response.getDescripcion());
        assertEquals("Operativo", response.getCategoria());
        assertEquals("publicado", response.getEstado());
        assertTrue(response.isActivo());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    @Test
    void builder_conValoresMinimos_funcionaCorrectamente() {
        ProcesoResponse response = ProcesoResponse.builder()
                .nombre("Proceso Simple")
                .build();

        assertEquals("Proceso Simple", response.getNombre());
        assertNull(response.getId());
        assertNull(response.getEmpresaId());
        assertNull(response.getPoolId());
        assertNull(response.getCreadoPorId());
        assertNull(response.getDescripcion());
        assertNull(response.getCategoria());
        assertNull(response.getEstado());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
        assertFalse(response.isActivo()); // valor por defecto del boolean
    }

    @Test
    void toString_noEsNull() {
        ProcesoResponse response = ProcesoResponse.builder()
                .nombre("Proceso Test")
                .build();

        assertNotNull(response.toString());
    }
}