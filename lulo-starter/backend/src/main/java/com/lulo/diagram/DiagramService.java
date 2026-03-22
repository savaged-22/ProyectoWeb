package com.lulo.diagram;

import com.lulo.audit.AuditService;
import com.lulo.common.exception.ApiException;
import com.lulo.diagram.activity.Actividad;
import com.lulo.diagram.activity.ActividadRepository;
import com.lulo.diagram.activity.dto.CrearActividadRequest;
import com.lulo.diagram.activity.dto.EditarActividadRequest;
import com.lulo.diagram.activity.dto.EliminarActividadRequest;
import com.lulo.diagram.arc.Arco;
import com.lulo.diagram.arc.ArcoRepository;
import com.lulo.diagram.arc.dto.ArcoResponse;
import com.lulo.diagram.gateway.Gateway;
import com.lulo.diagram.lane.Lane;
import com.lulo.diagram.lane.LaneRepository;
import com.lulo.diagram.lane.dto.LaneResponse;
import com.lulo.diagram.node.Nodo;
import com.lulo.diagram.node.NodoRepository;
import com.lulo.diagram.node.dto.NodoResponse;
import com.lulo.process.Proceso;
import com.lulo.process.ProcesoRepository;
import com.lulo.users.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiagramService {

    private final ProcesoRepository   procesoRepository;
    private final LaneRepository      laneRepository;
    private final NodoRepository      nodoRepository;
    private final ActividadRepository actividadRepository;
    private final ArcoRepository      arcoRepository;
    private final UsuarioRepository   usuarioRepository;
    private final AuditService        auditService;

    // ── Crear actividad ───────────────────────────────────────────────────────

    @Transactional
    public NodoResponse crearActividad(Integer procesoId, CrearActividadRequest request) {

        Proceso proceso = procesoRepository.findByIdAndActivoTrue(procesoId)
                .orElseThrow(() -> new ApiException("Proceso no encontrado", HttpStatus.NOT_FOUND));

        Lane lane = null;
        if (request.getLaneId() != null) {
            lane = laneRepository.findById(request.getLaneId())
                    .orElseThrow(() -> new ApiException("Lane no encontrada", HttpStatus.NOT_FOUND));
            // La lane debe pertenecer al mismo proceso
            if (!lane.getProceso().getId().equals(procesoId)) {
                throw new ApiException("La lane no pertenece a este proceso", HttpStatus.FORBIDDEN);
            }
        }

        // TODO: verificar permiso DIAGRAMA_EDITAR del usuario en el pool (HU-Auth)

        Actividad actividad = new Actividad();
        actividad.setProceso(proceso);
        actividad.setLane(lane);
        actividad.setLabel(request.getLabel());
        actividad.setPosX(request.getPosX());
        actividad.setPosY(request.getPosY());
        actividad.setTipoActividad(request.getTipoActividad());
        actividad.setPropsJson(request.getPropsJson());
        actividad = actividadRepository.save(actividad);

        return toNodoResponse(actividad);
    }

    // ── Editar actividad ──────────────────────────────────────────────────────

    @Transactional
    public NodoResponse editarActividad(Integer procesoId, Integer actividadId,
                                        EditarActividadRequest request) {

        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ApiException("Actividad no encontrada", HttpStatus.NOT_FOUND));

        if (!actividad.getProceso().getId().equals(procesoId)) {
            throw new ApiException("La actividad no pertenece a este proceso", HttpStatus.FORBIDDEN);
        }

        var editadoPor = usuarioRepository.findById(request.getEditadoPorId())
                .orElseThrow(() -> new ApiException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        if (!editadoPor.getEmpresa().getId().equals(actividad.getProceso().getEmpresa().getId())) {
            throw new ApiException("El usuario no pertenece a esta empresa", HttpStatus.FORBIDDEN);
        }

        // TODO: verificar permiso DIAGRAMA_EDITAR del usuario en el pool (HU-Auth)

        Map<String, Object> antes = snapshotActividad(actividad);

        if (request.getLabel()         != null) actividad.setLabel(request.getLabel());
        if (request.getTipoActividad() != null) actividad.setTipoActividad(request.getTipoActividad());
        if (request.getPosX()          != null) actividad.setPosX(request.getPosX());
        if (request.getPosY()          != null) actividad.setPosY(request.getPosY());
        if (request.getPropsJson()     != null) actividad.setPropsJson(request.getPropsJson());

        if (request.getLaneId() != null) {
            Lane lane = laneRepository.findById(request.getLaneId())
                    .orElseThrow(() -> new ApiException("Lane no encontrada", HttpStatus.NOT_FOUND));
            if (!lane.getProceso().getId().equals(procesoId)) {
                throw new ApiException("La lane no pertenece a este proceso", HttpStatus.FORBIDDEN);
            }
            actividad.setLane(lane);
        }

        actividad = actividadRepository.save(actividad);

        auditService.registrar(
                actividad.getProceso().getEmpresa(),
                editadoPor,
                "ACTIVIDAD",
                actividad.getId(),
                "EDITAR",
                antes,
                snapshotActividad(actividad)
        );

        return toNodoResponse(actividad);
    }

    // ── Eliminar actividad ────────────────────────────────────────────────────

    @Transactional
    public void eliminarActividad(Integer procesoId, Integer actividadId,
                                  EliminarActividadRequest request) {

        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ApiException("Actividad no encontrada", HttpStatus.NOT_FOUND));

        if (!actividad.getProceso().getId().equals(procesoId)) {
            throw new ApiException("La actividad no pertenece a este proceso", HttpStatus.FORBIDDEN);
        }

        var eliminadoPor = usuarioRepository.findById(request.getEliminadoPorId())
                .orElseThrow(() -> new ApiException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        if (!eliminadoPor.getEmpresa().getId().equals(actividad.getProceso().getEmpresa().getId())) {
            throw new ApiException("El usuario no pertenece a esta empresa", HttpStatus.FORBIDDEN);
        }

        // TODO: verificar permiso DIAGRAMA_EDITAR del usuario en el pool (HU-Auth)

        Map<String, Object> snapshotAntes = snapshotActividad(actividad);

        // ── Ajuste automático del flujo ───────────────────────────────────────
        // Los arcos tienen FK a nodo — deben eliminarse físicamente antes que el nodo
        // para no violar la integridad referencial.
        Integer nodoId = actividad.getId(); // con JOINED el ID de Actividad == ID de Nodo
        List<Arco> arcosConectados = new java.util.ArrayList<>();
        arcosConectados.addAll(arcoRepository.findByFromNodoId(nodoId));
        arcosConectados.addAll(arcoRepository.findByToNodoId(nodoId));
        arcoRepository.deleteAll(arcosConectados);

        // ── Eliminación física de la actividad y su nodo base ─────────────────
        // JPA elimina primero de `actividad`, luego de `nodo` (orden FK)
        actividadRepository.delete(actividad);

        // ── Historial ─────────────────────────────────────────────────────────
        Map<String, Object> detalle = new LinkedHashMap<>(snapshotAntes);
        detalle.put("arcosEliminados", arcosConectados.size());

        auditService.registrar(
                actividad.getProceso().getEmpresa(),
                eliminadoPor,
                "ACTIVIDAD",
                actividadId,
                "ELIMINAR",
                detalle,
                null
        );
    }

    // ── Consultas del diagrama ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<LaneResponse> getLanes(Integer procesoId) {
        return laneRepository.findByProcesoIdOrderByOrdenAsc(procesoId)
                .stream()
                .map(DiagramService::toLaneResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NodoResponse> getNodos(Integer procesoId) {
        return nodoRepository.findByProcesoId(procesoId)
                .stream()
                .map(DiagramService::toNodoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ArcoResponse> getArcos(Integer procesoId) {
        return arcoRepository.findByProcesoIdAndActivoTrue(procesoId)
                .stream()
                .map(DiagramService::toArcoResponse)
                .toList();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Map<String, Object> snapshotActividad(Actividad a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("label",         a.getLabel());
        m.put("tipoActividad", a.getTipoActividad());
        m.put("laneId",        a.getLane() != null ? a.getLane().getId() : null);
        m.put("posX",          a.getPosX());
        m.put("posY",          a.getPosY());
        m.put("propsJson",     a.getPropsJson());
        return m;
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    public static LaneResponse toLaneResponse(Lane lane) {
        return LaneResponse.builder()
                .id(lane.getId())
                .rolProcesoId(lane.getRolProceso() != null ? lane.getRolProceso().getId() : null)
                .rolProcesoNombre(lane.getRolProceso() != null ? lane.getRolProceso().getNombre() : null)
                .nombre(lane.getNombre())
                .orden(lane.getOrden())
                .createdAt(lane.getCreatedAt())
                .build();
    }

    /**
     * Mapeo polimórfico: Hibernate instancia Actividad o Gateway según el discriminador.
     * Java 21 pattern matching para extraer los campos especializados.
     */
    public static NodoResponse toNodoResponse(Nodo nodo) {
        // Determinar tipo vía instanceof: el campo discriminador no se popula
        // en memoria tras un INSERT (insertable=false, updatable=false)
        String tipo = nodo instanceof Actividad ? "actividad"
                    : nodo instanceof Gateway   ? "gateway"
                    : "nodo";

        NodoResponse.NodoResponseBuilder builder = NodoResponse.builder()
                .id(nodo.getId())
                .laneId(nodo.getLane() != null ? nodo.getLane().getId() : null)
                .tipo(tipo)
                .label(nodo.getLabel())
                .posX(nodo.getPosX())
                .posY(nodo.getPosY())
                .createdAt(nodo.getCreatedAt());

        if (nodo instanceof Actividad a) {
            builder.tipoActividad(a.getTipoActividad())
                   .propsJson(a.getPropsJson());
        } else if (nodo instanceof Gateway g) {
            builder.tipoGateway(g.getTipoGateway())
                   .configJson(g.getConfigJson());
        }

        return builder.build();
    }

    public static ArcoResponse toArcoResponse(Arco arco) {
        return ArcoResponse.builder()
                .id(arco.getId())
                .fromNodoId(arco.getFromNodo().getId())
                .toNodoId(arco.getToNodo().getId())
                .condicionExpr(arco.getCondicionExpr())
                .propsJson(arco.getPropsJson())
                .activo(arco.isActivo())
                .createdAt(arco.getCreatedAt())
                .build();
    }
}
