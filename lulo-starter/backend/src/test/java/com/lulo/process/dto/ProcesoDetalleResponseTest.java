package com.lulo.process.dto;

import com.lulo.diagram.arc.dto.ArcoResponse;
import com.lulo.diagram.lane.dto.LaneResponse;
import com.lulo.diagram.node.dto.NodoResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProcesoDetalleResponseTest {

    @Test
    void builder_asignaValoresCorrectamente() {
        UUID id = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();
        UUID poolId = UUID.randomUUID();
        UUID creadoPorId = UUID.randomUUID();

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        LaneResponse lane = LaneResponse.builder().build();
        NodoResponse nodo = NodoResponse.builder().build();
        ArcoResponse arco = ArcoResponse.builder().build();

        ProcesoDetalleResponse response = ProcesoDetalleResponse.builder()
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
                .estado("borrador")
                .activo(true)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lanes(List.of(lane))
                .nodos(List.of(nodo))
                .arcos(List.of(arco))
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
        assertEquals("borrador", response.getEstado());
        assertTrue(response.isActivo());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());

        assertNotNull(response.getLanes());
        assertEquals(1, response.getLanes().size());

        assertNotNull(response.getNodos());
        assertEquals(1, response.getNodos().size());

        assertNotNull(response.getArcos());
        assertEquals(1, response.getArcos().size());
    }

    @Test
    void builder_conValoresMinimos_funcionaCorrectamente() {
        ProcesoDetalleResponse response = ProcesoDetalleResponse.builder()
                .nombre("Proceso Simple")
                .build();

        assertEquals("Proceso Simple", response.getNombre());
        assertNull(response.getId());
        assertNull(response.getEmpresaId());
        assertNull(response.getLanes());
        assertNull(response.getNodos());
        assertNull(response.getArcos());
        assertFalse(response.isActivo()); // valor por defecto de boolean
    }

    @Test
    void toString_noEsNull() {
        ProcesoDetalleResponse response = ProcesoDetalleResponse.builder()
                .nombre("Proceso Test")
                .build();

        assertNotNull(response.toString());
    }
}