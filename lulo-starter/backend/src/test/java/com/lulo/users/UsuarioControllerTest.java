package com.lulo.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    private JdbcTemplate jdbcTemplate;
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        usuarioController = new UsuarioController();

        // Inyecta el mock en el campo privado jdbcTemplate
        ReflectionTestUtils.setField(
                usuarioController,
                "jdbcTemplate",
                jdbcTemplate
        );
    }

    @Test
    void getAllUsers_retornaListaUsuarios() {
        List<Map<String, Object>> usuarios = List.of(
                Map.of(
                        "id", 1,
                        "email", "test@correo.com",
                        "estado", "ACTIVO",
                        "empresa_id", 10
                )
        );

        when(jdbcTemplate.queryForList(
                "SELECT id, email, estado, empresa_id FROM usuario"
        )).thenReturn(usuarios);

        ResponseEntity<?> response = usuarioController.getAllUsers();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(usuarios, response.getBody());
    }

    @Test
    void getAllUsers_cuandoOcurreError_retorna500() {
        when(jdbcTemplate.queryForList(
                "SELECT id, email, estado, empresa_id FROM usuario"
        )).thenThrow(new RuntimeException("Error de base de datos"));

        ResponseEntity<?> response = usuarioController.getAllUsers();

        assertEquals(500, response.getStatusCode().value());

        String body = (String) response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("Error de base de datos"));
    }
}
