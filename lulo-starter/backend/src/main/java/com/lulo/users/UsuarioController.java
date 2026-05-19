package com.lulo.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

    private final JdbcTemplate  jdbcTemplate;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<Map<String, Object>> usuarios = jdbcTemplate.queryForList(
                    "SELECT id, email, estado, empresa_id FROM usuario");
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error: " + e.getMessage() + "\nCause: " +
                          (e.getCause() != null ? e.getCause().getMessage() : ""));
        }
    }

    @PostMapping
    public ResponseEntity<CrearUsuarioDirectoResponse> crearUsuario(
            @RequestBody CrearUsuarioDirectoRequest request) {
        return ResponseEntity.status(201).body(usuarioService.crearDirecto(request));
    }

    @PatchMapping("/{usuarioId}")
    public ResponseEntity<ActualizarUsuarioResponse> actualizarUsuario(
            @PathVariable UUID usuarioId,
            @RequestBody ActualizarUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.actualizar(usuarioId, request));
    }
}
