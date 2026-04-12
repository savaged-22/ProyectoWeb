package com.lulo.process;

import com.lulo.audit.AuditService;
import com.lulo.common.exception.ApiException;
import com.lulo.company.EmpresaRepository;
import com.lulo.diagram.DiagramService;
import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import com.lulo.process.dto.CrearProcesoRequest;
import com.lulo.process.dto.EditarProcesoRequest;
import com.lulo.process.dto.EliminarProcesoRequest;
import com.lulo.process.dto.ProcesoDetalleResponse;
import com.lulo.process.dto.ProcesoResponse;
import com.lulo.rbac.PoolPermissionService;
import com.lulo.sharing.ProcesoCompartidoService;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcesoService {

    private final ProcesoRepository  procesoRepository;
    private final EmpresaRepository  empresaRepository;
    private final PoolRepository     poolRepository;
    private final UsuarioRepository  usuarioRepository;
    private final AuditService       auditService;
    private final DiagramService     diagramService;
    private final PoolPermissionService poolPermissionService;
    private final ProcesoCompartidoService procesoCompartidoService;

    // ── Listar con filtros ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<ProcesoResponse> listar(UUID empresaId,
                                        Integer usuarioId,
                                        String estado,
                                        String categoria,
                                        String nombre,
                                        Pageable pageable) {
        poolPermissionService.requireUsuarioDeEmpresa(usuarioId, empresaId);

        Specification<Proceso> spec = Specification
                .where(ProcesoSpec.activos())
                .and(ProcesoSpec.deEmpresa(empresaId));

        if (estado    != null) spec = spec.and(ProcesoSpec.conEstado(estado));
        if (categoria != null) spec = spec.and(ProcesoSpec.conCategoria(categoria));
        if (nombre    != null) spec = spec.and(ProcesoSpec.nombreContiene(nombre));

        List<Integer> poolIdsPropios = poolPermissionService.getPoolIdsConPermisoEnEmpresa(usuarioId, empresaId, "PROCESO_VER");
        List<Proceso> visibles = new ArrayList<>();
        if (!poolIdsPropios.isEmpty()) {
            visibles.addAll(procesoRepository.findAll(spec).stream()
                    .filter(proceso -> poolIdsPropios.contains(proceso.getPool().getId()))
                    .toList());
        }
        visibles.addAll(procesoCompartidoService.findProcesosCompartidosVisibles(empresaId, usuarioId, estado, categoria, nombre));

        List<Proceso> ordenados = deduplicar(visibles).stream()
                .sorted(buildComparator(pageable))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), ordenados.size());
        List<ProcesoResponse> content = start >= ordenados.size()
                ? List.of()
                : ordenados.subList(start, end).stream().map(ProcesoService::toResponse).toList();

        return new PageImpl<>(content, pageable, ordenados.size());
    }

    // ── Obtener detalle con diagrama ──────────────────────────────────────────

    @Transactional(readOnly = true)
    public ProcesoDetalleResponse obtener(Integer procesoId, UUID empresaId, Integer usuarioId) {
        poolPermissionService.requireUsuarioDeEmpresa(usuarioId, empresaId);

        Proceso proceso = procesoRepository.findByIdAndActivoTrue(procesoId)
                .orElseThrow(() -> new ApiException("Proceso no encontrado", HttpStatus.NOT_FOUND));

        if (!procesoCompartidoService.puedeVer(proceso, usuarioId, empresaId)) {
            throw new ApiException("El usuario no puede acceder a este proceso", HttpStatus.FORBIDDEN);
        }

        return ProcesoDetalleResponse.builder()
                .id(proceso.getId())
                .empresaId(proceso.getEmpresa().getId())
                .empresaNombre(proceso.getEmpresa().getNombre())
                .poolId(proceso.getPool().getId())
                .poolNombre(proceso.getPool().getNombre())
                .creadoPorId(proceso.getCreatedByUser().getId())
                .creadoPorEmail(proceso.getCreatedByUser().getEmail())
                .nombre(proceso.getNombre())
                .descripcion(proceso.getDescripcion())
                .categoria(proceso.getCategoria())
                .estado(proceso.getEstado())
                .activo(proceso.isActivo())
                .createdAt(proceso.getCreatedAt())
                .updatedAt(proceso.getUpdatedAt())
                .lanes(diagramService.getLanes(procesoId))
                .nodos(diagramService.getNodos(procesoId))
                .arcos(diagramService.getArcos(procesoId))
                .build();
    }

    // ── Crear ─────────────────────────────────────────────────────────────────

    @Transactional
    public ProcesoResponse crear(CrearProcesoRequest request) {

        var empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ApiException("Empresa no encontrada", HttpStatus.NOT_FOUND));

        Pool pool = poolRepository.findById(request.getPoolId())
                .orElseThrow(() -> new ApiException("Pool no encontrado", HttpStatus.NOT_FOUND));

        // El pool debe pertenecer a la empresa
        if (!pool.getEmpresa().getId().equals(request.getEmpresaId())) {
            throw new ApiException("El pool no pertenece a esta empresa", HttpStatus.FORBIDDEN);
        }

        Usuario creadoPor = usuarioRepository.findById(request.getCreadoPorId())
                .orElseThrow(() -> new ApiException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        // El usuario debe pertenecer a la empresa
        if (!creadoPor.getEmpresa().getId().equals(request.getEmpresaId())) {
            throw new ApiException("El usuario no pertenece a esta empresa", HttpStatus.FORBIDDEN);
        }

        poolPermissionService.requirePermisoEnPool(creadoPor.getId(), pool.getId(), "PROCESO_CREAR");

        String estado = request.getEstado() != null ? request.getEstado() : "borrador";

        Proceso proceso = new Proceso();
        proceso.setEmpresa(empresa);
        proceso.setPool(pool);
        proceso.setCreatedByUser(creadoPor);
        proceso.setNombre(request.getNombre());
        proceso.setDescripcion(request.getDescripcion());
        proceso.setCategoria(request.getCategoria());
        proceso.setEstado(estado);
        proceso.setActivo(true);
        proceso = procesoRepository.save(proceso);

        return toResponse(proceso);
    }

    // ── Editar ───────────────────────────────────────────────────────────────

    @Transactional
    public ProcesoResponse editar(Integer procesoId, EditarProcesoRequest request) {

        Proceso proceso = procesoRepository.findByIdAndActivoTrue(procesoId)
                .orElseThrow(() -> new ApiException("Proceso no encontrado", HttpStatus.NOT_FOUND));

        Usuario editadoPor = usuarioRepository.findById(request.getEditadoPorId())
                .orElseThrow(() -> new ApiException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        if (!procesoCompartidoService.puedeEditar(proceso, editadoPor.getId(), editadoPor.getEmpresa().getId())) {
            throw new ApiException("El usuario no tiene permiso para editar este proceso", HttpStatus.FORBIDDEN);
        }

        // ── Snapshot antes del cambio ─────────────────────────────────────────
        Map<String, Object> antes = snapshot(proceso);

        // ── Aplicar solo los campos presentes en el request ───────────────────
        if (request.getNombre()      != null) proceso.setNombre(request.getNombre());
        if (request.getDescripcion() != null) proceso.setDescripcion(request.getDescripcion());
        if (request.getCategoria()   != null) proceso.setCategoria(request.getCategoria());
        if (request.getEstado()      != null) proceso.setEstado(request.getEstado());

        // saveAndFlush fuerza el flush inmediato para que @PreUpdate dispare
        // antes de construir el response (evita updatedAt null en la respuesta)
        proceso = procesoRepository.saveAndFlush(proceso);

        // ── Registrar historial ───────────────────────────────────────────────
        auditService.registrar(
                proceso.getEmpresa(),
                editadoPor,
                "PROCESO",
                proceso.getId(),
                "EDITAR",
                antes,
                snapshot(proceso)
        );

        return toResponse(proceso);
    }

    // ── Archivar (soft delete) ────────────────────────────────────────────────

    @Transactional
    public void archivar(Integer procesoId, EliminarProcesoRequest request) {

        Proceso proceso = procesoRepository.findByIdAndActivoTrue(procesoId)
                .orElseThrow(() -> new ApiException("Proceso no encontrado", HttpStatus.NOT_FOUND));

        Usuario eliminadoPor = usuarioRepository.findById(request.getEliminadoPorId())
                .orElseThrow(() -> new ApiException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        // El usuario debe pertenecer a la misma empresa que el proceso
        if (!eliminadoPor.getEmpresa().getId().equals(proceso.getEmpresa().getId())) {
            throw new ApiException("El usuario no pertenece a esta empresa", HttpStatus.FORBIDDEN);
        }

        poolPermissionService.requirePermisoEnPool(eliminadoPor.getId(), proceso.getPool().getId(), "PROCESO_ELIMINAR");

        Map<String, Object> antes = snapshot(proceso);

        // Soft delete: el proceso queda en BD para trazabilidad
        proceso.setActivo(false);
        procesoRepository.save(proceso);

        auditService.registrar(
                proceso.getEmpresa(),
                eliminadoPor,
                "PROCESO",
                proceso.getId(),
                "ARCHIVAR",
                antes,
                snapshot(proceso)
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Map<String, Object> snapshot(Proceso p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nombre",      p.getNombre());
        m.put("descripcion", p.getDescripcion());
        m.put("categoria",   p.getCategoria());
        m.put("estado",      p.getEstado());
        return m;
    }

    private List<Proceso> deduplicar(List<Proceso> procesos) {
        Set<Integer> ids = new LinkedHashSet<>();
        List<Proceso> deduplicados = new ArrayList<>();
        for (Proceso proceso : procesos) {
            if (ids.add(proceso.getId())) {
                deduplicados.add(proceso);
            }
        }
        return deduplicados;
    }

    private Comparator<Proceso> buildComparator(Pageable pageable) {
        Comparator<Proceso> comparator = Comparator.comparing(Proceso::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
        if (pageable.getSort().isUnsorted()) {
            return comparator;
        }

        for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
            Comparator<Proceso> next = switch (order.getProperty()) {
                case "nombre" -> Comparator.comparing(Proceso::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "categoria" -> Comparator.comparing(Proceso::getCategoria, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "estado" -> Comparator.comparing(Proceso::getEstado, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "updatedAt" -> Comparator.comparing(Proceso::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
                default -> Comparator.comparing(Proceso::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            };
            comparator = order.isAscending() ? next : next.reversed();
        }
        return comparator;
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    static ProcesoResponse toResponse(Proceso p) {
        return ProcesoResponse.builder()
                .id(p.getId())
                .empresaId(p.getEmpresa().getId())
                .empresaNombre(p.getEmpresa().getNombre())
                .poolId(p.getPool().getId())
                .poolNombre(p.getPool().getNombre())
                .creadoPorId(p.getCreatedByUser().getId())
                .creadoPorEmail(p.getCreatedByUser().getEmail())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .categoria(p.getCategoria())
                .estado(p.getEstado())
                .activo(p.isActivo())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
