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
import com.lulo.diagram.arc.dto.CrearArcoRequest;
import com.lulo.diagram.arc.dto.EditarArcoRequest;
import com.lulo.diagram.arc.dto.EliminarArcoRequest;
import com.lulo.diagram.arc.dto.ArcoResponse;
import com.lulo.diagram.gateway.Gateway;
import com.lulo.diagram.gateway.GatewayRepository;
import com.lulo.diagram.gateway.dto.CrearGatewayRequest;
import com.lulo.diagram.gateway.dto.EditarGatewayRequest;
import com.lulo.diagram.gateway.dto.EliminarGatewayRequest;
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

    private static final String LANE_NO_ENCONTRADA = "Lane no encontrada";
    private static final String LANE_NO_PERTENECE = "La lane no pertenece a este proceso";
    private static final String TIPO_GATEWAY = "GATEWAY";
    private static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    private static final String USUARIO_NO_EMPRESA = "El usuario no pertenece a esta empresa";
    private static final String ACCION_EDITAR = "EDITAR";
    private static final String ACCION_ELIMINAR = "ELIMINAR";

    private final ProcesoRepository   procesoRepository;
    private final LaneRepository      laneRepository;
    private final NodoRepository      nodoRepository;
    private final ActividadRepository actividadRepository;
    private final GatewayRepository   gatewayRepository;
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
                    .orElseThrow(() -> new ApiException(LANE_NO_ENCONTRADA, HttpStatus.NOT_FOUND));
            // La lane debe pertenecer al mismo proceso
            if (!lane.getProceso().getId().equals(procesoId)) {
                throw new ApiException(LANE_NO_PERTENECE, HttpStatus.FORBIDDEN);
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

    // ── Crear gateway ────────────────────────────────────────────────────────

    @Transactional
    public NodoResponse crearGateway(Integer procesoId, CrearGatewayRequest request) {

        Proceso proceso = requireProcesoActivo(procesoId);
        var creadoPor = requireUsuarioDeEmpresa(request.getCreadoPorId(), proceso.getEmpresa().getId());
        Lane lane = resolveLane(procesoId, request.getLaneId());

        // TODO: verificar permiso DIAGRAMA_EDITAR del usuario en el pool (HU-Auth)

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
                TIPO_GATEWAY,
                gateway.getId(),
                "CREAR",
                null,
                snapshotGateway(gateway)
        );

        return toNodoResponse(gateway);
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
                .orElseThrow(() -> new ApiException(USUARIO_NO_ENCONTRADO, HttpStatus.NOT_FOUND));

        if (!editadoPor.getEmpresa().getId().equals(actividad.getProceso().getEmpresa().getId())) {
            throw new ApiException(USUARIO_NO_EMPRESA, HttpStatus.FORBIDDEN);
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
                    .orElseThrow(() -> new ApiException(LANE_NO_ENCONTRADA, HttpStatus.NOT_FOUND));
            if (!lane.getProceso().getId().equals(procesoId)) {
                throw new ApiException(LANE_NO_PERTENECE, HttpStatus.FORBIDDEN);
            }
            actividad.setLane(lane);
        }

        actividad = actividadRepository.save(actividad);

        auditService.registrar(
                actividad.getProceso().getEmpresa(),
                editadoPor,
                "ACTIVIDAD",
                actividad.getId(),
                ACCION_EDITAR,
                antes,
                snapshotActividad(actividad)
        );

        return toNodoResponse(actividad);
    }

    // ── Editar gateway ───────────────────────────────────────────────────────

    @Transactional
    public NodoResponse editarGateway(Integer procesoId, Integer gatewayId,
                                      EditarGatewayRequest request) {

        Gateway gateway = gatewayRepository.findByIdAndProcesoId(gatewayId, procesoId)
                .orElseThrow(() -> new ApiException("Gateway no encontrado", HttpStatus.NOT_FOUND));

        var editadoPor = requireUsuarioDeEmpresa(request.getEditadoPorId(), gateway.getProceso().getEmpresa().getId());

        // TODO: verificar permiso DIAGRAMA_EDITAR del usuario en el pool (HU-Auth)

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
                TIPO_GATEWAY,
                gateway.getId(),
                ACCION_EDITAR,
                antes,
                snapshotGateway(gateway)
        );

        return toNodoResponse(gateway);
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
                .orElseThrow(() -> new ApiException(USUARIO_NO_ENCONTRADO, HttpStatus.NOT_FOUND));

        if (!eliminadoPor.getEmpresa().getId().equals(actividad.getProceso().getEmpresa().getId())) {
            throw new ApiException(USUARIO_NO_EMPRESA, HttpStatus.FORBIDDEN);
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
                ACCION_ELIMINAR,
                detalle,
                null
        );
    }

    // ── Eliminar gateway ─────────────────────────────────────────────────────

    @Transactional
    public void eliminarGateway(Integer procesoId, Integer gatewayId,
                                EliminarGatewayRequest request) {

        Gateway gateway = gatewayRepository.findByIdAndProcesoId(gatewayId, procesoId)
                .orElseThrow(() -> new ApiException("Gateway no encontrado", HttpStatus.NOT_FOUND));

        var eliminadoPor = requireUsuarioDeEmpresa(request.getEliminadoPorId(), gateway.getProceso().getEmpresa().getId());

        // TODO: verificar permiso DIAGRAMA_EDITAR del usuario en el pool (HU-Auth)

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
                TIPO_GATEWAY,
                gatewayId,
                ACCION_ELIMINAR,
                antes,
                null
        );
    }

    // ── Crear arco ───────────────────────────────────────────────────────────

    @Transactional
    public ArcoResponse crearArco(Integer procesoId, CrearArcoRequest request) {

        Proceso proceso = requireProcesoActivo(procesoId);
        var creadoPor = requireUsuarioDeEmpresa(request.getCreadoPorId(), proceso.getEmpresa().getId());
        Nodo fromNodo = requireNodoDiagramable(procesoId, request.getFromNodoId(), "origen");
        Nodo toNodo = requireNodoDiagramable(procesoId, request.getToNodoId(), "destino");

        validateArcoChange(null, procesoId, fromNodo, toNodo, request.getCondicionExpr());

        // TODO: verificar permiso DIAGRAMA_EDITAR del usuario en el pool (HU-Auth)

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

    // ── Editar arco ──────────────────────────────────────────────────────────

    @Transactional
    public ArcoResponse editarArco(Integer procesoId, Integer arcoId, EditarArcoRequest request) {

        Arco arco = arcoRepository.findByIdAndActivoTrue(arcoId)
                .orElseThrow(() -> new ApiException("Arco no encontrado", HttpStatus.NOT_FOUND));

        if (!arco.getProceso().getId().equals(procesoId)) {
            throw new ApiException("El arco no pertenece a este proceso", HttpStatus.FORBIDDEN);
        }

        var editadoPor = requireUsuarioDeEmpresa(request.getEditadoPorId(), arco.getProceso().getEmpresa().getId());

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

        // TODO: verificar permiso DIAGRAMA_EDITAR del usuario en el pool (HU-Auth)

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
                ACCION_EDITAR,
                antes,
                snapshotArco(arco)
        );

        return toArcoResponse(arco);
    }

    // ── Eliminar arco ────────────────────────────────────────────────────────

    @Transactional
    public void eliminarArco(Integer procesoId, Integer arcoId, EliminarArcoRequest request) {

        Arco arco = arcoRepository.findByIdAndActivoTrue(arcoId)
                .orElseThrow(() -> new ApiException("Arco no encontrado", HttpStatus.NOT_FOUND));

        if (!arco.getProceso().getId().equals(procesoId)) {
            throw new ApiException("El arco no pertenece a este proceso", HttpStatus.FORBIDDEN);
        }

        var eliminadoPor = requireUsuarioDeEmpresa(request.getEliminadoPorId(), arco.getProceso().getEmpresa().getId());

        // TODO: verificar permiso DIAGRAMA_EDITAR del usuario en el pool (HU-Auth)

        Map<String, Object> antes = snapshotArco(arco);
        arco.setActivo(false);
        arcoRepository.save(arco);

        auditService.registrar(
                arco.getProceso().getEmpresa(),
                eliminadoPor,
                "ARCO",
                arco.getId(),
                ACCION_ELIMINAR,
                antes,
                snapshotArco(arco)
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

    private Map<String, Object> snapshotGateway(Gateway g) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("label",       g.getLabel());
        m.put("tipoGateway", g.getTipoGateway());
        m.put("laneId",      g.getLane() != null ? g.getLane().getId() : null);
        m.put("posX",        g.getPosX());
        m.put("posY",        g.getPosY());
        m.put("configJson",  g.getConfigJson());
        return m;
    }

    private Map<String, Object> snapshotArco(Arco a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("fromNodoId",     a.getFromNodo().getId());
        m.put("toNodoId",       a.getToNodo().getId());
        m.put("condicionExpr",  a.getCondicionExpr());
        m.put("propsJson",      a.getPropsJson());
        m.put("activo",         a.isActivo());
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
                .orElseThrow(() -> new ApiException(LANE_NO_ENCONTRADA, HttpStatus.NOT_FOUND));
        if (!lane.getProceso().getId().equals(procesoId)) {
            throw new ApiException(LANE_NO_PERTENECE, HttpStatus.FORBIDDEN);
        }
        return lane;
    }

    private Nodo requireNodoDiagramable(Integer procesoId, Integer nodoId, String etiqueta) {
        Nodo nodo = nodoRepository.findById(nodoId)
                .orElseThrow(() -> new ApiException("Nodo " + etiqueta + " no encontrado", HttpStatus.NOT_FOUND));

        if (!nodo.getProceso().getId().equals(procesoId)) {
            throw new ApiException("El nodo " + etiqueta + " no pertenece a este proceso", HttpStatus.FORBIDDEN);
        }

        if (!(nodo instanceof Actividad) && !(nodo instanceof Gateway)) {
            throw new ApiException(
                    "El nodo " + etiqueta + " debe ser una actividad o un gateway",
                    HttpStatus.BAD_REQUEST);
        }

        return nodo;
    }

    private void validateArcoChange(Integer arcoId, Integer procesoId,
                                    Nodo fromNodo, Nodo toNodo, String condicionExpr) {
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

    private com.lulo.users.Usuario requireUsuarioDeEmpresa(Integer usuarioId, Integer empresaId) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ApiException(USUARIO_NO_ENCONTRADO, HttpStatus.NOT_FOUND));

        if (!usuario.getEmpresa().getId().equals(empresaId)) {
            throw new ApiException(USUARIO_NO_EMPRESA, HttpStatus.FORBIDDEN);
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
        String tipo;
        if (nodo instanceof Actividad) {
            tipo = "actividad";
        } else if (nodo instanceof Gateway) {
            tipo = "gateway";
        } else {
            tipo = "nodo";
        }

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
