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
import com.lulo.process.dto.ProcesoDetalleResponse;
import com.lulo.process.dto.ProcesoResponse;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcesoServiceTest {

    @Mock private ProcesoRepository  procesoRepository;
    @Mock private EmpresaRepository  empresaRepository;
    @Mock private PoolRepository     poolRepository;
    @Mock private UsuarioRepository  usuarioRepository;
    @Mock private AuditService       auditService;
    @Mock private DiagramService     diagramService;

    @InjectMocks private ProcesoService service;

    private Empresa empresa;
    private Pool pool;
    private Usuario usuario;
    private Proceso proceso;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1);
        empresa.setNombre("Demo SA");

        pool = new Pool();
        pool.setId(2);
        pool.setNombre("Principal");
        pool.setEmpresa(empresa);

        usuario = new Usuario();
        usuario.setId(3);
        usuario.setEmail("user@demo.com");
        usuario.setEmpresa(empresa);

        proceso = new Proceso();
        proceso.setId(10);
        proceso.setNombre("Proceso Alpha");
        proceso.setEstado("borrador");
        proceso.setActivo(true);
        proceso.setEmpresa(empresa);
        proceso.setPool(pool);
        proceso.setCreatedByUser(usuario);
    }

    // ── listar ───────────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void listar_sinFiltros_retornaPageVacia() {
        when(procesoRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<ProcesoResponse> result = service.listar(1, null, null, null, Pageable.unpaged());

        assertThat(result).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    void listar_conFiltros_aplicaTodosLosSpec() {
        when(procesoRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(proceso)));

        Page<ProcesoResponse> result = service.listar(1, "borrador", "legal", "Alpha", Pageable.unpaged());

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNombre()).isEqualTo("Proceso Alpha");
    }

    // ── obtener ──────────────────────────────────────────────────────────────

    @Test
    void obtener_exitoso_retornaDetalleConDiagrama() {
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(diagramService.getLanes(10)).thenReturn(List.of());
        when(diagramService.getNodos(10)).thenReturn(List.of());
        when(diagramService.getArcos(10)).thenReturn(List.of());

        ProcesoDetalleResponse response = service.obtener(10, 1);

        assertThat(response.getNombre()).isEqualTo("Proceso Alpha");
        assertThat(response.getLanes()).isEmpty();
    }

    @Test
    void obtener_procesoNoExiste_throwsNotFound() {
        when(procesoRepository.findByIdAndActivoTrue(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(99, 1))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void obtener_procesoDeOtraEmpresa_throwsForbidden() {
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));

        assertThatThrownBy(() -> service.obtener(10, 999))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    // ── crear ────────────────────────────────────────────────────────────────

    @Test
    void crear_exitoso_retornaProceso() {
        CrearProcesoRequest request = new CrearProcesoRequest(1, 2, 3, "Nuevo Proceso", null, null, null);

        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(poolRepository.findById(2)).thenReturn(Optional.of(pool));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(procesoRepository.save(any())).thenReturn(proceso);

        ProcesoResponse response = service.crear(request);

        assertThat(response.getNombre()).isEqualTo("Proceso Alpha");
        assertThat(response.getEstado()).isEqualTo("borrador");
    }

    @Test
    void crear_estadoNullUsaBorrador() {
        CrearProcesoRequest request = new CrearProcesoRequest(1, 2, 3, "Nuevo", null, null, null);
        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(poolRepository.findById(2)).thenReturn(Optional.of(pool));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(procesoRepository.save(any())).thenAnswer(inv -> {
            Proceso p = inv.getArgument(0);
            p.setId(10);
            return p;
        });

        ProcesoResponse response = service.crear(request);

        assertThat(response.getEstado()).isEqualTo("borrador");
    }

    @Test
    void crear_empresaNoEncontrada_throwsNotFound() {
        CrearProcesoRequest request = new CrearProcesoRequest(99, 2, 3, "X", null, null, null);
        when(empresaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void crear_poolDeOtraEmpresa_throwsForbidden() {
        Empresa otraEmpresa = new Empresa();
        otraEmpresa.setId(99);
        Pool poolAjeno = new Pool();
        poolAjeno.setId(2);
        poolAjeno.setEmpresa(otraEmpresa);

        CrearProcesoRequest request = new CrearProcesoRequest(1, 2, 3, "X", null, null, null);
        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(poolRepository.findById(2)).thenReturn(Optional.of(poolAjeno));

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void crear_usuarioDeOtraEmpresa_throwsForbidden() {
        Empresa otraEmpresa = new Empresa();
        otraEmpresa.setId(99);
        Usuario usuarioAjeno = new Usuario();
        usuarioAjeno.setId(3);
        usuarioAjeno.setEmpresa(otraEmpresa);

        CrearProcesoRequest request = new CrearProcesoRequest(1, 2, 3, "X", null, null, null);
        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(poolRepository.findById(2)).thenReturn(Optional.of(pool));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuarioAjeno));

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    // ── editar ───────────────────────────────────────────────────────────────

    @Test
    void editar_exitoso_actualizaCamposPresentes() {
        EditarProcesoRequest request = new EditarProcesoRequest(3, "Nombre Nuevo", null, "rrhh", null);
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(procesoRepository.saveAndFlush(any())).thenReturn(proceso);

        ProcesoResponse response = service.editar(10, request);

        assertThat(response).isNotNull();
        verify(auditService).registrar(any(), any(), eq("PROCESO"), eq(10), eq("EDITAR"), any(), any());
    }

    @Test
    void editar_procesoNoEncontrado_throwsNotFound() {
        when(procesoRepository.findByIdAndActivoTrue(99)).thenReturn(Optional.empty());

        var req = new EditarProcesoRequest(3, null, null, null, null);
        assertThatThrownBy(() -> service.editar(99, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void editar_usuarioDeOtraEmpresa_throwsForbidden() {
        Empresa otraEmpresa = new Empresa();
        otraEmpresa.setId(99);
        Usuario usuarioAjeno = new Usuario();
        usuarioAjeno.setId(3);
        usuarioAjeno.setEmpresa(otraEmpresa);

        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuarioAjeno));

        var req = new EditarProcesoRequest(3, null, null, null, null);
        assertThatThrownBy(() -> service.editar(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    // ── archivar ─────────────────────────────────────────────────────────────

    @Test
    void archivar_exitoso_desactivaProceso() {
        EliminarProcesoRequest request = new EliminarProcesoRequest(3, true);
        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(procesoRepository.save(any())).thenReturn(proceso);

        service.archivar(10, request);

        assertThat(proceso.isActivo()).isFalse();
        verify(auditService).registrar(any(), any(), eq("PROCESO"), eq(10), eq("ARCHIVAR"), any(), any());
    }

    @Test
    void archivar_procesoNoEncontrado_throwsNotFound() {
        when(procesoRepository.findByIdAndActivoTrue(99)).thenReturn(Optional.empty());

        var req = new EliminarProcesoRequest(3, true);
        assertThatThrownBy(() -> service.archivar(99, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void archivar_usuarioDeOtraEmpresa_throwsForbidden() {
        Empresa otraEmpresa = new Empresa();
        otraEmpresa.setId(99);
        Usuario usuarioAjeno = new Usuario();
        usuarioAjeno.setId(3);
        usuarioAjeno.setEmpresa(otraEmpresa);

        when(procesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(proceso));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuarioAjeno));

        var req = new EliminarProcesoRequest(3, true);
        assertThatThrownBy(() -> service.archivar(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }
}
