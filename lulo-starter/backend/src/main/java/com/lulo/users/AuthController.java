package com.lulo.users;

import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import com.lulo.rbac.Permiso;
import com.lulo.rbac.PermisoRepository;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PoolRepository poolRepository;
    private final UsuarioRolPoolRepository usuarioRolPoolRepository;
    private final PermisoRepository permisoRepository;

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          PoolRepository poolRepository,
                          UsuarioRolPoolRepository usuarioRolPoolRepository,
                          PermisoRepository permisoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.poolRepository = poolRepository;
        this.usuarioRolPoolRepository = usuarioRolPoolRepository;
        this.permisoRepository = permisoRepository;
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

        boolean esPropietario = usuarioRolPoolRepository
                .findByIdUsuarioId(usuario.getId())
                .stream()
                .anyMatch(urp -> urp.getRolPool().isEsPropietario());

        // Permisos efectivos del usuario en su pool: el propietario los tiene
        // todos; el colaborador, la unión de los permisos de sus roles activos.
        // El frontend usa esta lista para ocultar menús, botones y rutas.
        Set<String> permisos;
        if (esPropietario) {
            permisos = permisoRepository.findAllByOrderByCodigoAsc().stream()
                    .map(Permiso::getCodigo)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } else {
            permisos = usuarioRolPoolRepository
                    .findByUsuarioIdAndPoolId(usuario.getId(), targetPool.getId()).stream()
                    .map(UsuarioRolPool::getRolPool)
                    .filter(RolPool::isActivo)
                    .flatMap(rol -> rol.getPermisos().stream())
                    .map(Permiso::getCodigo)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        HashMap<String, Object> response = new HashMap<>();
        response.put("token", "demo-jwt-token-lulo-12345");
        response.put("message", "Autenticación exitosa");
        response.put("email", usuario.getEmail());
        response.put("usuarioId", usuario.getId().toString());
        response.put("empresaId", usuario.getEmpresa().getId().toString());
        response.put("empresaNombre", usuario.getEmpresa().getNombre());
        response.put("poolId", targetPool.getId().toString());
        response.put("rol", esPropietario ? "PROPIETARIO" : "COLABORADOR");
        response.put("permisos", permisos);

        return ResponseEntity.ok(response);
    }
}
