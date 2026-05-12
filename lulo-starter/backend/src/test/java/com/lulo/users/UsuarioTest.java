package com.lulo.users;

import com.lulo.company.Empresa;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void gettersYSetters_funcionanCorrectamente() {
        // Arrange
        Usuario usuario = new Usuario();
        UUID id = UUID.randomUUID();
        Empresa empresa = new Empresa();

        usuario.setId(id);
        usuario.setEmpresa(empresa);
        usuario.setEmail("test@correo.com");
        usuario.setPasswordHash("hash123");
        usuario.setEstado("ACTIVO");

        // Assert
        assertEquals(id, usuario.getId());
        assertEquals(empresa, usuario.getEmpresa());
        assertEquals("test@correo.com", usuario.getEmail());
        assertEquals("hash123", usuario.getPasswordHash());
        assertEquals("ACTIVO", usuario.getEstado());
    }

    @Test
    void constructorVacio_creaObjetoCorrectamente() {
        Usuario usuario = new Usuario();

        assertNotNull(usuario);
        assertNull(usuario.getId());
        assertNull(usuario.getEmpresa());
        assertNull(usuario.getEmail());
        assertNull(usuario.getPasswordHash());
        assertNull(usuario.getEstado());
    }
}
