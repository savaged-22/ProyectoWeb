package com.lulo.users;

import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolRepository;
import com.lulo.users.dto.LoginRequest;
import com.lulo.users.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolPoolRepository usuarioRolPoolRepository;
    private final PoolRepository poolRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository,
                          UsuarioRolPoolRepository usuarioRolPoolRepository,
                          PoolRepository poolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolPoolRepository = usuarioRolPoolRepository;
        this.poolRepository = poolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest credentials) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(credentials.getEmail());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(credentials.getPassword(), usuario.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        String rol = resolveRol(usuario);
        Pool targetPool = resolveDefaultPool(usuario);

        // TODO: Reemplazar token demo por JWT real cuando se implemente HU-Auth
        LoginResponse response = LoginResponse.builder()
                .token("demo-jwt-token-lulo-" + usuario.getId())
                .message("Autenticación exitosa")
                .usuarioId(usuario.getId())
                .empresaId(usuario.getEmpresa().getId())
                .empresaNombre(usuario.getEmpresa().getNombre())
                .email(usuario.getEmail())
                .rol(rol)
                .poolId(targetPool.getId())
                .build();

        return ResponseEntity.ok(response);
    }

    private String resolveRol(Usuario usuario) {
        return usuarioRolPoolRepository.findByIdUsuarioId(usuario.getId()).stream()
                .map(UsuarioRolPool::getRolPool)
                .anyMatch(rp -> rp != null && rp.isEsPropietario())
                ? "PROPIETARIO"
                : "COLABORADOR";
    }

    private Pool resolveDefaultPool(Usuario usuario) {
        List<Pool> pools = poolRepository.findByEmpresaIdOrderByNombreAsc(usuario.getEmpresa().getId());
        if (!pools.isEmpty()) return pools.get(0);

        Pool nuevo = new Pool();
        nuevo.setNombre("Main Pool");
        nuevo.setEmpresa(usuario.getEmpresa());
        nuevo.setConfigJson("{}");
        return poolRepository.save(nuevo);
    }
}
