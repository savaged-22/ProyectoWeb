package com.lulo.sharing;

import com.lulo.audit.AuditService;
import com.lulo.common.exception.ApiException;
import com.lulo.pool.PoolRepository;
import com.lulo.process.Proceso;
import com.lulo.process.ProcesoRepository;
import com.lulo.rbac.PoolPermissionService;
import com.lulo.sharing.dto.ActualizarComparticionProcesoRequest;
import com.lulo.sharing.dto.CompartirProcesoRequest;
import com.lulo.sharing.dto.ProcesoCompartidoResponse;
import com.lulo.users.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProcesoCompartidoService {

    private final ProcesoCompartidoRepository procesoCompartidoRepository;
    private final ProcesoRepository procesoRepository;
    private final PoolRepository poolRepository;
    private final PoolPermissionService poolPermissionService;
    private final AuditService auditService;

    @Transactional
    public ProcesoCompartidoResponse compartir(Integer procesoId, CompartirProcesoRequest request) {
        Proceso proceso = requireProcesoActivo(procesoId);
        Usuario creadoPor = poolPermissionService.requireUsuarioDeEmpresa(
                request.getCreadoPorId(), proceso.getEmpresa().getId());
        poolPermissionService.requirePermisoEnPool(creadoPor.getId(), proceso.getPool().getId(), "PROCESO_COMPARTIR");

        var poolDestino = poolRepository.findById(request.getPoolDestinoId())
                .orElseThrow(() -> new ApiException("Pool destino no encontrado", HttpStatus.NOT_FOUND));

        if (poolDestino.getId().equals(proceso.getPool().getId())) {
            throw new ApiException("No se puede compartir un proceso con su mismo pool de origen", HttpStatus.CONFLICT);
        }

        if (procesoCompartidoRepository.existsByProcesoIdAndPoolDestinoId(procesoId, poolDestino.getId())) {
            throw new ApiException("El proceso ya está compartido con ese pool", HttpStatus.CONFLICT);
        }

        ProcesoCompartido compartido = new ProcesoCompartido();
        compartido.setProceso(proceso);
        compartido.setPoolDestino(poolDestino);
        compartido.setCreatedByUser(creadoPor);
        compartido.setPermiso(normalizePermiso(request.getPermiso()));
        compartido = procesoCompartidoRepository.save(compartido);

        auditService.registrar(
                proceso.getEmpresa(),
                creadoPor,
                "PROCESO",
                proceso.getId(),
                "COMPARTIR",
                null,
                snapshot(compartido)
        );

        return toResponse(compartido);
    }

    @Transactional(readOnly = true)
    public List<ProcesoCompartidoResponse> listar(Integer procesoId, Integer usuarioId) {
        Proceso proceso = requireProcesoActivo(procesoId);
        Usuario usuario = poolPermissionService.requireUsuario(usuarioId);

        boolean puedeVer = poolPermissionService.hasPermisoEnPool(usuario.getId(), proceso.getPool().getId(), "PROCESO_COMPARTIR")
                && usuario.getEmpresa().getId().equals(proceso.getEmpresa().getId());
        if (!puedeVer) {
            throw new ApiException("El usuario no puede consultar la compartición de este proceso", HttpStatus.FORBIDDEN);
        }

        return procesoCompartidoRepository.findByProcesoId(procesoId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProcesoCompartidoResponse actualizar(Integer procesoId,
                                                Integer poolDestinoId,
                                                ActualizarComparticionProcesoRequest request) {
        Proceso proceso = requireProcesoActivo(procesoId);
        Usuario actualizadoPor = poolPermissionService.requireUsuarioDeEmpresa(
                request.getActualizadoPorId(), proceso.getEmpresa().getId());
        poolPermissionService.requirePermisoEnPool(actualizadoPor.getId(), proceso.getPool().getId(), "PROCESO_COMPARTIR");

        ProcesoCompartido compartido = procesoCompartidoRepository.findByProcesoIdAndPoolDestinoId(procesoId, poolDestinoId)
                .orElseThrow(() -> new ApiException("La compartición no existe", HttpStatus.NOT_FOUND));

        Map<String, Object> antes = snapshot(compartido);
        compartido.setPermiso(normalizePermiso(request.getPermiso()));
        compartido = procesoCompartidoRepository.save(compartido);

        auditService.registrar(
                proceso.getEmpresa(),
                actualizadoPor,
                "PROCESO",
                proceso.getId(),
                "COMPARTIR",
                antes,
                snapshot(compartido)
        );

        return toResponse(compartido);
    }

    @Transactional(readOnly = true)
    public List<Proceso> findProcesosCompartidosVisibles(Integer empresaId,
                                                         Integer usuarioId,
                                                         String estado,
                                                         String categoria,
                                                         String nombre) {
        List<Integer> poolIdsVisibles = poolPermissionService.getPoolIdsConPermisoEnEmpresa(usuarioId, empresaId, "PROCESO_VER");
        if (poolIdsVisibles.isEmpty()) {
            return List.of();
        }

        return procesoCompartidoRepository.findByPoolDestinoIdIn(poolIdsVisibles).stream()
                .map(ProcesoCompartido::getProceso)
                .filter(Proceso::isActivo)
                .filter(proceso -> matches(proceso, estado, categoria, nombre))
                .distinct()
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean puedeVer(Proceso proceso, Integer usuarioId, Integer empresaId) {
        if (proceso.getEmpresa().getId().equals(empresaId) &&
                poolPermissionService.hasPermisoEnPool(usuarioId, proceso.getPool().getId(), "PROCESO_VER")) {
            return true;
        }

        return procesoCompartidoRepository.findByProcesoId(proceso.getId()).stream()
                .filter(compartido -> compartido.getPoolDestino().getEmpresa().getId().equals(empresaId))
                .anyMatch(compartido -> poolPermissionService.hasPermisoEnPool(
                        usuarioId, compartido.getPoolDestino().getId(), "PROCESO_VER"));
    }

    @Transactional(readOnly = true)
    public boolean puedeEditar(Proceso proceso, Integer usuarioId, Integer empresaId) {
        if (proceso.getEmpresa().getId().equals(empresaId) &&
                poolPermissionService.hasPermisoEnPool(usuarioId, proceso.getPool().getId(), "PROCESO_EDITAR")) {
            return true;
        }

        return procesoCompartidoRepository.findByProcesoId(proceso.getId()).stream()
                .filter(compartido -> "edicion".equals(compartido.getPermiso()))
                .filter(compartido -> compartido.getPoolDestino().getEmpresa().getId().equals(empresaId))
                .anyMatch(compartido -> poolPermissionService.hasPermisoEnPool(
                        usuarioId, compartido.getPoolDestino().getId(), "PROCESO_EDITAR"));
    }

    @Transactional(readOnly = true)
    public boolean puedeEditarDiagrama(Proceso proceso, Integer usuarioId, Integer empresaId) {
        if (proceso.getEmpresa().getId().equals(empresaId) &&
                poolPermissionService.hasPermisoEnPool(usuarioId, proceso.getPool().getId(), "DIAGRAMA_EDITAR")) {
            return true;
        }

        return procesoCompartidoRepository.findByProcesoId(proceso.getId()).stream()
                .filter(compartido -> "edicion".equals(compartido.getPermiso()))
                .filter(compartido -> compartido.getPoolDestino().getEmpresa().getId().equals(empresaId))
                .anyMatch(compartido -> poolPermissionService.hasPermisoEnPool(
                        usuarioId, compartido.getPoolDestino().getId(), "DIAGRAMA_EDITAR"));
    }

    @Transactional(readOnly = true)
    public Optional<ProcesoCompartido> findComparticionEditable(Proceso proceso, Integer usuarioId, Integer empresaId) {
        return procesoCompartidoRepository.findByProcesoId(proceso.getId()).stream()
                .filter(compartido -> "edicion".equals(compartido.getPermiso()))
                .filter(compartido -> compartido.getPoolDestino().getEmpresa().getId().equals(empresaId))
                .filter(compartido -> poolPermissionService.hasPermisoEnPool(
                        usuarioId, compartido.getPoolDestino().getId(), "PROCESO_EDITAR"))
                .findFirst();
    }

    private Proceso requireProcesoActivo(Integer procesoId) {
        return procesoRepository.findByIdAndActivoTrue(procesoId)
                .orElseThrow(() -> new ApiException("Proceso no encontrado", HttpStatus.NOT_FOUND));
    }

    private String normalizePermiso(String permiso) {
        return permiso == null ? "lectura" : permiso.trim();
    }

    private boolean matches(Proceso proceso, String estado, String categoria, String nombre) {
        if (estado != null && !estado.equals(proceso.getEstado())) {
            return false;
        }
        if (categoria != null && !categoria.equals(proceso.getCategoria())) {
            return false;
        }
        return nombre == null || (proceso.getNombre() != null &&
                proceso.getNombre().toLowerCase().contains(nombre.toLowerCase()));
    }

    private Map<String, Object> snapshot(ProcesoCompartido compartido) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("poolDestinoId", compartido.getPoolDestino().getId());
        snapshot.put("poolDestinoNombre", compartido.getPoolDestino().getNombre());
        snapshot.put("permiso", compartido.getPermiso());
        return snapshot;
    }

    private ProcesoCompartidoResponse toResponse(ProcesoCompartido compartido) {
        return ProcesoCompartidoResponse.builder()
                .procesoId(compartido.getProceso().getId())
                .poolDestinoId(compartido.getPoolDestino().getId())
                .poolDestinoNombre(compartido.getPoolDestino().getNombre())
                .creadoPorId(compartido.getCreatedByUser().getId())
                .creadoPorEmail(compartido.getCreatedByUser().getEmail())
                .permiso(compartido.getPermiso())
                .createdAt(compartido.getCreatedAt())
                .build();
    }
}
