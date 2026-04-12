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
import com.lulo.diagram.arc.dto.CrearArcoRequest;
import com.lulo.diagram.arc.dto.EditarArcoRequest;
import com.lulo.diagram.arc.dto.EliminarArcoRequest;
import com.lulo.diagram.gateway.Gateway;
import com.lulo.diagram.gateway.GatewayRepository;
import com.lulo.diagram.gateway.dto.CrearGatewayRequest;
import com.lulo.diagram.gateway.dto.EditarGatewayRequest;
import com.lulo.diagram.gateway.dto.EliminarGatewayRequest;
import com.lulo.diagram.lane.Lane;
import com.lulo.diagram.lane.LaneRepository;
import com.lulo.diagram.lane.dto.CrearLaneRequest;
import com.lulo.diagram.lane.dto.EditarLaneRequest;
import com.lulo.diagram.lane.dto.EliminarLaneRequest;
import com.lulo.diagram.lane.dto.LaneResponse;
import com.lulo.diagram.node.Nodo;
import com.lulo.diagram.node.NodoRepository;
import com.lulo.diagram.node.dto.NodoResponse;
import com.lulo.process.Proceso;
import com.lulo.process.ProcesoRepository;
import com.lulo.rbac.PoolPermissionService;
import com.lulo.rbac.RolProceso;
import com.lulo.rbac.RolProcesoRepository;
import com.lulo.sharing.ProcesoCompartidoService;
import com.lulo.users.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiagramService {

    private final ProcesoRepository procesoRepository;
    private final LaneRepository laneRepository;
    private final NodoRepository nodoRepository;
    private final ActividadRepository actividadRepository;
    private final GatewayRepository gatewayRepository;
    private final ArcoRepository arcoRepository;
    private final AuditService auditService;
    private final PoolPermissionService poolPermissionService;
    private final RolProcesoRepository rolProcesoRepository;
    private final ProcesoCompartidoService procesoCompartidoService;

    @Transactional
    public NodoResponse crearActividad(Integer procesoId, CrearActividadRequest request) {
        Proceso proceso = requireProcesoActivo(procesoId);
        requireUsuarioConEdicionDiagrama(proceso, request.getCreadoPorId());
        Lane lane = resolveLane(procesoId, request.getLaneId());

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

    @Transactional
    public NodoResponse crearGateway(Integer procesoId, CrearGatewayRequest request) {
        Proceso proceso = requireProcesoActivo(procesoId);
        Usuario creadoPor = requireUsuarioConEdicionDiagrama(proceso, request.getCreadoPorId());
        Lane lane = resolveLane(procesoId, request.getLaneId());

        Gateway gateway = new Gateway();
        gateway.setProceso(proceso);
        gateway.setLane(lane);
        gateway.setLabel(request.getLabel());
        gateway.setPosX(request.getPosX());
        gateway.setPosY(request.getPosY());
        gateway.setTipoGateway(request.getTipoGateway());
        gateway.setConfigJson(request.getConfigJson());
        gateway = gatewayRepository.save(gateway);

        auditService.registrar(
                proceso.getEmpresa(),
                creadoPor,
                "GATEWAY",
                gateway.getId(),
                "CREAR",
                null,
                snapshotGateway(gateway)
        );

        return toNodoResponse(gateway);
    }

    @Transactional
    public ArcoResponse crearArco(Integer procesoId, CrearArcoRequest request) {
        Proceso proceso = requireProcesoActivo(procesoId);
        Usuario creadoPor = requireUsuarioConEdicionDiagrama(proceso, request.getCreadoPorId());
        Nodo fromNodo = requireNodoDiagramable(procesoId, request.getFromNodoId(), "origen");
        Nodo toNodo = requireNodoDiagramable(procesoId, request.getToNodoId(), "destino");

        validateArcoChange(null, procesoId, fromNodo, toNodo, request.getCondicionExpr());

        Arco arco = new Arco();
        arco.setProceso(proceso);
        arco.setFromNodo(fromNodo);
        arco.setToNodo(toNodo);
        arco.setCondicionExpr(normalize(request.getCondicionExpr()));
        arco.setPropsJson(request.getPropsJson());
        arco = arcoRepository.save(arco);

        auditService.registrar(
                proceso.getEmpresa(),
                creadoPor,
                "ARCO",
                arco.getId(),
                "CREAR",
                null,
                snapshotArco(arco)
        );

        return toArcoResponse(arco);
    }

    @Transactional
    public LaneResponse crearLane(Integer procesoId, CrearLaneRequest request) {
        Proceso proceso = requireProcesoActivo(procesoId);
        Usuario creadoPor = requireUsuarioConEdicionDiagrama(proceso, request.getCreadoPorId());

        String nombre = normalizeRequired(request.getNombre(), "El nombre de la lane es obligatorio");
        if (laneRepository.existsByProcesoIdAndNombre(procesoId, nombre)) {
            throw new ApiException("Ya existe una lane con ese nombre en el proceso", HttpStatus.CONFLICT);
        }

        Lane lane = new Lane();
        lane.setProceso(proceso);
        lane.setRolProceso(resolveRolProceso(proceso.getEmpresa().getId(), request.getRolProcesoId()));
        lane.setNombre(nombre);
        lane.setOrden(request.getOrden() != null ? request.getOrden() : 0);
        lane = laneRepository.save(lane);

        auditService.registrar(
                proceso.getEmpresa(),
                creadoPor,
                "LANE",
                lane.getId(),
                "CREAR",
                null,
                snapshotLane(lane)
        );

        return toLaneResponse(lane);
    }

    @Transactional
    public NodoResponse editarActividad(Integer procesoId, Integer actividadId, EditarActividadRequest request) {
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ApiException("Actividad no encontrada", HttpStatus.NOT_FOUND));
        if (!actividad.getProceso().getId().equals(procesoId)) {
            throw new ApiException("La actividad no pertenece a este proceso", HttpStatus.FORBIDDEN);
        }

        Usuario editadoPor = requireUsuarioConEdicionDiagrama(actividad.getProceso(), request.getEditadoPorId());
        Map<String, Object> antes = snapshotActividad(actividad);

        if (request.getLabel() != null) actividad.setLabel(request.getLabel());
        if (request.getTipoActividad() != null) actividad.setTipoActividad(request.getTipoActividad());
        if (request.getPosX() != null) actividad.setPosX(request.getPosX());
        if (request.getPosY() != null) actividad.setPosY(request.getPosY());
        if (request.getPropsJson() != null) actividad.setPropsJson(request.getPropsJson());
        if (request.getLaneId() != null) actividad.setLane(resolveLane(procesoId, request.getLaneId()));

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

    @Transactional
    public NodoResponse editarGateway(Integer procesoId, Integer gatewayId, EditarGatewayRequest request) {
        Gateway gateway = gatewayRepository.findByIdAndProcesoId(gatewayId, procesoId)
                .orElseThrow(() -> new ApiException("Gateway no encontrado", HttpStatus.NOT_FOUND));

        Usuario editadoPor = requireUsuarioConEdicionDiagrama(gateway.getProceso(), request.getEditadoPorId());
        Map<String, Object> antes = snapshotGateway(gateway);

        if (request.getLabel() != null) gateway.setLabel(request.getLabel());
        if (request.getPosX() != null) gateway.setPosX(request.getPosX());
        if (request.getPosY() != null) gateway.setPosY(request.getPosY());
        if (request.getTipoGateway() != null) gateway.setTipoGateway(request.getTipoGateway());
        if (request.getConfigJson() != null) gateway.setConfigJson(request.getConfigJson());
        if (request.getLaneId() != null) gateway.setLane(resolveLane(procesoId, request.getLaneId()));

        validateGatewayOutgoingConsistency(gateway, gateway.getTipoGateway());
        gateway = gatewayRepository.save(gateway);

        auditService.registrar(
                gateway.getProceso().getEmpresa(),
                editadoPor,
                "GATEWAY",
                gateway.getId(),
                "EDITAR",
                antes,
                snapshotGateway(gateway)
        );

        return toNodoResponse(gateway);
    }

    @Transactional
    public ArcoResponse editarArco(Integer procesoId, Integer arcoId, EditarArcoRequest request) {
        Arco arco = arcoRepository.findByIdAndActivoTrue(arcoId)
                .orElseThrow(() -> new ApiException("Arco no encontrado", HttpStatus.NOT_FOUND));
        if (!arco.getProceso().getId().equals(procesoId)) {
            throw new ApiException("El arco no pertenece a este proceso", HttpStatus.FORBIDDEN);
        }

        Usuario editadoPor = requireUsuarioConEdicionDiagrama(arco.getProceso(), request.getEditadoPorId());
        Nodo fromNodo = request.getFromNodoId() != null
                ? requireNodoDiagramable(procesoId, request.getFromNodoId(), "origen")
                : arco.getFromNodo();
        Nodo toNodo = request.getToNodoId() != null
                ? requireNodoDiagramable(procesoId, request.getToNodoId(), "destino")
                : arco.getToNodo();
        String condicionExpr = request.getCondicionExpr() != null
                ? normalize(request.getCondicionExpr())
                : arco.getCondicionExpr();

        validateArcoChange(arco.getId(), procesoId, fromNodo, toNodo, condicionExpr);

        Map<String, Object> antes = snapshotArco(arco);
        arco.setFromNodo(fromNodo);
        arco.setToNodo(toNodo);
        arco.setCondicionExpr(condicionExpr);
        if (request.getPropsJson() != null) arco.setPropsJson(request.getPropsJson());
        arco = arcoRepository.save(arco);

        auditService.registrar(
                arco.getProceso().getEmpresa(),
                editadoPor,
                "ARCO",
                arco.getId(),
                "EDITAR",
                antes,
                snapshotArco(arco)
        );

        return toArcoResponse(arco);
    }

    @Transactional
    public LaneResponse editarLane(Integer procesoId, Integer laneId, EditarLaneRequest request) {
        Proceso proceso = requireProcesoActivo(procesoId);
        Lane lane = laneRepository.findById(laneId)
                .orElseThrow(() -> new ApiException("Lane no encontrada", HttpStatus.NOT_FOUND));
        if (!lane.getProceso().getId().equals(procesoId)) {
            throw new ApiException("La lane no pertenece a este proceso", HttpStatus.FORBIDDEN);
        }

        Usuario editadoPor = requireUsuarioConEdicionDiagrama(proceso, request.getEditadoPorId());
        Map<String, Object> antes = snapshotLane(lane);
        String nombre = request.getNombre() != null
                ? normalizeRequired(request.getNombre(), "El nombre de la lane no puede estar vacío")
                : null;

        if (nombre != null &&
                laneRepository.existsByProcesoIdAndNombreExcluyendoId(procesoId, nombre, laneId)) {
            throw new ApiException("Ya existe una lane con ese nombre en el proceso", HttpStatus.CONFLICT);
        }

        if (nombre != null) lane.setNombre(nombre);
        if (request.getOrden() != null) lane.setOrden(request.getOrden());
        if (Boolean.TRUE.equals(request.getLimpiarRolProceso())) {
            lane.setRolProceso(null);
        } else if (request.getRolProcesoId() != null) {
            lane.setRolProceso(resolveRolProceso(proceso.getEmpresa().getId(), request.getRolProcesoId()));
        }

        lane = laneRepository.save(lane);

        auditService.registrar(
                proceso.getEmpresa(),
                editadoPor,
                "LANE",
                lane.getId(),
                "EDITAR",
                antes,
                snapshotLane(lane)
        );

        return toLaneResponse(lane);
    }

    @Transactional
    public void eliminarActividad(Integer procesoId, Integer actividadId, EliminarActividadRequest request) {
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ApiException("Actividad no encontrada", HttpStatus.NOT_FOUND));
        if (!actividad.getProceso().getId().equals(procesoId)) {
            throw new ApiException("La actividad no pertenece a este proceso", HttpStatus.FORBIDDEN);
        }

        Usuario eliminadoPor = requireUsuarioConEdicionDiagrama(actividad.getProceso(), request.getEliminadoPorId());
        Map<String, Object> snapshotAntes = snapshotActividad(actividad);

        Integer nodoId = actividad.getId();
        List<Arco> arcosConectados = new java.util.ArrayList<>();
        arcosConectados.addAll(arcoRepository.findByFromNodoId(nodoId));
        arcosConectados.addAll(arcoRepository.findByToNodoId(nodoId));
        arcoRepository.deleteAll(arcosConectados);
        actividadRepository.delete(actividad);

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

    @Transactional
    public void eliminarGateway(Integer procesoId, Integer gatewayId, EliminarGatewayRequest request) {
        Gateway gateway = gatewayRepository.findByIdAndProcesoId(gatewayId, procesoId)
                .orElseThrow(() -> new ApiException("Gateway no encontrado", HttpStatus.NOT_FOUND));

        Usuario eliminadoPor = requireUsuarioConEdicionDiagrama(gateway.getProceso(), request.getEliminadoPorId());
        List<Arco> arcosEntrantes = arcoRepository.findByToNodoIdAndActivoTrue(gatewayId);
        List<Arco> arcosSalientes = arcoRepository.findByFromNodoIdAndActivoTrue(gatewayId);
        if (!arcosEntrantes.isEmpty() || !arcosSalientes.isEmpty()) {
            throw new ApiException(
                    "No se puede eliminar el gateway mientras tenga arcos activos conectados",
                    HttpStatus.CONFLICT);
        }

        Map<String, Object> antes = snapshotGateway(gateway);
        gatewayRepository.delete(gateway);

        auditService.registrar(
                gateway.getProceso().getEmpresa(),
                eliminadoPor,
                "GATEWAY",
                gatewayId,
                "ELIMINAR",
                antes,
                null
        );
    }

    @Transactional
    public void eliminarArco(Integer procesoId, Integer arcoId, EliminarArcoRequest request) {
        Arco arco = arcoRepository.findByIdAndActivoTrue(arcoId)
                .orElseThrow(() -> new ApiException("Arco no encontrado", HttpStatus.NOT_FOUND));
        if (!arco.getProceso().getId().equals(procesoId)) {
            throw new ApiException("El arco no pertenece a este proceso", HttpStatus.FORBIDDEN);
        }

        Usuario eliminadoPor = requireUsuarioConEdicionDiagrama(arco.getProceso(), request.getEliminadoPorId());
        Map<String, Object> antes = snapshotArco(arco);
        arco.setActivo(false);
        arcoRepository.save(arco);

        auditService.registrar(
                arco.getProceso().getEmpresa(),
                eliminadoPor,
                "ARCO",
                arco.getId(),
                "ELIMINAR",
                antes,
                snapshotArco(arco)
        );
    }

    @Transactional
    public void eliminarLane(Integer procesoId, Integer laneId, EliminarLaneRequest request) {
        Proceso proceso = requireProcesoActivo(procesoId);
        Lane lane = laneRepository.findById(laneId)
                .orElseThrow(() -> new ApiException("Lane no encontrada", HttpStatus.NOT_FOUND));
        if (!lane.getProceso().getId().equals(procesoId)) {
            throw new ApiException("La lane no pertenece a este proceso", HttpStatus.FORBIDDEN);
        }

        Usuario eliminadoPor = requireUsuarioConEdicionDiagrama(proceso, request.getEliminadoPorId());
        if (!nodoRepository.findByProcesoIdAndLaneId(procesoId, laneId).isEmpty()) {
            throw new ApiException("No se puede eliminar una lane que tiene nodos asignados", HttpStatus.CONFLICT);
        }

        Map<String, Object> antes = snapshotLane(lane);
        laneRepository.delete(lane);

        auditService.registrar(
                proceso.getEmpresa(),
                eliminadoPor,
                "LANE",
                laneId,
                "ELIMINAR",
                antes,
                null
        );
    }

    @Transactional(readOnly = true)
    public List<LaneResponse> getLanes(Integer procesoId) {
        return laneRepository.findByProcesoIdOrderByOrdenAsc(procesoId).stream()
                .map(DiagramService::toLaneResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NodoResponse> getNodos(Integer procesoId) {
        return nodoRepository.findByProcesoId(procesoId).stream()
                .map(DiagramService::toNodoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ArcoResponse> getArcos(Integer procesoId) {
        return arcoRepository.findByProcesoIdAndActivoTrue(procesoId).stream()
                .map(DiagramService::toArcoResponse)
                .toList();
    }

    private Map<String, Object> snapshotActividad(Actividad actividad) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("label", actividad.getLabel());
        m.put("tipoActividad", actividad.getTipoActividad());
        m.put("laneId", actividad.getLane() != null ? actividad.getLane().getId() : null);
        m.put("posX", actividad.getPosX());
        m.put("posY", actividad.getPosY());
        m.put("propsJson", actividad.getPropsJson());
        return m;
    }

    private Map<String, Object> snapshotGateway(Gateway gateway) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("label", gateway.getLabel());
        m.put("tipoGateway", gateway.getTipoGateway());
        m.put("laneId", gateway.getLane() != null ? gateway.getLane().getId() : null);
        m.put("posX", gateway.getPosX());
        m.put("posY", gateway.getPosY());
        m.put("configJson", gateway.getConfigJson());
        return m;
    }

    private Map<String, Object> snapshotArco(Arco arco) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("fromNodoId", arco.getFromNodo().getId());
        m.put("toNodoId", arco.getToNodo().getId());
        m.put("condicionExpr", arco.getCondicionExpr());
        m.put("propsJson", arco.getPropsJson());
        m.put("activo", arco.isActivo());
        return m;
    }

    private Map<String, Object> snapshotLane(Lane lane) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nombre", lane.getNombre());
        m.put("orden", lane.getOrden());
        m.put("rolProcesoId", lane.getRolProceso() != null ? lane.getRolProceso().getId() : null);
        return m;
    }

    private Proceso requireProcesoActivo(Integer procesoId) {
        return procesoRepository.findByIdAndActivoTrue(procesoId)
                .orElseThrow(() -> new ApiException("Proceso no encontrado", HttpStatus.NOT_FOUND));
    }

    private Lane resolveLane(Integer procesoId, Integer laneId) {
        if (laneId == null) {
            return null;
        }

        Lane lane = laneRepository.findById(laneId)
                .orElseThrow(() -> new ApiException("Lane no encontrada", HttpStatus.NOT_FOUND));
        if (!lane.getProceso().getId().equals(procesoId)) {
            throw new ApiException("La lane no pertenece a este proceso", HttpStatus.FORBIDDEN);
        }
        return lane;
    }

    private RolProceso resolveRolProceso(UUID empresaId, Integer rolProcesoId) {
        if (rolProcesoId == null) {
            return null;
        }

        RolProceso rolProceso = rolProcesoRepository.findByIdAndActivoTrue(rolProcesoId)
                .orElseThrow(() -> new ApiException("Rol de proceso no encontrado", HttpStatus.NOT_FOUND));
        if (!rolProceso.getEmpresa().getId().equals(empresaId)) {
            throw new ApiException("El rol de proceso no pertenece a esta empresa", HttpStatus.FORBIDDEN);
        }
        return rolProceso;
    }

    private Nodo requireNodoDiagramable(Integer procesoId, Integer nodoId, String etiqueta) {
        Nodo nodo = nodoRepository.findById(nodoId)
                .orElseThrow(() -> new ApiException("Nodo " + etiqueta + " no encontrado", HttpStatus.NOT_FOUND));
        if (!nodo.getProceso().getId().equals(procesoId)) {
            throw new ApiException("El nodo " + etiqueta + " no pertenece a este proceso", HttpStatus.FORBIDDEN);
        }
        if (!(nodo instanceof Actividad) && !(nodo instanceof Gateway)) {
            throw new ApiException("El nodo " + etiqueta + " debe ser una actividad o un gateway", HttpStatus.BAD_REQUEST);
        }
        return nodo;
    }

    private void validateArcoChange(Integer arcoId, Integer procesoId, Nodo fromNodo, Nodo toNodo, String condicionExpr) {
        if (fromNodo.getId().equals(toNodo.getId())) {
            throw new ApiException("El nodo origen y destino no pueden ser el mismo", HttpStatus.BAD_REQUEST);
        }

        boolean duplicado = arcoId == null
                ? arcoRepository.existsByProcesoIdAndFromNodoIdAndToNodoIdAndActivoTrue(
                        procesoId, fromNodo.getId(), toNodo.getId())
                : arcoRepository.existsActivoDuplicadoExcluyendoId(
                        procesoId, fromNodo.getId(), toNodo.getId(), arcoId);
        if (duplicado) {
            throw new ApiException("Ya existe un arco activo con ese origen y destino", HttpStatus.CONFLICT);
        }

        validateOutgoingConditionRules(fromNodo, condicionExpr);
    }

    private void validateOutgoingConditionRules(Nodo fromNodo, String condicionExpr) {
        String condicionNormalizada = normalize(condicionExpr);
        if (fromNodo instanceof Gateway gateway) {
            if ("paralelo".equals(gateway.getTipoGateway()) && condicionNormalizada != null) {
                throw new ApiException(
                        "Los arcos salientes de un gateway paralelo no pueden tener condición",
                        HttpStatus.BAD_REQUEST);
            }
            return;
        }

        if (condicionNormalizada != null) {
            throw new ApiException(
                    "Solo los gateways exclusivos o inclusivos pueden definir condiciones de salida",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void validateGatewayOutgoingConsistency(Gateway gateway, String tipoGateway) {
        if (!"paralelo".equals(tipoGateway)) {
            return;
        }

        boolean tieneCondiciones = arcoRepository.findByFromNodoIdAndActivoTrue(gateway.getId()).stream()
                .map(Arco::getCondicionExpr)
                .map(this::normalize)
                .anyMatch(java.util.Objects::nonNull);

        if (tieneCondiciones) {
            throw new ApiException(
                    "No se puede convertir el gateway a paralelo mientras existan arcos salientes con condición",
                    HttpStatus.CONFLICT);
        }
    }

    private Usuario requireUsuarioConEdicionDiagrama(Proceso proceso, Integer usuarioId) {
        Usuario usuario = poolPermissionService.requireUsuario(usuarioId);
        if (!procesoCompartidoService.puedeEditarDiagrama(proceso, usuario.getId(), usuario.getEmpresa().getId())) {
            throw new ApiException("El usuario no tiene permiso para editar el diagrama", HttpStatus.FORBIDDEN);
        }
        return usuario;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalize(value);
        if (normalized == null) {
            throw new ApiException(message, HttpStatus.BAD_REQUEST);
        }
        return normalized;
    }

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

    public static NodoResponse toNodoResponse(Nodo nodo) {
        String tipo = nodo instanceof Actividad ? "actividad"
                : nodo instanceof Gateway ? "gateway"
                : "nodo";

        NodoResponse.NodoResponseBuilder builder = NodoResponse.builder()
                .id(nodo.getId())
                .laneId(nodo.getLane() != null ? nodo.getLane().getId() : null)
                .tipo(tipo)
                .label(nodo.getLabel())
                .posX(nodo.getPosX())
                .posY(nodo.getPosY())
                .createdAt(nodo.getCreatedAt());

        if (nodo instanceof Actividad actividad) {
            builder.tipoActividad(actividad.getTipoActividad())
                    .propsJson(actividad.getPropsJson());
        } else if (nodo instanceof Gateway gateway) {
            builder.tipoGateway(gateway.getTipoGateway())
                    .configJson(gateway.getConfigJson());
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
