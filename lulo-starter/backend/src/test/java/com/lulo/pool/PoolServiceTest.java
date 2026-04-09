package com.lulo.pool;

import com.lulo.audit.AuditService;
import com.lulo.common.exception.ApiException;
import com.lulo.company.Empresa;
import com.lulo.company.EmpresaRepository;
import com.lulo.pool.dto.CrearPoolRequest;
import com.lulo.pool.dto.EditarPoolRequest;
import com.lulo.pool.dto.PoolResponse;
import com.lulo.rbac.Permiso;
import com.lulo.rbac.PermisoRepository;
import com.lulo.rbac.PoolPermissionService;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.RolPoolRepository;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolRepository;
import com.lulo.users.Usuario;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PoolService - Pruebas unitarias")
class PoolServiceTest {

    @Mock private PoolRepository           poolRepository;
    @Mock private EmpresaRepository        empresaRepository;
    @Mock private PermisoRepository        permisoRepository;
    @Mock private RolPoolRepository        rolPoolRepository;
    @Mock private UsuarioRolPoolRepository usuarioRolPoolRepository;
    @Mock private PoolPermissionService    poolPermissionService;
    @Mock private AuditService             auditService;

    @InjectMocks
    private PoolService poolService;

    private Empresa empresa;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        ReflectionTestUtils.setField(empresa, "id", 1);
        empresa.setNombre("Empresa Test");

        usuario = new Usuario();
        ReflectionTestUtils.setField(usuario, "id", 1);
        usuario.setEmpresa(empresa);
        usuario.setEmail("admin@test.com");
    }

    // ─── CREAR ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("crear: caso exitoso devuelve PoolResponse con nombre y empresa correctos")
    void crear_exitoso() {
        CrearPoolRequest request = new CrearPoolRequest(1, 1, "Desarrollo", null);

        Pool poolGuardado = new Pool();
        ReflectionTestUtils.setField(poolGuardado, "id", 10);
        poolGuardado.setEmpresa(empresa);
        poolGuardado.setNombre("Desarrollo");

        RolPool rolGuardado = new RolPool();
        ReflectionTestUtils.setField(rolGuardado, "id", 1);
        rolGuardado.setPool(poolGuardado);

        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(poolPermissionService.requireUsuarioDeEmpresa(1, 1)).thenReturn(usuario);
        doNothing().when(poolPermissionService).requirePermisoEnEmpresa(1, 1, "POOL_ADMINISTRAR");
        when(poolRepository.existsByNombreAndEmpresaId("Desarrollo", 1)).thenReturn(false);
        when(poolRepository.save(any())).thenReturn(poolGuardado);
        when(permisoRepository.findAll()).thenReturn(List.of(new Permiso()));
        when(rolPoolRepository.save(any())).thenReturn(rolGuardado);
        when(usuarioRolPoolRepository.save(any())).thenReturn(new UsuarioRolPool());
        doNothing().when(auditService).registrar(any(), any(), any(), any(), any(), any(), any());

        PoolResponse response = poolService.crear(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10);
        assertThat(response.getNombre()).isEqualTo("Desarrollo");
        assertThat(response.getEmpresaId()).isEqualTo(1);
        verify(poolRepository).save(any(Pool.class));
        verify(rolPoolRepository).save(any(RolPool.class));
    }

    @Test
    @DisplayName("crear: empresa no encontrada lanza ApiException 404")
    void crear_empresaNoEncontrada_lanzaNotFound() {
        CrearPoolRequest request = new CrearPoolRequest(99, 1, "Pool", null);
        when(empresaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> poolService.crear(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Empresa no encontrada")
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(poolRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear: nombre duplicado en la empresa lanza ApiException 409")
    void crear_nombreDuplicado_lanzaConflict() {
        CrearPoolRequest request = new CrearPoolRequest(1, 1, "Desarrollo", null);

        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(poolPermissionService.requireUsuarioDeEmpresa(1, 1)).thenReturn(usuario);
        doNothing().when(poolPermissionService).requirePermisoEnEmpresa(1, 1, "POOL_ADMINISTRAR");
        when(poolRepository.existsByNombreAndEmpresaId("Desarrollo", 1)).thenReturn(true);

        assertThatThrownBy(() -> poolService.crear(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Ya existe un pool con ese nombre")
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(poolRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear: nombre en blanco lanza ApiException 400")
    void crear_nombreBlanco_lanzaBadRequest() {
        CrearPoolRequest request = new CrearPoolRequest(1, 1, "   ", null);

        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(poolPermissionService.requireUsuarioDeEmpresa(1, 1)).thenReturn(usuario);
        doNothing().when(poolPermissionService).requirePermisoEnEmpresa(1, 1, "POOL_ADMINISTRAR");

        assertThatThrownBy(() -> poolService.crear(request))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(poolRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear: el rol administrador creado es propietario y tiene todos los permisos del catálogo")
    void crear_rolAdmin_esPropietarioConTodosLosPermisos() {
        CrearPoolRequest request = new CrearPoolRequest(1, 1, "QA", null);

        Pool poolGuardado = new Pool();
        ReflectionTestUtils.setField(poolGuardado, "id", 1);
        poolGuardado.setEmpresa(empresa);
        poolGuardado.setNombre("QA");

        Permiso p1 = new Permiso(); Permiso p2 = new Permiso();
        List<Permiso> permisos = List.of(p1, p2);

        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));
        when(poolPermissionService.requireUsuarioDeEmpresa(1, 1)).thenReturn(usuario);
        doNothing().when(poolPermissionService).requirePermisoEnEmpresa(any(), any(), any());
        when(poolRepository.existsByNombreAndEmpresaId(any(), any())).thenReturn(false);
        when(poolRepository.save(any())).thenReturn(poolGuardado);
        when(permisoRepository.findAll()).thenReturn(permisos);
        when(rolPoolRepository.save(any(RolPool.class))).thenAnswer(invocation -> {
            RolPool rol = invocation.getArgument(0);
            assertThat(rol.isEsPropietario()).isTrue();
            assertThat(rol.isActivo()).isTrue();
            assertThat(rol.getPermisos()).hasSize(2);
            ReflectionTestUtils.setField(rol, "id", 1);
            return rol;
        });
        when(usuarioRolPoolRepository.save(any())).thenReturn(new UsuarioRolPool());
        doNothing().when(auditService).registrar(any(), any(), any(), any(), any(), any(), any());

        poolService.crear(request);

        verify(rolPoolRepository).save(any(RolPool.class));
    }

    // ─── EDITAR ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("editar: caso exitoso actualiza nombre y retorna PoolResponse")
    void editar_exitoso() {
        Pool poolExistente = new Pool();
        ReflectionTestUtils.setField(poolExistente, "id", 1);
        poolExistente.setEmpresa(empresa);
        poolExistente.setNombre("Viejo Nombre");

        Pool poolActualizado = new Pool();
        ReflectionTestUtils.setField(poolActualizado, "id", 1);
        poolActualizado.setEmpresa(empresa);
        poolActualizado.setNombre("Nuevo Nombre");

        EditarPoolRequest request = new EditarPoolRequest(1, "Nuevo Nombre", null);

        when(poolRepository.findById(1)).thenReturn(Optional.of(poolExistente));
        when(poolPermissionService.requireUsuarioDeEmpresa(1, 1)).thenReturn(usuario);
        doNothing().when(poolPermissionService).requirePermisoEnPool(1, 1, "POOL_ADMINISTRAR");
        when(poolRepository.existsByNombreAndEmpresaId("Nuevo Nombre", 1)).thenReturn(false);
        when(poolRepository.save(any())).thenReturn(poolActualizado);
        doNothing().when(auditService).registrar(any(), any(), any(), any(), any(), any(), any());

        PoolResponse response = poolService.editar(1, request);

        assertThat(response.getNombre()).isEqualTo("Nuevo Nombre");
        verify(poolRepository).save(any(Pool.class));
    }

    @Test
    @DisplayName("editar: pool no encontrado lanza ApiException 404")
    void editar_poolNoEncontrado_lanzaNotFound() {
        EditarPoolRequest request = new EditarPoolRequest(1, "Nombre", null);
        when(poolRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> poolService.editar(99, request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Pool no encontrado")
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(poolRepository, never()).save(any());
    }

    @Test
    @DisplayName("editar: nombre duplicado en la empresa lanza ApiException 409")
    void editar_nombreDuplicado_lanzaConflict() {
        Pool poolExistente = new Pool();
        ReflectionTestUtils.setField(poolExistente, "id", 1);
        poolExistente.setEmpresa(empresa);
        poolExistente.setNombre("Viejo Nombre");

        EditarPoolRequest request = new EditarPoolRequest(1, "Nombre Duplicado", null);

        when(poolRepository.findById(1)).thenReturn(Optional.of(poolExistente));
        when(poolPermissionService.requireUsuarioDeEmpresa(1, 1)).thenReturn(usuario);
        doNothing().when(poolPermissionService).requirePermisoEnPool(any(), any(), any());
        when(poolRepository.existsByNombreAndEmpresaId("Nombre Duplicado", 1)).thenReturn(true);

        assertThatThrownBy(() -> poolService.editar(1, request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Ya existe un pool con ese nombre")
                .extracting(e -> ((ApiException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(poolRepository, never()).save(any());
    }
}
