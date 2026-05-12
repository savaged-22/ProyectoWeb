package com.lulo.users;

import com.lulo.company.Empresa;
import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PoolRepository poolRepository;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(
                usuarioRepository,
                passwordEncoder,
                poolRepository
        );
    }

    @Test
    void login_faltanCredenciales_retornaBadRequest() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "test@correo.com");

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void login_usuarioNoExiste_retornaUnauthorized() {
        Map<String, String> credentials = Map.of(
                "email", "test@correo.com",
                "password", "123456"
        );

        when(usuarioRepository.findByEmail("test@correo.com"))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void login_passwordIncorrecto_retornaUnauthorized() {
        Usuario usuario = new Usuario();
        usuario.setPasswordHash("hash");

        Map<String, String> credentials = Map.of(
                "email", "test@correo.com",
                "password", "incorrecta"
        );

        when(usuarioRepository.findByEmail("test@correo.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("incorrecta", "hash"))
                .thenReturn(false);

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void login_exitoso_conPoolExistente() {
        UUID usuarioId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();
        UUID poolId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmpresa(empresa);
        usuario.setEmail("test@correo.com");
        usuario.setPasswordHash("hash");

        Pool pool = new Pool();
        pool.setId(poolId);

        Map<String, String> credentials = Map.of(
                "email", "test@correo.com",
                "password", "123456"
        );

        when(usuarioRepository.findByEmail("test@correo.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("123456", "hash"))
                .thenReturn(true);

        when(poolRepository.findByEmpresaIdOrderByNombreAsc(empresaId))
                .thenReturn(List.of(pool));

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(200, response.getStatusCode().value());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("test@correo.com", body.get("email"));
        assertEquals(poolId.toString(), body.get("poolId"));
        assertEquals("demo-jwt-token-lulo-12345", body.get("token"));
    }

    @Test
    void login_exitoso_creaPoolSiNoExiste() {
        UUID usuarioId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();
        UUID poolId = UUID.randomUUID();

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmpresa(empresa);
        usuario.setEmail("test@correo.com");
        usuario.setPasswordHash("hash");

        Pool nuevoPool = new Pool();
        nuevoPool.setId(poolId);

        Map<String, String> credentials = Map.of(
                "email", "test@correo.com",
                "password", "123456"
        );

        when(usuarioRepository.findByEmail("test@correo.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("123456", "hash"))
                .thenReturn(true);

        when(poolRepository.findByEmpresaIdOrderByNombreAsc(empresaId))
                .thenReturn(Collections.emptyList());

        when(poolRepository.save(any(Pool.class)))
                .thenReturn(nuevoPool);

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(200, response.getStatusCode().value());

        verify(poolRepository).save(any(Pool.class));

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals(poolId.toString(), body.get("poolId"));
    }
}