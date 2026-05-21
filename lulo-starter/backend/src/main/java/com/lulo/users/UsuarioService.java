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
}
