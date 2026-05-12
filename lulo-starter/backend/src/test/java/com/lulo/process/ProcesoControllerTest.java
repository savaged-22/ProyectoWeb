package com.lulo.process;

import com.lulo.process.dto.CrearProcesoRequest;
import com.lulo.process.dto.EditarProcesoRequest;
import com.lulo.process.dto.EliminarProcesoRequest;
import com.lulo.process.dto.ProcesoDetalleResponse;
import com.lulo.process.dto.ProcesoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcesoControllerTest {

    @Mock
    private ProcesoService procesoService;

    @InjectMocks
    private ProcesoController procesoController;

    @Test
    void listar_debeRetornarPaginaDeProcesos() {
        UUID empresaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        PageRequest pageable = PageRequest.of(0, 10);

        ProcesoResponse proceso = ProcesoResponse.builder()
                .id(UUID.randomUUID())
                .nombre("Proceso Test")
                .build();

        Page<ProcesoResponse> page = new PageImpl<>(List.of(proceso));

        when(procesoService.listar(
                empresaId,
                usuarioId,
                "borrador",
                "Operativo",
                "Proceso",
                pageable
        )).thenReturn(page);

        Page<ProcesoResponse> resultado = procesoController.listar(
                empresaId,
                usuarioId,
                "borrador",
                "Operativo",
                "Proceso",
                pageable
        );

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Proceso Test", resultado.getContent().get(0).getNombre());

        verify(procesoService).listar(
                empresaId,
                usuarioId,
                "borrador",
                "Operativo",
                "Proceso",
                pageable
        );
    }

    @Test
    void obtener_debeRetornarDetalleProceso() {
        UUID procesoId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        ProcesoDetalleResponse detalle = ProcesoDetalleResponse.builder()
                .id(procesoId)
                .nombre("Proceso Detalle")
                .build();

        when(procesoService.obtener(procesoId, empresaId, usuarioId))
                .thenReturn(detalle);

        ProcesoDetalleResponse resultado = procesoController.obtener(
                procesoId,
                empresaId,
                usuarioId
        );

        assertNotNull(resultado);
        assertEquals(procesoId, resultado.getId());
        assertEquals("Proceso Detalle", resultado.getNombre());

        verify(procesoService).obtener(procesoId, empresaId, usuarioId);
    }

    @Test
    void crear_debeRetornarProcesoCreado() {
        CrearProcesoRequest request = new CrearProcesoRequest();

        ProcesoResponse response = ProcesoResponse.builder()
                .id(UUID.randomUUID())
                .nombre("Proceso Nuevo")
                .build();

        when(procesoService.crear(request))
                .thenReturn(response);

        ProcesoResponse resultado = procesoController.crear(request);

        assertNotNull(resultado);
        assertEquals("Proceso Nuevo", resultado.getNombre());

        verify(procesoService).crear(request);
    }

    @Test
    void editar_debeRetornarProcesoEditado() {
        UUID procesoId = UUID.randomUUID();
        EditarProcesoRequest request = new EditarProcesoRequest();

        ProcesoResponse response = ProcesoResponse.builder()
                .id(procesoId)
                .nombre("Proceso Editado")
                .build();

        when(procesoService.editar(procesoId, request))
                .thenReturn(response);

        ProcesoResponse resultado = procesoController.editar(procesoId, request);

        assertNotNull(resultado);
        assertEquals("Proceso Editado", resultado.getNombre());

        verify(procesoService).editar(procesoId, request);
    }

    @Test
    void eliminar_debeInvocarArchivar() {
        UUID procesoId = UUID.randomUUID();
        EliminarProcesoRequest request = new EliminarProcesoRequest();

        doNothing().when(procesoService).archivar(procesoId, request);

        assertDoesNotThrow(() ->
                procesoController.eliminar(procesoId, request)
        );

        verify(procesoService).archivar(procesoId, request);
    }
}