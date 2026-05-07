package com.lulo.rbac;

import com.lulo.audit.AuditService;
import com.lulo.common.exception.ApiException;
import com.lulo.company.Empresa;
import com.lulo.company.EmpresaRepository;
import com.lulo.rbac.dto.CrearRolProcesoRequest;
import com.lulo.rbac.dto.EditarRolProcesoRequest;
import com.lulo.rbac.dto.EliminarRolProcesoRequest;
import com.lulo.rbac.dto.RolProcesoResponse;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolProcesoServiceTest {

    @Mock private RolProcesoRepository rolProcesoRepository;
    @Mock private EmpresaRepository    empresaRepository;
    @Mock private UsuarioRepository    usuarioRepository;
    @Mock private AuditService         auditService;

    @InjectMocks private RolProcesoService service;

    private Empresa empresa;
    private Usuario usuario;
    private RolProceso rol;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1);
        empresa.setNombre("Demo SA");

        usuario = new Usuario();
        usuario.setId(3);
        usuario.setEmail("user@demo.com");
        usuario.setEmpresa(empresa);

        rol = new RolProceso();
        rol.setId(10);
        rol.setNombre("Revisor");
        rol.setEmpresa(empresa);
        rol.setActivo(true);
    }

    // ── crear ────────────────────────────────────────────────────────────────

    @Test
    void crear_exitoso_retornaRol() {
        CrearRolProcesoRequest request = new CrearRolProcesoRequest(1, 3, "Revisor", "Revisa procesos");

        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(rolProcesoRepository.existsByEmpresaIdAndNombreAndActivoTrue(1, "Revisor")).thenReturn(false);
        when(rolProcesoRepository.save(any())).thenReturn(rol);

        RolProcesoResponse response = service.crear(request);

        assertThat(response.getNombre()).isEqualTo("Revisor");
        verify(auditService).registrar(any(), any(), eq("ROL_PROCESO"), eq(10), eq("CREAR"), eq(null), any());
    }

    @Test
    void crear_nombreNormalizado_sinEspacios() {
        CrearRolProcesoRequest request = new CrearRolProcesoRequest(1, 3, "  Revisor  ", null);

        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(rolProcesoRepository.existsByEmpresaIdAndNombreAndActivoTrue(1, "Revisor")).thenReturn(false);
        when(rolProcesoRepository.save(any())).thenReturn(rol);

        RolProcesoResponse response = service.crear(request);

        assertThat(response).isNotNull();
    }

    @Test
    void crear_empresaNoEncontrada_throwsNotFound() {
        when(empresaRepository.findById(99)).thenReturn(Optional.empty());

        var req = new CrearRolProcesoRequest(99, 3, "X", null);
        assertThatThrownBy(() -> service.crear(req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void crear_usuarioDeOtraEmpresa_throwsForbidden() {
        Empresa otraEmpresa = new Empresa();
        otraEmpresa.setId(99);
        Usuario ajeno = new Usuario();
        ajeno.setId(3);
        ajeno.setEmpresa(otraEmpresa);

        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(ajeno));

        var req = new CrearRolProcesoRequest(1, 3, "X", null);
        assertThatThrownBy(() -> service.crear(req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void crear_nombreDuplicado_throwsConflict() {
        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(rolProcesoRepository.existsByEmpresaIdAndNombreAndActivoTrue(1, "Revisor")).thenReturn(true);

        var req = new CrearRolProcesoRequest(1, 3, "Revisor", null);
        assertThatThrownBy(() -> service.crear(req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void crear_nombreVacio_throwsBadRequest() {
        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));

        var req = new CrearRolProcesoRequest(1, 3, "   ", null);
        assertThatThrownBy(() -> service.crear(req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    // ── editar ───────────────────────────────────────────────────────────────

    @Test
    void editar_exitoso_actualizaNombre() {
        when(rolProcesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(rol));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(rolProcesoRepository.existsActivoByEmpresaIdAndNombreExcluyendoId(1, "Aprobador", 10)).thenReturn(false);
        when(rolProcesoRepository.save(any())).thenReturn(rol);

        RolProcesoResponse response = service.editar(10, new EditarRolProcesoRequest(3, "Aprobador", null));

        assertThat(response).isNotNull();
        verify(auditService).registrar(any(), any(), eq("ROL_PROCESO"), eq(10), eq("EDITAR"), any(), any());
    }

    @Test
    void editar_rolNoEncontrado_throwsNotFound() {
        when(rolProcesoRepository.findByIdAndActivoTrue(99)).thenReturn(Optional.empty());

        var req = new EditarRolProcesoRequest(3, "X", null);
        assertThatThrownBy(() -> service.editar(99, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void editar_nombreDuplicadoEnOtroRol_throwsConflict() {
        when(rolProcesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(rol));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(rolProcesoRepository.existsActivoByEmpresaIdAndNombreExcluyendoId(1, "Aprobador", 10)).thenReturn(true);

        var req = new EditarRolProcesoRequest(3, "Aprobador", null);
        assertThatThrownBy(() -> service.editar(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    // ── eliminar ─────────────────────────────────────────────────────────────

    @Test
    void eliminar_exitoso_desactivaRol() {
        when(rolProcesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(rol));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(rolProcesoRepository.existsEnLane(10)).thenReturn(false);
        when(rolProcesoRepository.save(any())).thenReturn(rol);

        service.eliminar(10, new EliminarRolProcesoRequest(3, true));

        assertThat(rol.isActivo()).isFalse();
        verify(auditService).registrar(any(), any(), eq("ROL_PROCESO"), eq(10), eq("ELIMINAR"), any(), any());
    }

    @Test
    void eliminar_rolNoEncontrado_throwsNotFound() {
        when(rolProcesoRepository.findByIdAndActivoTrue(99)).thenReturn(Optional.empty());

        var req = new EliminarRolProcesoRequest(3, true);
        assertThatThrownBy(() -> service.eliminar(99, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void eliminar_rolAsignadoALane_throwsConflict() {
        when(rolProcesoRepository.findByIdAndActivoTrue(10)).thenReturn(Optional.of(rol));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(rolProcesoRepository.existsEnLane(10)).thenReturn(true);

        var req = new EliminarRolProcesoRequest(3, true);
        assertThatThrownBy(() -> service.eliminar(10, req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }
}
