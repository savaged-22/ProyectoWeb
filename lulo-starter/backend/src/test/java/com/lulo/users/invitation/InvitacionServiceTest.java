package com.lulo.users.invitation;

import com.lulo.common.exception.ApiException;
import com.lulo.company.Empresa;
import com.lulo.company.EmpresaRepository;
import com.lulo.pool.Pool;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.RolPoolRepository;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolRepository;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import com.lulo.users.invitation.dto.AceptarInvitacionRequest;
import com.lulo.users.invitation.dto.AceptarInvitacionResponse;
import com.lulo.users.invitation.dto.InvitarUsuarioRequest;
import com.lulo.users.invitation.dto.InvitarUsuarioResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvitacionServiceTest {

    @Mock private InvitacionUsuarioRepository invitacionRepository;
    @Mock private EmpresaRepository           empresaRepository;
    @Mock private UsuarioRepository           usuarioRepository;
    @Mock private RolPoolRepository           rolPoolRepository;
    @Mock private UsuarioRolPoolRepository    usuarioRolPoolRepository;

    @InjectMocks private InvitacionService service;

    private Empresa empresa;
    private Pool pool;
    private RolPool rolPool;
    private Usuario invitador;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1);
        empresa.setNombre("Demo SA");

        pool = new Pool();
        pool.setId(2);
        pool.setEmpresa(empresa);

        rolPool = new RolPool();
        rolPool.setId(5);
        rolPool.setNombre("Analista");
        rolPool.setPool(pool);

        invitador = new Usuario();
        invitador.setId(3);
        invitador.setEmail("jefe@demo.com");
        invitador.setEmpresa(empresa);
    }

    // ── invitar ──────────────────────────────────────────────────────────────

    @Test
    void invitar_exitoso_creaInvitacion() {
        InvitarUsuarioRequest request = new InvitarUsuarioRequest(1, 5, 3, "nuevo@demo.com");

        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(rolPoolRepository.findById(5)).thenReturn(Optional.of(rolPool));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(invitador));
        when(invitacionRepository.existsByEmailAndEstado("nuevo@demo.com", "pendiente")).thenReturn(false);
        when(usuarioRepository.existsByEmail("nuevo@demo.com")).thenReturn(false);

        InvitacionUsuario invitacion = new InvitacionUsuario();
        invitacion.setId(20);
        invitacion.setEmail("nuevo@demo.com");
        invitacion.setEmpresa(empresa);
        invitacion.setRolPool(rolPool);
        invitacion.setTokenHash("token-abc");
        invitacion.setExpiresAt(LocalDateTime.now().plusHours(72));
        when(invitacionRepository.save(any())).thenReturn(invitacion);

        InvitarUsuarioResponse response = service.invitar(request);

        assertThat(response.getEmailInvitado()).isEqualTo("nuevo@demo.com");
        assertThat(response.getRolAsignado()).isEqualTo("Analista");
        assertThat(response.getToken()).isNotBlank();
    }

    @Test
    void invitar_empresaNoEncontrada_throwsNotFound() {
        when(empresaRepository.findById(99)).thenReturn(Optional.empty());

        var req = new InvitarUsuarioRequest(99, 5, 3, "x@demo.com");
        assertThatThrownBy(() -> service.invitar(req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void invitar_rolDeOtraEmpresa_throwsForbidden() {
        Empresa otraEmpresa = new Empresa();
        otraEmpresa.setId(99);
        Pool poolAjeno = new Pool();
        poolAjeno.setEmpresa(otraEmpresa);
        RolPool rolAjeno = new RolPool();
        rolAjeno.setPool(poolAjeno);

        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(rolPoolRepository.findById(5)).thenReturn(Optional.of(rolAjeno));

        var req = new InvitarUsuarioRequest(1, 5, 3, "x@demo.com");
        assertThatThrownBy(() -> service.invitar(req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void invitar_invitacionPendienteExistente_throwsConflict() {
        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(rolPoolRepository.findById(5)).thenReturn(Optional.of(rolPool));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(invitador));
        when(invitacionRepository.existsByEmailAndEstado("nuevo@demo.com", "pendiente")).thenReturn(true);

        var req1 = new InvitarUsuarioRequest(1, 5, 3, "nuevo@demo.com");
        assertThatThrownBy(() -> service.invitar(req1))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void invitar_emailYaRegistrado_throwsConflict() {
        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(rolPoolRepository.findById(5)).thenReturn(Optional.of(rolPool));
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(invitador));
        when(invitacionRepository.existsByEmailAndEstado(any(), any())).thenReturn(false);
        when(usuarioRepository.existsByEmail("nuevo@demo.com")).thenReturn(true);

        var req2 = new InvitarUsuarioRequest(1, 5, 3, "nuevo@demo.com");
        assertThatThrownBy(() -> service.invitar(req2))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    // ── aceptar ──────────────────────────────────────────────────────────────

    @Test
    void aceptar_exitoso_creaUsuarioYAsignaRol() {
        InvitacionUsuario invitacion = new InvitacionUsuario();
        invitacion.setId(20);
        invitacion.setEmail("nuevo@demo.com");
        invitacion.setEstado("pendiente");
        invitacion.setExpiresAt(LocalDateTime.now().plusHours(24));
        invitacion.setEmpresa(empresa);
        invitacion.setRolPool(rolPool);

        when(invitacionRepository.findByTokenHash("token-abc")).thenReturn(Optional.of(invitacion));
        when(usuarioRepository.existsByEmail("nuevo@demo.com")).thenReturn(false);

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setId(50);
        nuevoUsuario.setEmail("nuevo@demo.com");
        nuevoUsuario.setEmpresa(empresa);
        when(usuarioRepository.save(any())).thenReturn(nuevoUsuario);
        when(usuarioRolPoolRepository.save(any())).thenReturn(new UsuarioRolPool());
        when(invitacionRepository.save(any())).thenReturn(invitacion);

        AceptarInvitacionResponse response = service.aceptar("token-abc", new AceptarInvitacionRequest("clave123"));

        assertThat(response.getEmail()).isEqualTo("nuevo@demo.com");
        assertThat(response.getRolAsignado()).isEqualTo("Analista");
        assertThat(invitacion.getEstado()).isEqualTo("aceptada");
    }

    @Test
    void aceptar_tokenInvalido_throwsNotFound() {
        when(invitacionRepository.findByTokenHash("token-falso")).thenReturn(Optional.empty());

        var req = new AceptarInvitacionRequest("clave123");
        assertThatThrownBy(() -> service.aceptar("token-falso", req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void aceptar_invitacionYaUsada_throwsConflict() {
        InvitacionUsuario invitacion = new InvitacionUsuario();
        invitacion.setEstado("aceptada");

        when(invitacionRepository.findByTokenHash("token-abc")).thenReturn(Optional.of(invitacion));

        var req = new AceptarInvitacionRequest("clave123");
        assertThatThrownBy(() -> service.aceptar("token-abc", req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void aceptar_invitacionExpirada_marcaExpiradaYThrowsGone() {
        InvitacionUsuario invitacion = new InvitacionUsuario();
        invitacion.setEstado("pendiente");
        invitacion.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(invitacionRepository.findByTokenHash("token-abc")).thenReturn(Optional.of(invitacion));
        when(invitacionRepository.save(any())).thenReturn(invitacion);

        var req = new AceptarInvitacionRequest("clave123");
        assertThatThrownBy(() -> service.aceptar("token-abc", req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.GONE));

        assertThat(invitacion.getEstado()).isEqualTo("expirada");
    }

    @Test
    void aceptar_emailYaRegistrado_throwsConflict() {
        InvitacionUsuario invitacion = new InvitacionUsuario();
        invitacion.setEstado("pendiente");
        invitacion.setExpiresAt(LocalDateTime.now().plusHours(24));
        invitacion.setEmail("nuevo@demo.com");

        when(invitacionRepository.findByTokenHash("token-abc")).thenReturn(Optional.of(invitacion));
        when(usuarioRepository.existsByEmail("nuevo@demo.com")).thenReturn(true);

        var req = new AceptarInvitacionRequest("clave123");
        assertThatThrownBy(() -> service.aceptar("token-abc", req))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }
}
