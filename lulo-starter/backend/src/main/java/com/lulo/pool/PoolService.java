package com.lulo.pool;

import com.lulo.audit.AuditService;
import com.lulo.common.exception.ApiException;
import com.lulo.company.Empresa;
import com.lulo.company.EmpresaRepository;
import com.lulo.pool.dto.CrearPoolRequest;
import com.lulo.pool.dto.EditarPoolRequest;
import com.lulo.pool.dto.PoolResponse;
import com.lulo.rbac.Permiso;
import com.lulo.rbac.PermisoRepository;
import com.lulo.rbac.PoolPermissionService;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.RolPoolRepository;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolId;
import com.lulo.rbac.UsuarioRolPoolRepository;
import com.lulo.users.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PoolService {

    private static final String ROL_ADMIN_NOMBRE = "Administrador";

    private final PoolRepository poolRepository;
    private final EmpresaRepository empresaRepository;
    private final PermisoRepository permisoRepository;
    private final RolPoolRepository rolPoolRepository;
    private final UsuarioRolPoolRepository usuarioRolPoolRepository;
    private final PoolPermissionService poolPermissionService;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<PoolResponse> listar(UUID empresaId, Integer usuarioId) {
        poolPermissionService.requirePermisoEnEmpresa(usuarioId, empresaId, "POOL_ADMINISTRAR");
        return poolRepository.findByEmpresaIdOrderByNombreAsc(empresaId).stream()
                .map(PoolService::toResponse)
                .toList();
    }

    @Transactional
    public PoolResponse crear(CrearPoolRequest request) {
        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ApiException("Empresa no encontrada", HttpStatus.NOT_FOUND));

        Usuario creadoPor = poolPermissionService.requireUsuarioDeEmpresa(request.getCreadoPorId(), empresa.getId());
        poolPermissionService.requirePermisoEnEmpresa(creadoPor.getId(), empresa.getId(), "POOL_ADMINISTRAR");

        String nombre = normalizeRequired(request.getNombre(), "El nombre del pool es obligatorio");
        if (poolRepository.existsByNombreAndEmpresaId(nombre, empresa.getId())) {
            throw new ApiException("Ya existe un pool con ese nombre en la empresa", HttpStatus.CONFLICT);
        }

        Pool pool = new Pool();
        pool.setEmpresa(empresa);
        pool.setNombre(nombre);
        pool.setConfigJson(normalize(request.getConfigJson()));
        pool = poolRepository.save(pool);

        List<Permiso> todosLosPermisos = permisoRepository.findAll();

        RolPool rolAdmin = new RolPool();
        rolAdmin.setPool(pool);
        rolAdmin.setNombre(ROL_ADMIN_NOMBRE);
        rolAdmin.setDescripcion("Rol con acceso completo al pool");
        rolAdmin.setActivo(true);
        rolAdmin.setEsPropietario(true);
        rolAdmin.setPermisos(new HashSet<>(todosLosPermisos));
        rolAdmin = rolPoolRepository.save(rolAdmin);

        UsuarioRolPool asignacion = new UsuarioRolPool();
        asignacion.setId(new UsuarioRolPoolId(creadoPor.getId(), rolAdmin.getId()));
        asignacion.setUsuario(creadoPor);
        asignacion.setRolPool(rolAdmin);
        usuarioRolPoolRepository.save(asignacion);

        auditService.registrar(
                empresa,
                creadoPor,
                "POOL",
                pool.getId(),
                "CREAR",
                null,
                snapshot(pool)
        );

        return toResponse(pool);
    }

    @Transactional
    public PoolResponse editar(Integer poolId, EditarPoolRequest request) {
        Pool pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new ApiException("Pool no encontrado", HttpStatus.NOT_FOUND));

        Usuario editadoPor = poolPermissionService.requireUsuarioDeEmpresa(
                request.getEditadoPorId(), pool.getEmpresa().getId());
        poolPermissionService.requirePermisoEnPool(editadoPor.getId(), pool.getId(), "POOL_ADMINISTRAR");

        Map<String, Object> antes = snapshot(pool);
        String nombre = request.getNombre() != null
                ? normalizeRequired(request.getNombre(), "El nombre del pool no puede estar vacío")
                : null;

        if (nombre != null &&
                !nombre.equals(pool.getNombre()) &&
                poolRepository.existsByNombreAndEmpresaId(nombre, pool.getEmpresa().getId())) {
            throw new ApiException("Ya existe un pool con ese nombre en la empresa", HttpStatus.CONFLICT);
        }

        if (nombre != null) pool.setNombre(nombre);
        if (request.getConfigJson() != null) pool.setConfigJson(normalize(request.getConfigJson()));
        pool = poolRepository.save(pool);

        auditService.registrar(
                pool.getEmpresa(),
                editadoPor,
                "POOL",
                pool.getId(),
                "EDITAR",
                antes,
                snapshot(pool)
        );

        return toResponse(pool);
    }

    private Map<String, Object> snapshot(Pool pool) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("empresaId", pool.getEmpresa().getId());
        snapshot.put("nombre", pool.getNombre());
        snapshot.put("configJson", pool.getConfigJson());
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

    static PoolResponse toResponse(Pool pool) {
        return PoolResponse.builder()
                .id(pool.getId())
                .empresaId(pool.getEmpresa().getId())
                .empresaNombre(pool.getEmpresa().getNombre())
                .nombre(pool.getNombre())
                .configJson(pool.getConfigJson())
                .createdAt(pool.getCreatedAt())
                .build();
    }
}
