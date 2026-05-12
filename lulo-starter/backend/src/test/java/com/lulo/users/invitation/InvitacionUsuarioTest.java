package com.lulo.users.invitation;

import com.lulo.company.Empresa;
import com.lulo.rbac.RolPool;
import com.lulo.users.Usuario;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InvitacionUsuarioTest {

    @Test
    void constructorVacio_settersYGetters_funcionanCorrectamente() {
        InvitacionUsuario invitacion = new InvitacionUsuario();

        UUID id = UUID.randomUUID();
        Empresa empresa = new Empresa();
        RolPool rolPool = new RolPool();
        Usuario createdByUser = new Usuario();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(3);
        LocalDateTime createdAt = LocalDateTime.now();

        invitacion.setId(id);
        invitacion.setEmpresa(empresa);
        invitacion.setRolPool(rolPool);
        invitacion.setCreatedByUser(createdByUser);
        invitacion.setEmail("usuario@test.com");
        invitacion.setTokenHash("token-123");
        invitacion.setEstado("pendiente");
        invitacion.setExpiresAt(expiresAt);
        invitacion.setCreatedAt(createdAt);

        assertEquals(id, invitacion.getId());
        assertEquals(empresa, invitacion.getEmpresa());
        assertEquals(rolPool, invitacion.getRolPool());
        assertEquals(createdByUser, invitacion.getCreatedByUser());
        assertEquals("usuario@test.com", invitacion.getEmail());
        assertEquals("token-123", invitacion.getTokenHash());
        assertEquals("pendiente", invitacion.getEstado());
        assertEquals(expiresAt, invitacion.getExpiresAt());
        assertEquals(createdAt, invitacion.getCreatedAt());
    }

    @Test
    void prePersist_debeAsignarCreatedAt() throws Exception {
        InvitacionUsuario invitacion = new InvitacionUsuario();

        assertNull(invitacion.getCreatedAt());

        Method method = InvitacionUsuario.class.getDeclaredMethod("prePersist");
        method.setAccessible(true);
        method.invoke(invitacion);

        assertNotNull(invitacion.getCreatedAt());
        assertTrue(
                invitacion.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1))
        );
    }

    @Test
    void estado_porDefecto_debeSerPendiente() {
        InvitacionUsuario invitacion = new InvitacionUsuario();

        assertEquals("pendiente", invitacion.getEstado());
    }
}