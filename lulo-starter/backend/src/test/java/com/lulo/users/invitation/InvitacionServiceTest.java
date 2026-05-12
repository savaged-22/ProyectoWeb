package com.lulo.users.invitation;

import com.lulo.company.Empresa;
import com.lulo.company.EmpresaRepository;
import com.lulo.pool.Pool;
import com.lulo.rbac.PoolPermissionService;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.RolPoolRepository;
import com.lulo.rbac.UsuarioRolPoolRepository;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import com.lulo.users.invitation.dto.AceptarInvitacionRequest;
import com.lulo.users.invitation.dto.AceptarInvitacionResponse;
import com.lulo.users.invitation.dto.InvitarUsuarioRequest;
import com.lulo.users.invitation.dto.InvitarUsuarioResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitacionServiceTest {

    @Mock
    private InvitacionUsuarioRepository invitacionRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolPoolRepository rolPoolRepository;

    @Mock
    private UsuarioRolPoolRepository usuarioRolPoolRepository;

    @Mock
    private PoolPermissionService poolPermissionService;

    @InjectMocks
    private InvitacionService invitacionService;

    @Test
    void invitar_debeCrearInvitacionYRetornarResponse() {
        UUID empresaId = UUID.randomUUID();
        UUID rolPoolId = UUID.randomUUID();
        UUID invitadoPorId = UUID.randomUUID();

        InvitarUsuarioRequest request = new InvitarUsuarioRequest();
        request.setEmpresaId(empresaId);
        request.setRolPoolId(rolPoolId);
        request.setInvitadoPorId(invitadoPorId);
        request.setEmailInvitado("nuevo@test.com");

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNombre("Empresa Test");

        Pool pool = new Pool();
        pool.setId(UUID.randomUUID());
        pool.setEmpresa(empresa);

        RolPool rolPool = new RolPool();
        rolPool.setId(rolPoolId);
        rolPool.setNombre("Editor");
        rolPool.setPool(pool);

        Usuario invitadoPor = new Usuario();
        invitadoPor.setId(invitadoPorId);
        invitadoPor.setEmpresa(empresa);

        InvitacionUsuario invitacion = new InvitacionUsuario();
        invitacion.setId(UUID.randomUUID());
        invitacion.setEmpresa(empresa);
        invitacion.setRolPool(rolPool);
        invitacion.setCreatedByUser(invitadoPor);
        invitacion.setEmail("nuevo@test.com");
        invitacion.setTokenHash("token-test");
        invitacion.setEstado("pendiente");
        invitacion.setExpiresAt(LocalDateTime.now().plusHours(72));

        when(empresaRepository.findById(empresaId))
                .thenReturn(Optional.of(empresa));

        when(rolPoolRepository.findById(rolPoolId))
                .thenReturn(Optional.of(rolPool));

        when(usuarioRepository.findById(invitadoPorId))
                .thenReturn(Optional.of(invitadoPor));

        when(invitacionRepository.existsByEmailAndEstado(
                "nuevo@test.com", "pendiente"))
                .thenReturn(false);

        when(usuarioRepository.existsByEmail("nuevo@test.com"))
                .thenReturn(false);

        when(invitacionRepository.save(any(InvitacionUsuario.class)))
                .thenReturn(invitacion);

        InvitarUsuarioResponse response =
                invitacionService.invitar(request);

        assertNotNull(response);
        assertEquals("nuevo@test.com", response.getEmailInvitado());
        assertEquals("Editor", response.getRolAsignado());
        assertNotNull(response.getMensaje());

        verify(invitacionRepository).save(any(InvitacionUsuario.class));
        verify(poolPermissionService).requirePermisoEnPool(
                eq(invitadoPorId),
                eq(pool.getId()),
                eq("USUARIO_INVITAR")
        );
    }

    @Test
    void aceptar_debeCrearUsuarioYRetornarResponse() {
        String token = "token-123";

        AceptarInvitacionRequest request =
                new AceptarInvitacionRequest();
        request.setPassword("123456");

        UUID empresaId = UUID.randomUUID();
        UUID rolPoolId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setNombre("Empresa Test");

        Pool pool = new Pool();
        pool.setId(UUID.randomUUID());
        pool.setEmpresa(empresa);

        RolPool rolPool = new RolPool();
        rolPool.setId(rolPoolId);
        rolPool.setNombre("Editor");
        rolPool.setPool(pool);

        InvitacionUsuario invitacion = new InvitacionUsuario();
        invitacion.setId(UUID.randomUUID());
        invitacion.setEmpresa(empresa);
        invitacion.setRolPool(rolPool);
        invitacion.setEmail("nuevo@test.com");
        invitacion.setEstado("pendiente");
        invitacion.setTokenHash(token);
        invitacion.setExpiresAt(LocalDateTime.now().plusHours(24));

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(UUID.randomUUID());
        usuarioGuardado.setEmail("nuevo@test.com");
        usuarioGuardado.setEmpresa(empresa);

        when(invitacionRepository.findByTokenHash(token))
                .thenReturn(Optional.of(invitacion));

        when(usuarioRepository.existsByEmail("nuevo@test.com"))
                .thenReturn(false);

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioGuardado);

        AceptarInvitacionResponse response =
                invitacionService.aceptar(token, request);

        assertNotNull(response);
        assertEquals(usuarioGuardado.getId(), response.getUsuarioId());
        assertEquals("nuevo@test.com", response.getEmail());
        assertEquals("Empresa Test", response.getEmpresaNombre());
        assertEquals("Editor", response.getRolAsignado());
        assertEquals(
                "Registro completado exitosamente",
                response.getMensaje()
        );

        verify(usuarioRepository).save(any(Usuario.class));
        verify(usuarioRolPoolRepository).save(any());
        verify(invitacionRepository, atLeastOnce())
                .save(any(InvitacionUsuario.class));
    }
}