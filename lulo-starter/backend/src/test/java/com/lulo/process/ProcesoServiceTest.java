package com.lulo.process;

import com.lulo.audit.AuditService;
import com.lulo.company.Empresa;
import com.lulo.company.EmpresaRepository;
import com.lulo.diagram.DiagramService;
import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import com.lulo.process.dto.CrearProcesoRequest;
import com.lulo.process.dto.ProcesoResponse;
import com.lulo.rbac.PoolPermissionService;
import com.lulo.sharing.ProcesoCompartidoService;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcesoServiceTest {

    @Mock
    private ProcesoRepository procesoRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private PoolRepository poolRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private DiagramService diagramService;

    @Mock
    private PoolPermissionService poolPermissionService;

    @Mock
    private ProcesoCompartidoService procesoCompartidoService;

    @InjectMocks
    private ProcesoService procesoService;

    private Empresa empresa;
    private Pool pool;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(UUID.randomUUID());
        empresa.setNombre("Empresa Test");

        pool = new Pool();
        pool.setId(UUID.randomUUID());
        pool.setNombre("Pool Principal");
        pool.setEmpresa(empresa);

        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setEmail("admin@test.com");
        usuario.setEmpresa(empresa);
    }

    @Test
    void crear_debeCrearProcesoCorrectamente() {
        CrearProcesoRequest request = new CrearProcesoRequest();
        request.setEmpresaId(empresa.getId());
        request.setPoolId(pool.getId());
        request.setCreadoPorId(usuario.getId());
        request.setNombre("Proceso Test");
        request.setDescripcion("Descripción");
        request.setCategoria("Operativo");
        request.setEstado("borrador");

        when(empresaRepository.findById(empresa.getId()))
                .thenReturn(Optional.of(empresa));

        when(poolRepository.findById(pool.getId()))
                .thenReturn(Optional.of(pool));

        when(usuarioRepository.findById(usuario.getId()))
                .thenReturn(Optional.of(usuario));

        when(procesoRepository.save(any(Proceso.class)))
                .thenAnswer(invocation -> {
                    Proceso proceso = invocation.getArgument(0);
                    proceso.setId(UUID.randomUUID());
                    return proceso;
                });

        ProcesoResponse response = procesoService.crear(request);

        assertNotNull(response);
        assertEquals("Proceso Test", response.getNombre());
        assertEquals("Descripción", response.getDescripcion());
        assertEquals("Operativo", response.getCategoria());
        assertEquals("borrador", response.getEstado());
        assertEquals(empresa.getId(), response.getEmpresaId());
        assertEquals(pool.getId(), response.getPoolId());
        assertEquals(usuario.getId(), response.getCreadoPorId());

        verify(procesoRepository).save(any(Proceso.class));
    }

    @Test
    void toResponse_debeMapearCorrectamente() {
        Proceso proceso = new Proceso();
        proceso.setId(UUID.randomUUID());
        proceso.setEmpresa(empresa);
        proceso.setPool(pool);
        proceso.setCreatedByUser(usuario);
        proceso.setNombre("Proceso Mapeado");
        proceso.setDescripcion("Descripción");
        proceso.setCategoria("Administrativo");
        proceso.setEstado("publicado");
        proceso.setActivo(true);

        ProcesoResponse response = ProcesoService.toResponse(proceso);

        assertNotNull(response);
        assertEquals(proceso.getId(), response.getId());
        assertEquals(empresa.getId(), response.getEmpresaId());
        assertEquals("Empresa Test", response.getEmpresaNombre());
        assertEquals(pool.getId(), response.getPoolId());
        assertEquals("Pool Principal", response.getPoolNombre());
        assertEquals(usuario.getId(), response.getCreadoPorId());
        assertEquals("admin@test.com", response.getCreadoPorEmail());
        assertEquals("Proceso Mapeado", response.getNombre());
        assertEquals("Descripción", response.getDescripcion());
        assertEquals("Administrativo", response.getCategoria());
        assertEquals("publicado", response.getEstado());
        assertTrue(response.isActivo());
    }

    @Test
    void listar_cuandoNoHayProcesos_retornaPaginaVacia() {
        UUID empresaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        when(procesoRepository.findAll(any(Specification.class)))
                .thenReturn(List.of());

        when(procesoCompartidoService.findProcesosCompartidosVisibles(
                eq(empresaId),
                eq(usuarioId),
                isNull(),
                isNull(),
                isNull()
        )).thenReturn(List.of());

        var pageable = PageRequest.of(0, 10);

        var result = procesoService.listar(
                empresaId,
                usuarioId,
                null,
                null,
                null,
                pageable
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(poolPermissionService)
                .requireUsuarioDeEmpresa(usuarioId, empresaId);
    }
}
