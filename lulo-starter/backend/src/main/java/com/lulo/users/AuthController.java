package com.lulo.users;

import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PoolRepository poolRepository;

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          PoolRepository poolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.poolRepository = poolRepository;
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

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        // Buscar o crear un Pool por defecto para que el proceso builder no falle
        List<Pool> pools = poolRepository.findByEmpresaIdOrderByNombreAsc(usuario.getEmpresa().getId());
        Pool targetPool;
        
        if (pools.isEmpty()) {
            targetPool = new Pool();
            targetPool.setNombre("Main Pool");
            targetPool.setEmpresa(usuario.getEmpresa());
            targetPool.setConfigJson("{}");
            targetPool = poolRepository.save(targetPool);
        } else {
            targetPool = pools.get(0);
        }

        HashMap<String, Object> response = new HashMap<>();
        response.put("token", "demo-jwt-token-lulo-12345");
        response.put("message", "Autenticación exitosa");
        response.put("email", usuario.getEmail());
        response.put("usuarioId", usuario.getId().toString());
        response.put("empresaId", usuario.getEmpresa().getId().toString());
        response.put("poolId", targetPool.getId().toString());

        return ResponseEntity.ok(response);
    }
}
