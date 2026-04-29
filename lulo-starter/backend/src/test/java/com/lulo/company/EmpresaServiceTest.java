package com.lulo.company;

import com.lulo.common.exception.ApiException;
import com.lulo.company.dto.RegistroEmpresaRequest;
import com.lulo.company.dto.RegistroEmpresaResponse;
import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import com.lulo.rbac.Permiso;
import com.lulo.rbac.PermisoRepository;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.RolPoolRepository;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolRepository;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmpresaService - Pruebas unitarias")
class EmpresaServiceTest {

    // ── UUIDs de prueba ───────────────────────────────────────────────────────
    private static final UUID EMPRESA_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID USUARIO_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID POOL_ID    = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID ROL_ID     = UUID.fromString("00000000-0000-0000-0000-000000000004");

    @Mock private EmpresaRepository        empresaRepository;
    @Mock private UsuarioRepository        usuarioRepository;
    @Mock private PoolRepository           poolRepository;
    @Mock private PermisoRepository        permisoRepository;
    @Mock private RolPoolRepository        rolPoolRepository;
    @Mock private UsuarioRolPoolRepository usuarioRolPoolRepository;

    @InjectMocks
    private EmpresaService empresaService;

    private RegistroEmpresaRequest requestValido;

    @BeforeEach
    void setUp() {
        requestValido = new RegistroEmpresaRequest(
                "Empresa Demo SAS",
                "900111222-1",
                "contacto@demo.com",
                "admin@demo.com",
                "Admin1234!"
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CASO 1: Registro exitoso
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Registro exitoso: retorna response con los datos de empresa, usuario y pool")
    void registrar_exitoso() {
        // Arrange
        Empresa empresaGuardada = new Empresa();
        ReflectionTestUtils.setField(empresaGuardada, "id", EMPRESA_ID);
        empresaGuardada.setNombre("Empresa Demo SAS");
        empresaGuardada.setNit("900111222-1");

        Usuario adminGuardado = new Usuario();
        ReflectionTestUtils.setField(adminGuardado, "id", USUARIO_ID);
        adminGuardado.setEmail("admin@demo.com");

        Pool poolGuardado = new Pool();
        ReflectionTestUtils.setField(poolGuardado, "id", POOL_ID);
        poolGuardado.setNombre("Principal");

        RolPool rolGuardado = new RolPool();
        ReflectionTestUtils.setField(rolGuardado, "id", ROL_ID);

        when(empresaRepository.existsByNit("900111222-1")).thenReturn(false);
        when(usuarioRepository.existsByEmail("admin@demo.com")).thenReturn(false);
        when(empresaRepository.save(any())).thenReturn(empresaGuardada);
        when(usuarioRepository.save(any())).thenReturn(adminGuardado);
        when(poolRepository.save(any())).thenReturn(poolGuardado);
        when(permisoRepository.findAll()).thenReturn(List.of(new Permiso()));
        when(rolPoolRepository.save(any())).thenReturn(rolGuardado);
        when(usuarioRolPoolRepository.save(any())).thenReturn(new UsuarioRolPool());

        // Act
        RegistroEmpresaResponse response = empresaService.registrar(requestValido);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEmpresaId()).isEqualTo(EMPRESA_ID);
        assertThat(response.getEmpresaNombre()).isEqualTo("Empresa Demo SAS");
        assertThat(response.getUsuarioId()).isEqualTo(USUARIO_ID);
        assertThat(response.getEmailAdmin()).isEqualTo("admin@demo.com");
        assertThat(response.getPoolDefault()).isEqualTo("Principal");
        assertThat(response.getMensaje()).isEqualTo("Empresa registrada exitosamente");

        verify(empresaRepository).save(any());
        verify(usuarioRepository).save(any());
        verify(poolRepository).save(any());
        verify(rolPoolRepository).save(any());
        verify(usuarioRolPoolRepository).save(any());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CASO 2: NIT duplicado
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("NIT duplicado: lanza ApiException 409 CONFLICT")
    void registrar_nitDuplicado_lanzaApiException() {
        when(empresaRepository.existsByNit("900111222-1")).thenReturn(true);

        assertThatThrownBy(() -> empresaService.registrar(requestValido))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("900111222-1")
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(empresaRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CASO 3: Email de admin duplicado
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Email admin duplicado: lanza ApiException 409 CONFLICT")
    void registrar_emailAdminDuplicado_lanzaApiException() {
        when(empresaRepository.existsByNit("900111222-1")).thenReturn(false);
        when(usuarioRepository.existsByEmail("admin@demo.com")).thenReturn(true);

        assertThatThrownBy(() -> empresaService.registrar(requestValido))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("admin@demo.com")
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(empresaRepository, never()).save(any());
        verify(usuarioRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CASO 4: Pool por defecto se llama "Principal"
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Pool creado debe llamarse 'Principal'")
    void registrar_poolDefecto_nombrePrincipal() {
        Empresa e = new Empresa();
        ReflectionTestUtils.setField(e, "id", EMPRESA_ID);
        Usuario u = new Usuario();
        ReflectionTestUtils.setField(u, "id", USUARIO_ID);
        u.setEmail("admin@demo.com");
        Pool poolCapturado = new Pool();
        ReflectionTestUtils.setField(poolCapturado, "id", POOL_ID);
        poolCapturado.setNombre("Principal");
        RolPool rol = new RolPool();
        ReflectionTestUtils.setField(rol, "id", ROL_ID);

        when(empresaRepository.existsByNit(any())).thenReturn(false);
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(empresaRepository.save(any())).thenReturn(e);
        when(usuarioRepository.save(any())).thenReturn(u);
        when(poolRepository.save(any())).thenReturn(poolCapturado);
        when(permisoRepository.findAll()).thenReturn(List.of());
        when(rolPoolRepository.save(any())).thenReturn(rol);
        when(usuarioRolPoolRepository.save(any())).thenReturn(new UsuarioRolPool());

        RegistroEmpresaResponse response = empresaService.registrar(requestValido);

        assertThat(response.getPoolDefault()).isEqualTo("Principal");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CASO 5: Rol admin marcado como propietario con todos los permisos
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Rol administrador se crea como propietario con todos los permisos del catálogo")
    void registrar_rolAdmin_esPropietarioConTodosLosPermisos() {
        Empresa e = new Empresa();
        ReflectionTestUtils.setField(e, "id", EMPRESA_ID);
        Usuario u = new Usuario();
        ReflectionTestUtils.setField(u, "id", USUARIO_ID);
        u.setEmail("admin@demo.com");
        Pool pool = new Pool();
        ReflectionTestUtils.setField(pool, "id", POOL_ID);
        pool.setNombre("Principal");

        Permiso p1 = new Permiso(); Permiso p2 = new Permiso(); Permiso p3 = new Permiso();
        List<Permiso> permisos = List.of(p1, p2, p3);

        RolPool rolCapturado = new RolPool();
        ReflectionTestUtils.setField(rolCapturado, "id", ROL_ID);

        when(empresaRepository.existsByNit(any())).thenReturn(false);
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(empresaRepository.save(any())).thenReturn(e);
        when(usuarioRepository.save(any())).thenReturn(u);
        when(poolRepository.save(any())).thenReturn(pool);
        when(permisoRepository.findAll()).thenReturn(permisos);
        when(rolPoolRepository.save(any(RolPool.class))).thenAnswer(invocation -> {
            RolPool rol = invocation.getArgument(0);
            assertThat(rol.isEsPropietario()).isTrue();
            assertThat(rol.isActivo()).isTrue();
            assertThat(rol.getPermisos()).hasSize(3);
            ReflectionTestUtils.setField(rol, "id", ROL_ID);
            return rol;
        });
        when(usuarioRolPoolRepository.save(any())).thenReturn(new UsuarioRolPool());

        empresaService.registrar(requestValido);

        verify(rolPoolRepository).save(any(RolPool.class));
    }
}
