package com.lulo.process;

import com.lulo.audit.AuditService;
import com.lulo.common.exception.ApiException;
import com.lulo.company.Empresa;
import com.lulo.company.EmpresaRepository;
import com.lulo.diagram.DiagramService;
import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import com.lulo.process.dto.CrearProcesoRequest;
import com.lulo.process.dto.EditarProcesoRequest;
import com.lulo.process.dto.EliminarProcesoRequest;
import com.lulo.process.dto.ProcesoResponse;
import com.lulo.rbac.PoolPermissionService;
import com.lulo.sharing.ProcesoCompartidoService;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcesoService - Pruebas unitarias")
class ProcesoServiceTest {
    private static final UUID EMPRESA_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTRA_EMPRESA_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Mock private ProcesoRepository          procesoRepository;
    @Mock private EmpresaRepository          empresaRepository;
    @Mock private PoolRepository             poolRepository;
    @Mock private UsuarioRepository          usuarioRepository;
    @Mock private AuditService               auditService;
    @Mock private DiagramService             diagramService;
    @Mock private PoolPermissionService      poolPermissionService;
    @Mock private ProcesoCompartidoService   procesoCompartidoService;

    @InjectMocks
    private ProcesoService procesoService;

    private Empresa empresa;
    private Pool    pool;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        ReflectionTestUtils.setField(empresa, "id", EMPRESA_ID);
        empresa.setNombre("Empresa Test");

        pool = new Pool();
        ReflectionTestUtils.setField(pool, "id", 1);
        pool.setEmpresa(empresa);
        pool.setNombre("Pool Test");

        usuario = new Usuario();
        ReflectionTestUtils.setField(usuario, "id", 1);
        usuario.setEmpresa(empresa);
        usuario.setEmail("user@test.com");
    }

    // ─── CREAR ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("crear: caso exitoso devuelve ProcesoResponse con datos correctos")
    void crear_exitoso() {
        CrearProcesoRequest request = new CrearProcesoRequest(EMPRESA_ID, 1, 1, "Mi Proceso", "Descripción", "categoria", "borrador");

        Proceso procesoGuardado = buildProceso(10, "Mi Proceso", "borrador");

        when(empresaRepository.findById(EMPRESA_ID)).thenReturn(Optional.of(empresa));
        when(poolRepository.findById(1)).thenReturn(Optional.of(pool));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        doNothing().when(poolPermissionService).requirePermisoEnPool(1, 1, "PROCESO_CREAR");
        when(procesoRepository.save(any())).thenReturn(procesoGuardado);

        ProcesoResponse response = procesoService.crear(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10);
        assertThat(response.getNombre()).isEqualTo("Mi Proceso");
        assertThat(response.getEstado()).isEqualTo("borrador");
        verify(procesoRepository).save(any(Proceso.class));
    }

    @Test
    @DisplayName("crear: empresa no encontrada lanza ApiException 404")
    void crear_empresaNoEncontrada_lanzaNotFound() {
        UUID empresaInexistente = UUID.fromString("99999999-9999-9999-9999-999999999999");
        CrearProcesoRequest request = new CrearProcesoRequest(empresaInexistente, 1, 1, "Proceso", null, null, null);
        when(empresaRepository.findById(empresaInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> procesoService.crear(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Empresa no encontrada")
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(procesoRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear: pool no encontrado lanza ApiException 404")
    void crear_poolNoEncontrado_lanzaNotFound() {
        CrearProcesoRequest request = new CrearProcesoRequest(EMPRESA_ID, 99, 1, "Proceso", null, null, null);
        when(empresaRepository.findById(EMPRESA_ID)).thenReturn(Optional.of(empresa));
        when(poolRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> procesoService.crear(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Pool no encontrado")
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(procesoRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear: pool de otra empresa lanza ApiException 403")
    void crear_poolDeOtraEmpresa_lanzaForbidden() {
        Empresa otraEmpresa = new Empresa();
        ReflectionTestUtils.setField(otraEmpresa, "id", OTRA_EMPRESA_ID);
        Pool poolAjeno = new Pool();
        poolAjeno.setEmpresa(otraEmpresa);
        ReflectionTestUtils.setField(poolAjeno, "id", 5);

        CrearProcesoRequest request = new CrearProcesoRequest(EMPRESA_ID, 5, 1, "Proceso", null, null, null);
        when(empresaRepository.findById(EMPRESA_ID)).thenReturn(Optional.of(empresa));
        when(poolRepository.findById(5)).thenReturn(Optional.of(poolAjeno));

        assertThatThrownBy(() -> procesoService.crear(request))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(procesoRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear: usuario de otra empresa lanza ApiException 403")
    void crear_usuarioDeOtraEmpresa_lanzaForbidden() {
        Empresa otraEmpresa = new Empresa();
        ReflectionTestUtils.setField(otraEmpresa, "id", OTRA_EMPRESA_ID);
        Usuario usuarioAjeno = new Usuario();
        usuarioAjeno.setEmpresa(otraEmpresa);
        ReflectionTestUtils.setField(usuarioAjeno, "id", 5);

        CrearProcesoRequest request = new CrearProcesoRequest(EMPRESA_ID, 1, 5, "Proceso", null, null, null);
        when(empresaRepository.findById(EMPRESA_ID)).thenReturn(Optional.of(empresa));
        when(poolRepository.findById(1)).thenReturn(Optional.of(pool));
        when(usuarioRepository.findById(5)).thenReturn(Optional.of(usuarioAjeno));

        assertThatThrownBy(() -> procesoService.crear(request))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(procesoRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear: estado por defecto es 'borrador' cuando no se especifica")
    void crear_estadoDefecto_esBorrador() {
        CrearProcesoRequest request = new CrearProcesoRequest(EMPRESA_ID, 1, 1, "Proceso", null, null, null);
        Proceso guardado = buildProceso(1, "Proceso", "borrador");

        when(empresaRepository.findById(EMPRESA_ID)).thenReturn(Optional.of(empresa));
        when(poolRepository.findById(1)).thenReturn(Optional.of(pool));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        doNothing().when(poolPermissionService).requirePermisoEnPool(any(), any(), any());
        when(procesoRepository.save(any())).thenReturn(guardado);

        ProcesoResponse response = procesoService.crear(request);

        assertThat(response.getEstado()).isEqualTo("borrador");
    }

    // ─── EDITAR ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("editar: proceso no encontrado lanza ApiException 404")
    void editar_procesoNoEncontrado_lanzaNotFound() {
        EditarProcesoRequest request = new EditarProcesoRequest(1, "Nuevo Nombre", null, null, null);
        when(procesoRepository.findByIdAndActivoTrue(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> procesoService.editar(99, request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Proceso no encontrado")
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("editar: usuario no encontrado lanza ApiException 404")
    void editar_usuarioNoEncontrado_lanzaNotFound() {
        Proceso proceso = buildProceso(1, "Proceso", "borrador");
        EditarProcesoRequest request = new EditarProcesoRequest(99, "Nuevo Nombre", null, null, null);

        when(procesoRepository.findByIdAndActivoTrue(1)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> procesoService.editar(1, request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Usuario no encontrado")
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ─── ARCHIVAR ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("archivar: soft delete pone activo=false y guarda en repositorio")
    void archivar_exitoso_procesoQuedaInactivo() {
        Proceso proceso = buildProceso(1, "Proceso", "borrador");
        EliminarProcesoRequest request = new EliminarProcesoRequest(1, true);

        when(procesoRepository.findByIdAndActivoTrue(1)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        doNothing().when(poolPermissionService).requirePermisoEnPool(any(), any(), any());
        when(procesoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(auditService).registrar(any(), any(), any(), any(), any(), any(), any());

        procesoService.archivar(1, request);

        assertThat(proceso.isActivo()).isFalse();
        verify(procesoRepository).save(proceso);
        verify(auditService).registrar(any(), any(), eq("PROCESO"), eq(1), eq("ARCHIVAR"), any(), any());
    }

    @Test
    @DisplayName("archivar: proceso no encontrado lanza ApiException 404")
    void archivar_procesoNoEncontrado_lanzaNotFound() {
        EliminarProcesoRequest request = new EliminarProcesoRequest(1, true);
        when(procesoRepository.findByIdAndActivoTrue(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> procesoService.archivar(99, request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Proceso no encontrado")
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(procesoRepository, never()).save(any());
    }

    @Test
    @DisplayName("archivar: usuario de otra empresa lanza ApiException 403")
    void archivar_usuarioOtraEmpresa_lanzaForbidden() {
        Empresa otraEmpresa = new Empresa();
        ReflectionTestUtils.setField(otraEmpresa, "id", OTRA_EMPRESA_ID);
        Usuario usuarioAjeno = new Usuario();
        usuarioAjeno.setEmpresa(otraEmpresa);
        ReflectionTestUtils.setField(usuarioAjeno, "id", 5);

        Proceso proceso = buildProceso(1, "Proceso", "borrador");
        EliminarProcesoRequest request = new EliminarProcesoRequest(5, true);

        when(procesoRepository.findByIdAndActivoTrue(1)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(5)).thenReturn(Optional.of(usuarioAjeno));

        assertThatThrownBy(() -> procesoService.archivar(1, request))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(procesoRepository, never()).save(any());
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private Proceso buildProceso(Integer id, String nombre, String estado) {
        Proceso p = new Proceso();
        ReflectionTestUtils.setField(p, "id", id);
        p.setEmpresa(empresa);
        p.setPool(pool);
        p.setCreatedByUser(usuario);
        p.setNombre(nombre);
        p.setEstado(estado);
        p.setActivo(true);
        return p;
    }
}
