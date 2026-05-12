package com.lulo.pool;

import com.lulo.pool.dto.CrearPoolRequest;
import com.lulo.pool.dto.EditarPoolRequest;
import com.lulo.pool.dto.PoolResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PoolControllerTest {

    private PoolService poolService;
    private PoolController poolController;

    @BeforeEach
    void setUp() {
        poolService = mock(PoolService.class);
        poolController = new PoolController(poolService);
    }

    @Test
    void listar_retornaListaDePools() {
        UUID empresaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        PoolResponse poolResponse = mock(PoolResponse.class);
        List<PoolResponse> respuestaEsperada = List.of(poolResponse);

        when(poolService.listar(empresaId, usuarioId))
                .thenReturn(respuestaEsperada);

        List<PoolResponse> response = poolController.listar(empresaId, usuarioId);

        assertEquals(respuestaEsperada, response);
        verify(poolService).listar(empresaId, usuarioId);
    }

    @Test
    void crear_retornaPoolCreado() {
        CrearPoolRequest request = new CrearPoolRequest();
        PoolResponse respuestaEsperada = mock(PoolResponse.class);

        when(poolService.crear(request))
                .thenReturn(respuestaEsperada);

        PoolResponse response = poolController.crear(request);

        assertEquals(respuestaEsperada, response);
        verify(poolService).crear(request);
    }

    @Test
    void editar_retornaPoolEditado() {
        UUID poolId = UUID.randomUUID();
        EditarPoolRequest request = new EditarPoolRequest();
        PoolResponse respuestaEsperada = mock(PoolResponse.class);

        when(poolService.editar(poolId, request))
                .thenReturn(respuestaEsperada);

        PoolResponse response = poolController.editar(poolId, request);

        assertEquals(respuestaEsperada, response);
        verify(poolService).editar(poolId, request);
    }
}