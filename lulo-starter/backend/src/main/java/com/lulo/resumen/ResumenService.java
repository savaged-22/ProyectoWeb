package com.lulo.resumen;

import com.lulo.company.Empresa;
import com.lulo.company.EmpresaRepository;
import com.lulo.diagram.activity.Actividad;
import com.lulo.diagram.arc.Arco;
import com.lulo.diagram.arc.ArcoRepository;
import com.lulo.diagram.gateway.Gateway;
import com.lulo.diagram.lane.Lane;
import com.lulo.diagram.lane.LaneRepository;
import com.lulo.diagram.node.Nodo;
import com.lulo.diagram.node.NodoRepository;
import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import com.lulo.process.Proceso;
import com.lulo.process.ProcesoRepository;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.RolPoolRepository;
import com.lulo.rbac.RolProceso;
import com.lulo.rbac.RolProcesoRepository;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolRepository;
import com.lulo.resumen.dto.ResumenResponse;
import com.lulo.resumen.dto.ResumenResponse.*;
import com.lulo.sharing.ProcesoCompartido;
import com.lulo.sharing.ProcesoCompartidoRepository;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumenService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PoolRepository poolRepository;
    private final RolPoolRepository rolPoolRepository;
    private final UsuarioRolPoolRepository usuarioRolPoolRepository;
    private final RolProcesoRepository rolProcesoRepository;
    private final ProcesoRepository procesoRepository;
    private final LaneRepository laneRepository;
    private final NodoRepository nodoRepository;
    private final ArcoRepository arcoRepository;
    private final ProcesoCompartidoRepository procesoCompartidoRepository;

    @Transactional(readOnly = true)
    public ResumenResponse construir() {
        List<Empresa> empresas = empresaRepository.findAll();

        List<EmpresaResumen> empresasResumen = empresas.stream()
                .map(this::mapEmpresa)
                .toList();

        Estadisticas estadisticas = calcularEstadisticas(empresasResumen);

        return ResumenResponse.builder()
                .estadisticas(estadisticas)
                .empresas(empresasResumen)
                .build();
    }

    // ── Empresa ───────────────────────────────────────────────────────────────

    private EmpresaResumen mapEmpresa(Empresa e) {
        List<UsuarioResumen> usuarios = usuarioRepository.findByEmpresaId(e.getId())
                .stream().map(this::mapUsuario).toList();

        List<RolProcesoResumen> rolesProceso = rolProcesoRepository
                .findByEmpresaIdOrderByNombreAsc(e.getId())
                .stream().map(this::mapRolProceso).toList();

        List<PoolResumen> pools = poolRepository
                .findByEmpresaIdOrderByNombreAsc(e.getId())
                .stream().map(this::mapPool).toList();

        return EmpresaResumen.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .nit(e.getNit())
                .emailContacto(e.getEmailContacto())
                .createdAt(e.getCreatedAt())
                .usuarios(usuarios)
                .rolesProceso(rolesProceso)
                .pools(pools)
                .build();
    }

    // ── Usuario ───────────────────────────────────────────────────────────────

    private UsuarioResumen mapUsuario(Usuario u) {
        return UsuarioResumen.builder()
                .id(u.getId())
                .email(u.getEmail())
                .estado(u.getEstado())
                .createdAt(u.getCreatedAt())
                .build();
    }

    // ── Rol de Proceso ────────────────────────────────────────────────────────

    private RolProcesoResumen mapRolProceso(RolProceso r) {
        return RolProcesoResumen.builder()
                .id(r.getId())
                .nombre(r.getNombre())
                .descripcion(r.getDescripcion())
                .activo(r.isActivo())
                .createdAt(r.getCreatedAt())
                .build();
    }

    // ── Pool ──────────────────────────────────────────────────────────────────

    private PoolResumen mapPool(Pool p) {
        List<RolPoolResumen> roles = rolPoolRepository.findByPoolId(p.getId())
                .stream().map(this::mapRolPool).toList();

        List<ProcesoResumen> procesos = procesoRepository.findByPoolIdAndActivoTrue(p.getId())
                .stream().map(this::mapProceso).toList();

        return PoolResumen.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .configJson(p.getConfigJson())
                .createdAt(p.getCreatedAt())
                .rolesPool(roles)
                .procesos(procesos)
                .build();
    }

    // ── Rol de Pool ───────────────────────────────────────────────────────────

    private RolPoolResumen mapRolPool(RolPool r) {
        List<String> permisos = r.getPermisos().stream()
                .map(p -> p.getCodigo())
                .sorted()
                .toList();

        List<MiembroResumen> miembros = usuarioRolPoolRepository
                .findByIdRolPoolId(r.getId())
                .stream().map(this::mapMiembro).toList();

        return RolPoolResumen.builder()
                .id(r.getId())
                .nombre(r.getNombre())
                .descripcion(r.getDescripcion())
                .activo(r.isActivo())
                .esPropietario(r.isEsPropietario())
                .createdAt(r.getCreatedAt())
                .permisos(permisos)
                .miembros(miembros)
                .build();
    }

    private MiembroResumen mapMiembro(UsuarioRolPool urp) {
        return MiembroResumen.builder()
                .usuarioId(urp.getUsuario().getId())
                .email(urp.getUsuario().getEmail())
                .asignadoEn(urp.getCreatedAt())
                .build();
    }

    // ── Proceso ───────────────────────────────────────────────────────────────

    private ProcesoResumen mapProceso(Proceso p) {
        List<LaneResumen> lanes = laneRepository
                .findByProcesoIdOrderByOrdenAsc(p.getId())
                .stream().map(this::mapLane).toList();

        List<NodoResumen> nodos = nodoRepository.findByProcesoId(p.getId())
                .stream().map(this::mapNodo).toList();

        List<ArcoResumen> arcos = arcoRepository.findByProcesoId(p.getId())
                .stream().map(this::mapArco).toList();

        List<CompartidoResumen> compartidos = procesoCompartidoRepository
                .findByProcesoId(p.getId())
                .stream().map(this::mapCompartido).toList();

        return ProcesoResumen.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .categoria(p.getCategoria())
                .estado(p.getEstado())
                .activo(p.isActivo())
                .creadoPorEmail(p.getCreatedByUser().getEmail())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .lanes(lanes)
                .nodos(nodos)
                .arcos(arcos)
                .compartidosCon(compartidos)
                .build();
    }

    // ── Lane ──────────────────────────────────────────────────────────────────

    private LaneResumen mapLane(Lane l) {
        Integer rolProcesoId = l.getRolProceso() != null ? l.getRolProceso().getId() : null;
        String rolProcesoNombre = l.getRolProceso() != null ? l.getRolProceso().getNombre() : null;

        return LaneResumen.builder()
                .id(l.getId())
                .nombre(l.getNombre())
                .orden(l.getOrden())
                .rolProcesoId(rolProcesoId)
                .rolProcesoNombre(rolProcesoNombre)
                .createdAt(l.getCreatedAt())
                .build();
    }

    // ── Nodo ──────────────────────────────────────────────────────────────────

    private NodoResumen mapNodo(Nodo n) {
        String tipoActividad = null;
        String tipoGateway = null;

        if (n instanceof Actividad a) {
            tipoActividad = a.getTipoActividad();
        } else if (n instanceof Gateway g) {
            tipoGateway = g.getTipoGateway();
        }

        return NodoResumen.builder()
                .id(n.getId())
                .tipo(n.getTipo())
                .label(n.getLabel())
                .posX(n.getPosX())
                .posY(n.getPosY())
                .laneId(n.getLane() != null ? n.getLane().getId() : null)
                .createdAt(n.getCreatedAt())
                .tipoActividad(tipoActividad)
                .tipoGateway(tipoGateway)
                .build();
    }

    // ── Arco ──────────────────────────────────────────────────────────────────

    private ArcoResumen mapArco(Arco a) {
        return ArcoResumen.builder()
                .id(a.getId())
                .fromNodoId(a.getFromNodo().getId())
                .fromNodoLabel(a.getFromNodo().getLabel())
                .toNodoId(a.getToNodo().getId())
                .toNodoLabel(a.getToNodo().getLabel())
                .condicionExpr(a.getCondicionExpr())
                .activo(a.isActivo())
                .createdAt(a.getCreatedAt())
                .build();
    }

    // ── Proceso Compartido ────────────────────────────────────────────────────

    private CompartidoResumen mapCompartido(ProcesoCompartido pc) {
        return CompartidoResumen.builder()
                .poolDestinoId(pc.getPoolDestino().getId())
                .poolDestinoNombre(pc.getPoolDestino().getNombre())
                .permiso(pc.getPermiso())
                .createdAt(pc.getCreatedAt())
                .build();
    }

    // ── Estadísticas globales ─────────────────────────────────────────────────

    private Estadisticas calcularEstadisticas(List<EmpresaResumen> empresas) {
        long totalUsuarios = empresas.stream()
                .mapToLong(e -> e.getUsuarios().size()).sum();
        long totalPools = empresas.stream()
                .mapToLong(e -> e.getPools().size()).sum();
        long totalRolesPool = empresas.stream()
                .flatMap(e -> e.getPools().stream())
                .mapToLong(p -> p.getRolesPool().size()).sum();
        long totalRolesProceso = empresas.stream()
                .mapToLong(e -> e.getRolesProceso().size()).sum();
        long totalProcesos = empresas.stream()
                .flatMap(e -> e.getPools().stream())
                .mapToLong(p -> p.getProcesos().size()).sum();
        long totalLanes = empresas.stream()
                .flatMap(e -> e.getPools().stream())
                .flatMap(p -> p.getProcesos().stream())
                .mapToLong(p -> p.getLanes().size()).sum();
        long totalNodos = empresas.stream()
                .flatMap(e -> e.getPools().stream())
                .flatMap(p -> p.getProcesos().stream())
                .mapToLong(p -> p.getNodos().size()).sum();
        long totalArcos = empresas.stream()
                .flatMap(e -> e.getPools().stream())
                .flatMap(p -> p.getProcesos().stream())
                .mapToLong(p -> p.getArcos().size()).sum();

        return Estadisticas.builder()
                .totalEmpresas(empresas.size())
                .totalUsuarios(totalUsuarios)
                .totalPools(totalPools)
                .totalRolesPool(totalRolesPool)
                .totalRolesProceso(totalRolesProceso)
                .totalProcesos(totalProcesos)
                .totalLanes(totalLanes)
                .totalNodos(totalNodos)
                .totalArcos(totalArcos)
                .build();
    }
}
