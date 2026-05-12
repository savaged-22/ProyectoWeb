package com.lulo.company;

import com.lulo.company.dto.RegistroEmpresaRequest;
import com.lulo.company.dto.RegistroEmpresaResponse;
import com.lulo.common.exception.ApiException;
import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import com.lulo.rbac.*;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PoolRepository poolRepository;

    @Mock
    private PermisoRepository permisoRepository;

    @Mock
    private RolPoolRepository rolPoolRepository;

    @Mock
    private UsuarioRolPoolRepository usuarioRolPoolRepository;

    @InjectMocks
    private EmpresaService empresaService;

    private RegistroEmpresaRequest request;

    @BeforeEach
    void setUp() {
        request = new RegistroEmpresaRequest();
        request.setNombreEmpresa("Mi Empresa");
        request.setNit("900123456");
        request.setEmailContacto("contacto@empresa.com");
        request.setEmailAdmin("admin@empresa.com");
        request.setPassword("123456");
    }

    @Test
    void registrar_exitoso() {
        UUID empresaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID rolId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNombre("Mi Empresa");

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail("admin@empresa.com");
        usuario.setEmpresa(empresa);

        Pool pool = new Pool();
        pool.setNombre("Principal");

        Permiso permiso = new Permiso();

        RolPool rolPool = new RolPool();
        rolPool.setId(rolId);

        when(empresaRepository.existsByNit(request.getNit())).thenReturn(false);
        when(usuarioRepository.existsByEmail(request.getEmailAdmin())).thenReturn(false);
        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresa);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(poolRepository.save(any(Pool.class))).thenReturn(pool);
        when(permisoRepository.findAll()).thenReturn(List.of(permiso));
        when(rolPoolRepository.save(any(RolPool.class))).thenReturn(rolPool);

        RegistroEmpresaResponse response = empresaService.registrar(request);

        assertNotNull(response);
        assertEquals(empresaId, response.getEmpresaId());
        assertEquals("Mi Empresa", response.getEmpresaNombre());
        assertEquals(usuarioId, response.getUsuarioId());
        assertEquals("admin@empresa.com", response.getEmailAdmin());
        assertEquals("Principal", response.getPoolDefault());
        assertEquals("Empresa registrada exitosamente", response.getMensaje());

        verify(usuarioRolPoolRepository).save(any(UsuarioRolPool.class));
    }

    @Test
    void registrar_nitDuplicado_lanzaApiException() {
        when(empresaRepository.existsByNit(request.getNit())).thenReturn(true);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> empresaService.registrar(request)
        );

        assertTrue(exception.getMessage().contains("Ya existe una empresa registrada"));
    }

    @Test
    void registrar_emailDuplicado_lanzaApiException() {
        when(empresaRepository.existsByNit(request.getNit())).thenReturn(false);
        when(usuarioRepository.existsByEmail(request.getEmailAdmin())).thenReturn(true);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> empresaService.registrar(request)
        );

        assertTrue(exception.getMessage().contains("El correo de administrador ya está en uso"));
    }
}