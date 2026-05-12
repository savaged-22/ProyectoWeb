package com.lulo.diagram.arc.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ArcoResponseTest {

    @Test
    void testBuilderYGetters() {
        UUID id = UUID.randomUUID();
        UUID fromNodoId = UUID.randomUUID();
        UUID toNodoId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        ArcoResponse response = ArcoResponse.builder()
                .id(id)
                .fromNodoId(fromNodoId)
                .toNodoId(toNodoId)
                .condicionExpr("x > 10")
                .propsJson("{\"color\":\"red\"}")
                .activo(true)
                .createdAt(createdAt)
                .build();

        assertEquals(id, response.getId());
        assertEquals(fromNodoId, response.getFromNodoId());
        assertEquals(toNodoId, response.getToNodoId());
        assertEquals("x > 10", response.getCondicionExpr());
        assertEquals("{\"color\":\"red\"}", response.getPropsJson());
        assertTrue(response.isActivo());
        assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    void testBuilderConValoresMinimos() {
        ArcoResponse response = ArcoResponse.builder().build();

        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getFromNodoId());
        assertNull(response.getToNodoId());
        assertNull(response.getCondicionExpr());
        assertNull(response.getPropsJson());
        assertFalse(response.isActivo());
        assertNull(response.getCreatedAt());
    }
}