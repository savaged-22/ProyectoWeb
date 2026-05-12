package com.lulo.pool.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PoolResponseTest {

    @Test
    void builder_asignaValoresCorrectamente() {
        UUID id = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        PoolResponse response = PoolResponse.builder()
                .id(id)
                .empresaId(empresaId)
                .empresaNombre("Mi Empresa")
                .nombre("Pool Principal")
                .configJson("{\"tema\":\"dark\"}")
                .createdAt(createdAt)
                .build();

        assertEquals(id, response.getId());
        assertEquals(empresaId, response.getEmpresaId());
        assertEquals("Mi Empresa", response.getEmpresaNombre());
        assertEquals("Pool Principal", response.getNombre());
        assertEquals("{\"tema\":\"dark\"}", response.getConfigJson());
        assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    void builder_sinValoresOpcionales_funcionaCorrectamente() {
        PoolResponse response = PoolResponse.builder()
                .nombre("Pool Simple")
                .build();

        assertEquals("Pool Simple", response.getNombre());
        assertNull(response.getId());
        assertNull(response.getEmpresaId());
        assertNull(response.getEmpresaNombre());
        assertNull(response.getConfigJson());
        assertNull(response.getCreatedAt());
    }

    @Test
    void toString_noEsNull() {
        PoolResponse response = PoolResponse.builder()
                .nombre("Pool Test")
                .build();

        // Como solo tiene @Getter y @Builder, no garantiza toString personalizado.
        // Pero Object.toString() siempre retorna un valor no nulo.
        assertNotNull(response.toString());
    }
}