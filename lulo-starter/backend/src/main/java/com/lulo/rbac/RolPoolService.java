package com.lulo.rbac;

import java.util.UUID;

import com.lulo.audit.AuditService;
import com.lulo.common.exception.ApiException;
import com.lulo.rbac.dto.ActualizarPermisosRolPoolRequest;
import com.lulo.rbac.dto.AsignarRolPoolRequest;
import com.lulo.rbac.dto.CrearRolPoolRequest;
import com.lulo.rbac.dto.EditarRolPoolRequest;
import com.lulo.rbac.dto.EliminarRolPoolRequest;
import com.lulo.rbac.dto.PermisoResponse;
import com.lulo.rbac.dto.RolPoolResponse;
import com.lulo.users.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RolPoolService {

    private final RolPoolRepository rolPoolRepository;
    private final PermisoRepository permisoRepository;
    private final UsuarioRolPoolRepository usuarioRolPoolRepository;
    private final PoolPermissionService poolPermissionService;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<RolPoolResponse> listar(UUID poolId, UUID usuarioId, boolean soloActivos) {
        poolPermissionService.requirePermisoEnPool(usuarioId, poolId, "ROL_VER");
        List<RolPool> roles = soloActivos
                ? rolPoolRepository.findByPoolIdAndActivoTrueOrderByNombreAsc(poolId)
                : rolPoolRepository.findByPoolId(poolId);
        return roles.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PermisoResponse> listarPermisos(UUID poolId, UUID usuarioId) {
        poolPermissionService.requirePermisoEnPool(usuarioId, poolId, "ROL_VER");
        return permisoRepository.findAllByOrderByCodigoAsc().stream()
                .map(this::toPermisoResponse)
                .toList();
    }

    @Transactional
    public RolPoolResponse crear(CrearRolPoolRequest request) {
        var pool = poolPermissionService.requirePoolDeEmpresa(
                request.getPoolId(),
                poolPermissionService.requireUsuario(request.getCreadoPorId()).getEmpresa().getId());

        Usuario creadoPor = poolPermissionService.requireUsuarioDeEmpresa(
                request.getCreadoPorId(), pool.getEmpresa().getId());
        poolPermissionService.requirePermisoEnPool(creadoPor.getId(), pool.getId(), "ROL_CREAR");

        String nombre = normalizeRequired(request.getNombre(), "El nombre del rol es obligatorio");
        if (rolPoolRepository.existsByPoolIdAndNombreAndActivoTrue(pool.getId(), nombre)) {
            throw new ApiException("Ya existe un rol activo con ese nombre en el pool", HttpStatus.CONFLICT);
        }

        RolPool rolPool = new RolPool();
        rolPool.setPool(pool);
        rolPool.setNombre(nombre);
        rolPool.setDescripcion(normalize(request.getDescripcion()));
        rolPool.setActivo(true);
        rolPool.setEsPropietario(false);
        rolPool.setPermisos(resolvePermisos(request.getCodigosPermiso()));
        rolPool = rolPoolRepository.save(rolPool);

        auditService.registrar(
                pool.getEmpresa(),
                creadoPor,
                "ROL_POOL",
                rolPool.getId(),
                "CREAR",
                null,
                snapshot(rolPool)
        );

        return toResponse(rolPool);
    }

    @Transactional
    public RolPoolResponse editar(UUID rolPoolId, EditarRolPoolRequest request) {
        RolPool rolPool = rolPoolRepository.findByIdAndActivoTrue(rolPoolId)
                .orElseThrow(() -> new ApiException("Rol de pool no encontrado", HttpStatus.NOT_FOUND));

        Usuario editadoPor = poolPermissionService.requireUsuarioDeEmpresa(
                request.getEditadoPorId(), rolPool.getPool().getEmpresa().getId());
        poolPermissionService.requirePermisoEnPool(editadoPor.getId(), rolPool.getPool().getId(), "ROL_EDITAR");

        Map<String, Object> antes = snapshot(rolPool);
        String nombre = request.getNombre() != null
                ? normalizeRequired(request.getNombre(), "El nombre del rol no puede estar vacío")
                : null;

        if (nombre != null &&
                rolPoolRepository.existsActivoByPoolIdAndNombreExcluyendoId(rolPool.getPool().getId(), nombre, rolPoolId)) {
            throw new ApiException("Ya existe un rol activo con ese nombre en el pool", HttpStatus.CONFLICT);
        }

        if (nombre != null) rolPool.setNombre(nombre);
        if (request.getDescripcion() != null) rolPool.setDescripcion(normalize(request.getDescripcion()));
        rolPool = rolPoolRepository.save(rolPool);

        auditService.registrar(
                rolPool.getPool().getEmpresa(),
                editadoPor,
                "ROL_POOL",
                rolPool.getId(),
                "EDITAR",
                antes,
                snapshot(rolPool)
        );

        return toResponse(rolPool);
    }

    @Transactional
    public void eliminar(UUID rolPoolId, EliminarRolPoolRequest request) {
        RolPool rolPool = rolPoolRepository.findByIdAndActivoTrue(rolPoolId)
                .orElseThrow(() -> new ApiException("Rol de pool no encontrado", HttpStatus.NOT_FOUND));

        Usuario eliminadoPor = poolPermissionService.requireUsuarioDeEmpresa(
                request.getEliminadoPorId(), rolPool.getPool().getEmpresa().getId());
        poolPermissionService.requirePermisoEnPool(eliminadoPor.getId(), rolPool.getPool().getId(), "ROL_ELIMINAR");

        if (rolPool.isEsPropietario()) {
            throw new ApiException("No se puede eliminar el rol propietario del pool", HttpStatus.CONFLICT);
        }

        if (usuarioRolPoolRepository.existsByIdRolPoolId(rolPoolId)) {
            throw new ApiException("No se puede eliminar un rol que tiene usuarios asignados", HttpStatus.CONFLICT);
        }

        Map<String, Object> antes = snapshot(rolPool);
        rolPool.setActivo(false);
        rolPoolRepository.save(rolPool);

        auditService.registrar(
                rolPool.getPool().getEmpresa(),
                eliminadoPor,
                "ROL_POOL",
                rolPool.getId(),
                "ELIMINAR",
                antes,
                snapshot(rolPool)
        );
    }

    @Transactional
    public RolPoolResponse actualizarPermisos(UUID rolPoolId, ActualizarPermisosRolPoolRequest request) {
        RolPool rolPool = rolPoolRepository.findByIdAndActivoTrue(rolPoolId)
                .orElseThrow(() -> new ApiException("Rol de pool no encontrado", HttpStatus.NOT_FOUND));

        Usuario actualizadoPor = poolPermissionService.requireUsuarioDeEmpresa(
                request.getActualizadoPorId(), rolPool.getPool().getEmpresa().getId());
        poolPermissionService.requirePermisoEnPool(actualizadoPor.getId(), rolPool.getPool().getId(), "ROL_EDITAR");

        if (rolPool.isEsPropietario()) {
            throw new ApiException("No se pueden modificar los permisos del rol propietario", HttpStatus.CONFLICT);
        }

        Map<String, Object> antes = snapshot(rolPool);
        rolPool.setPermisos(resolvePermisos(request.getCodigosPermiso()));
        rolPool = rolPoolRepository.save(rolPool);

        auditService.registrar(
                rolPool.getPool().getEmpresa(),
                actualizadoPor,
                "ROL_POOL",
                rolPool.getId(),
                "EDITAR",
                antes,
                snapshot(rolPool)
        );

        return toResponse(rolPool);
    }

    @Transactional
    public RolPoolResponse asignarUsuario(UUID rolPoolId, AsignarRolPoolRequest request) {
        RolPool rolPool = rolPoolRepository.findByIdAndActivoTrue(rolPoolId)
                .orElseThrow(() -> new ApiException("Rol de pool no encontrado", HttpStatus.NOT_FOUND));

        Usuario asignadoPor = poolPermissionService.requireUsuarioDeEmpresa(
                request.getAsignadoPorId(), rolPool.getPool().getEmpresa().getId());
        poolPermissionService.requirePermisoEnPool(asignadoPor.getId(), rolPool.getPool().getId(), "POOL_ADMINISTRAR");

        Usuario usuario = poolPermissionService.requireUsuarioDeEmpresa(request.getUsuarioId(), rolPool.getPool().getEmpresa().getId());
        if (usuarioRolPoolRepository.existsByIdUsuarioIdAndIdRolPoolId(usuario.getId(), rolPoolId)) {
            throw new ApiException("El usuario ya tiene ese rol asignado", HttpStatus.CONFLICT);
        }

        UsuarioRolPool asignacion = new UsuarioRolPool();
        asignacion.setId(new UsuarioRolPoolId(usuario.getId(), rolPoolId));
        asignacion.setUsuario(usuario);
        asignacion.setRolPool(rolPool);
        usuarioRolPoolRepository.save(asignacion);

        auditService.registrar(
                rolPool.getPool().getEmpresa(),
                asignadoPor,
                "ROL_POOL",
                rolPool.getId(),
                "ASIGNAR_ROL",
                null,
                Map.of("usuarioId", usuario.getId(), "usuarioEmail", usuario.getEmail())
        );

        return toResponse(rolPool);
    }

    private Set<Permiso> resolvePermisos(List<String> codigosPermiso) {
        List<String> codigos = codigosPermiso == null ? List.of() : codigosPermiso.stream()
                .map(this::normalize)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        if (codigos.isEmpty()) {
            return Set.of();
        }

        List<Permiso> permisos = permisoRepository.findByCodigoIn(codigos);
        if (permisos.size() != codigos.size()) {
            throw new ApiException("Uno o más permisos no existen", HttpStatus.BAD_REQUEST);
        }
        return Set.copyOf(permisos);
    }

    private Map<String, Object> snapshot(RolPool rolPool) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("poolId", rolPool.getPool().getId());
        snapshot.put("nombre", rolPool.getNombre());
        snapshot.put("descripcion", rolPool.getDescripcion());
        snapshot.put("activo", rolPool.isActivo());
        snapshot.put("esPropietario", rolPool.isEsPropietario());
        snapshot.put("permisos", rolPool.getPermisos().stream().map(Permiso::getCodigo).sorted().toList());
        return snapshot;
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalize(value);
        if (normalized == null) {
            throw new ApiException(message, HttpStatus.BAD_REQUEST);
        }
        return normalized;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private PermisoResponse toPermisoResponse(Permiso permiso) {
        return PermisoResponse.builder()
                .id(permiso.getId())
                .codigo(permiso.getCodigo())
                .descripcion(permiso.getDescripcion())
                .build();
    }

    private RolPoolResponse toResponse(RolPool rolPool) {
        return RolPoolResponse.builder()
                .id(rolPool.getId())
                .poolId(rolPool.getPool().getId())
                .poolNombre(rolPool.getPool().getNombre())
                .nombre(rolPool.getNombre())
                .descripcion(rolPool.getDescripcion())
                .activo(rolPool.isActivo())
                .esPropietario(rolPool.isEsPropietario())
                .permisos(rolPool.getPermisos().stream()
                        .sorted(java.util.Comparator.comparing(Permiso::getCodigo))
                        .map(this::toPermisoResponse)
                        .toList())
                .createdAt(rolPool.getCreatedAt())
                .build();
    }
}
