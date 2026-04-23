package com.lulo.users;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Faltan credenciales"));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        Usuario usuario = usuarioOpt.get();
        
        // Verifica la contraseña encriptada usando BCrypt (configurado en SecurityConfig)
        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        // TODO: En el futuro esto devolverá un JWT real (HU-Auth)
        // Por ahora devuelve un token demo para conectar el frontend
        return ResponseEntity.ok(Map.of(
            "token", "demo-jwt-token-lulo-12345",
            "message", "Autenticación exitosa",
            "email", usuario.getEmail()
        ));
    }
}
