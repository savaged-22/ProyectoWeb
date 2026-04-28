package com.lulo.rbac;

import java.util.UUID;

import com.lulo.common.exception.ApiException;
import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PoolPermissionService {

    private final UsuarioRepository usuarioRepository;
    private final PoolRepository poolRepository;
    private final UsuarioRolPoolRepository usuarioRolPoolRepository;

    @Transactional(readOnly = true)
    public Usuario requireUsuario(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ApiException("Usuario no encontrado", HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Usuario requireUsuarioDeEmpresa(UUID usuarioId, UUID empresaId) {
        Usuario usuario = requireUsuario(usuarioId);
        if (!usuario.getEmpresa().getId().equals(empresaId)) {
            throw new ApiException("El usuario no pertenece a esta empresa", HttpStatus.FORBIDDEN);
        }
        return usuario;
    }

    @Transactional(readOnly = true)
    public Pool requirePoolDeEmpresa(UUID poolId, UUID empresaId) {
        return poolRepository.findById(poolId)
                .filter(pool -> pool.getEmpresa().getId().equals(empresaId))
                .orElseThrow(() -> new ApiException("El pool no pertenece a esta empresa", HttpStatus.FORBIDDEN));
    }

    @Transactional(readOnly = true)
    public void requirePermisoEnPool(UUID usuarioId, UUID poolId, String codigoPermiso) {
        if (!hasPermisoEnPool(usuarioId, poolId, codigoPermiso)) {
            throw new ApiException("El usuario no tiene permiso " + codigoPermiso + " en este pool", HttpStatus.FORBIDDEN);
        }
    }

    @Transactional(readOnly = true)
    public void requirePermisoEnEmpresa(UUID usuarioId, UUID empresaId, String codigoPermiso) {
        requireUsuarioDeEmpresa(usuarioId, empresaId);
        if (!hasPermisoEnEmpresa(usuarioId, empresaId, codigoPermiso)) {
            throw new ApiException("El usuario no tiene permiso " + codigoPermiso + " en esta empresa", HttpStatus.FORBIDDEN);
        }
    }

    @Transactional(readOnly = true)
    public boolean hasPermisoEnPool(UUID usuarioId, UUID poolId, String codigoPermiso) {
        List<UsuarioRolPool> asignaciones = usuarioRolPoolRepository.findByUsuarioIdAndPoolId(usuarioId, poolId);
        return asignaciones.stream()
                .map(UsuarioRolPool::getRolPool)
                .filter(RolPool::isActivo)
                .flatMap(rol -> rol.getPermisos().stream())
                .map(Permiso::getCodigo)
                .anyMatch(codigoPermiso::equals);
    }

    @Transactional(readOnly = true)
    public boolean hasPermisoEnEmpresa(UUID usuarioId, UUID empresaId, String codigoPermiso) {
        return usuarioRolPoolRepository.findByUsuarioIdAndEmpresaId(usuarioId, empresaId).stream()
                .map(UsuarioRolPool::getRolPool)
                .filter(RolPool::isActivo)
                .flatMap(rol -> rol.getPermisos().stream())
                .map(Permiso::getCodigo)
                .anyMatch(codigoPermiso::equals);
    }

    @Transactional(readOnly = true)
    public List<UUID> getPoolIdsConPermisoEnEmpresa(UUID usuarioId, UUID empresaId, String codigoPermiso) {
        Set<Integer> poolIds = new LinkedHashSet<>();
        usuarioRolPoolRepository.findByUsuarioIdAndEmpresaId(usuarioId, empresaId).stream()
                .map(UsuarioRolPool::getRolPool)
                .filter(RolPool::isActivo)
                .filter(rol -> rol.getPermisos().stream().map(Permiso::getCodigo).anyMatch(codigoPermiso::equals))
                .map(rol -> rol.getPool().getId())
                .forEach(poolIds::add);
        return List.copyOf(poolIds);
    }

    @Transactional(readOnly = true)
    public List<UUID> getPoolIdsAsignadosEnEmpresa(UUID usuarioId, UUID empresaId) {
        Set<Integer> poolIds = new LinkedHashSet<>();
        usuarioRolPoolRepository.findByUsuarioIdAndEmpresaId(usuarioId, empresaId).stream()
                .map(UsuarioRolPool::getRolPool)
                .filter(RolPool::isActivo)
                .map(rol -> rol.getPool().getId())
                .forEach(poolIds::add);
        return List.copyOf(poolIds);
    }
}
