package com.lulo.rbac;

import com.lulo.audit.AuditService;
import com.lulo.common.exception.ApiException;
import com.lulo.company.Empresa;
import com.lulo.company.EmpresaRepository;
import com.lulo.rbac.dto.CrearRolProcesoRequest;
import com.lulo.rbac.dto.EditarRolProcesoRequest;
import com.lulo.rbac.dto.EliminarRolProcesoRequest;
import com.lulo.rbac.dto.RolProcesoResponse;
import com.lulo.users.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RolProcesoService {

    private final RolProcesoRepository rolProcesoRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditService auditService;

    @Transactional
    public RolProcesoResponse crear(CrearRolProcesoRequest request) {
        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ApiException("Empresa no encontrada", HttpStatus.NOT_FOUND));

        var creadoPor = requireUsuarioDeEmpresa(request.getCreadoPorId(), empresa.getId());
        String nombreNormalizado = normalizeRequired(request.getNombre(), "El nombre del rol es obligatorio");

        if (rolProcesoRepository.existsByEmpresaIdAndNombreAndActivoTrue(empresa.getId(), nombreNormalizado)) {
            throw new ApiException("Ya existe un rol activo con ese nombre en la empresa", HttpStatus.CONFLICT);
        }

        // TODO: verificar permiso ROL_CREAR del usuario en la empresa/pool (HU-Auth)

        RolProceso rolProceso = new RolProceso();
        rolProceso.setEmpresa(empresa);
        rolProceso.setNombre(nombreNormalizado);
        rolProceso.setDescripcion(normalize(request.getDescripcion()));
        rolProceso.setActivo(true);
        rolProceso = rolProcesoRepository.save(rolProceso);

        auditService.registrar(
                empresa,
                creadoPor,
                "ROL_PROCESO",
                rolProceso.getId(),
                "CREAR",
                null,
                snapshot(rolProceso)
        );

        return toResponse(rolProceso);
    }

    @Transactional
    public RolProcesoResponse editar(Integer rolProcesoId, EditarRolProcesoRequest request) {
        RolProceso rolProceso = rolProcesoRepository.findByIdAndActivoTrue(rolProcesoId)
                .orElseThrow(() -> new ApiException("Rol de proceso no encontrado", HttpStatus.NOT_FOUND));

        var editadoPor = requireUsuarioDeEmpresa(request.getEditadoPorId(), rolProceso.getEmpresa().getId());
        Map<String, Object> antes = snapshot(rolProceso);

        String nombreNormalizado = request.getNombre() != null ? normalizeRequired(request.getNombre(),
                "El nombre del rol no puede estar vacío") : null;

        if (nombreNormalizado != null &&
                rolProcesoRepository.existsActivoByEmpresaIdAndNombreExcluyendoId(
                        rolProceso.getEmpresa().getId(), nombreNormalizado, rolProcesoId)) {
            throw new ApiException("Ya existe un rol activo con ese nombre en la empresa", HttpStatus.CONFLICT);
        }

        // TODO: verificar permiso ROL_EDITAR del usuario en la empresa/pool (HU-Auth)

        if (nombreNormalizado != null) rolProceso.setNombre(nombreNormalizado);
        if (request.getDescripcion() != null) rolProceso.setDescripcion(normalize(request.getDescripcion()));
        rolProceso = rolProcesoRepository.save(rolProceso);

        auditService.registrar(
                rolProceso.getEmpresa(),
                editadoPor,
                "ROL_PROCESO",
                rolProceso.getId(),
                "EDITAR",
                antes,
                snapshot(rolProceso)
        );

        return toResponse(rolProceso);
    }

    @Transactional
    public void eliminar(Integer rolProcesoId, EliminarRolProcesoRequest request) {
        RolProceso rolProceso = rolProcesoRepository.findByIdAndActivoTrue(rolProcesoId)
                .orElseThrow(() -> new ApiException("Rol de proceso no encontrado", HttpStatus.NOT_FOUND));

        var eliminadoPor = requireUsuarioDeEmpresa(request.getEliminadoPorId(), rolProceso.getEmpresa().getId());

        if (rolProcesoRepository.existsEnLane(rolProcesoId)) {
            throw new ApiException(
                    "No se puede eliminar el rol porque está asignado a una lane y podría afectar actividades existentes",
                    HttpStatus.CONFLICT);
        }

        // TODO: verificar permiso ROL_ELIMINAR del usuario en la empresa/pool (HU-Auth)

        Map<String, Object> antes = snapshot(rolProceso);
        rolProceso.setActivo(false);
        rolProcesoRepository.save(rolProceso);

        auditService.registrar(
                rolProceso.getEmpresa(),
                eliminadoPor,
                "ROL_PROCESO",
                rolProceso.getId(),
                "ELIMINAR",
                antes,
                snapshot(rolProceso)
        );
    }

    private com.lulo.users.Usuario requireUsuarioDeEmpresa(Integer usuarioId, Integer empresaId) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ApiException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        if (!usuario.getEmpresa().getId().equals(empresaId)) {
            throw new ApiException("El usuario no pertenece a esta empresa", HttpStatus.FORBIDDEN);
        }

        return usuario;
    }

    private Map<String, Object> snapshot(RolProceso rolProceso) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("empresaId", rolProceso.getEmpresa().getId());
        m.put("nombre", rolProceso.getNombre());
        m.put("descripcion", rolProceso.getDescripcion());
        m.put("activo", rolProceso.isActivo());
        return m;
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

    private RolProcesoResponse toResponse(RolProceso rolProceso) {
        return RolProcesoResponse.builder()
                .id(rolProceso.getId())
                .empresaId(rolProceso.getEmpresa().getId())
                .nombre(rolProceso.getNombre())
                .descripcion(rolProceso.getDescripcion())
                .activo(rolProceso.isActivo())
                .createdAt(rolProceso.getCreatedAt())
                .build();
    }
}
