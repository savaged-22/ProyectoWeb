package com.lulo.users;

import com.lulo.common.exception.ApiException;
import com.lulo.company.EmpresaRepository;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.RolPoolRepository;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolId;
import com.lulo.rbac.UsuarioRolPoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository       usuarioRepository;
    private final EmpresaRepository       empresaRepository;
    private final RolPoolRepository       rolPoolRepository;
    private final UsuarioRolPoolRepository usuarioRolPoolRepository;
    private final PasswordEncoder         passwordEncoder;

    @Transactional
    public CrearUsuarioDirectoResponse crearDirecto(CrearUsuarioDirectoRequest request) {

        var empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ApiException("Empresa no encontrada", HttpStatus.NOT_FOUND));

        RolPool rolPool = rolPoolRepository.findById(request.getRolPoolId())
                .orElseThrow(() -> new ApiException("Rol no encontrado", HttpStatus.NOT_FOUND));

        if (!rolPool.getPool().getEmpresa().getId().equals(request.getEmpresaId())) {
            throw new ApiException("El rol no pertenece a esta empresa", HttpStatus.FORBIDDEN);
        }

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(
                    "El correo ya está registrado: " + request.getEmail(),
                    HttpStatus.CONFLICT);
        }

        Usuario usuario = new Usuario();
        usuario.setEmpresa(empresa);
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setEstado("activo");
        usuario = usuarioRepository.save(usuario);

        UsuarioRolPool asignacion = new UsuarioRolPool();
        asignacion.setId(new UsuarioRolPoolId(usuario.getId(), rolPool.getId()));
        asignacion.setUsuario(usuario);
        asignacion.setRolPool(rolPool);
        usuarioRolPoolRepository.save(asignacion);

        return CrearUsuarioDirectoResponse.builder()
                .usuarioId(usuario.getId())
                .email(usuario.getEmail())
                .rolAsignado(rolPool.getNombre())
                .empresaNombre(empresa.getNombre())
                .mensaje("Usuario creado exitosamente")
                .build();
    }

    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("activo", "suspendido", "inactivo", "pendiente");

    /**
     * Edita un usuario: cambia su estado y/o reemplaza su rol.
     * Reemplazar el rol elimina todas las asignaciones previas del usuario
     * y deja únicamente la indicada (modelo de un rol por usuario).
     */
    @Transactional
    public ActualizarUsuarioResponse actualizar(UUID usuarioId, ActualizarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ApiException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        if (request.getEstado() != null && !request.getEstado().isBlank()) {
            String estado = request.getEstado().trim().toLowerCase();
            if (!ESTADOS_VALIDOS.contains(estado)) {
                throw new ApiException("Estado inválido: " + request.getEstado(), HttpStatus.BAD_REQUEST);
            }
            usuario.setEstado(estado);
        }

        String rolAsignado = null;
        if (request.getRolPoolId() != null) {
            RolPool rolPool = rolPoolRepository.findById(request.getRolPoolId())
                    .orElseThrow(() -> new ApiException("Rol no encontrado", HttpStatus.NOT_FOUND));
            if (!rolPool.getPool().getEmpresa().getId().equals(usuario.getEmpresa().getId())) {
                throw new ApiException("El rol no pertenece a la empresa del usuario", HttpStatus.FORBIDDEN);
            }
            // Reemplaza el rol: borra las asignaciones previas y deja solo la nueva.
            usuarioRolPoolRepository.deleteAll(usuarioRolPoolRepository.findByIdUsuarioId(usuarioId));
            usuarioRolPoolRepository.flush();
            UsuarioRolPool asignacion = new UsuarioRolPool();
            asignacion.setId(new UsuarioRolPoolId(usuario.getId(), rolPool.getId()));
            asignacion.setUsuario(usuario);
            asignacion.setRolPool(rolPool);
            usuarioRolPoolRepository.save(asignacion);
            rolAsignado = rolPool.getNombre();
        }

        usuario = usuarioRepository.save(usuario);

        if (rolAsignado == null) {
            rolAsignado = usuarioRolPoolRepository.findByIdUsuarioId(usuarioId).stream()
                    .map(urp -> urp.getRolPool().getNombre())
                    .findFirst()
                    .orElse("Sin rol");
        }

        return ActualizarUsuarioResponse.builder()
                .usuarioId(usuario.getId())
                .email(usuario.getEmail())
                .estado(usuario.getEstado())
                .rolAsignado(rolAsignado)
                .build();
    }
}
