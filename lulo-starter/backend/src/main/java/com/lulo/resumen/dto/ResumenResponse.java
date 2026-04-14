package com.lulo.resumen.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO raíz del endpoint GET /api/resumen.
 * Refleja la jerarquía completa de la base de datos:
 * Empresa → {Usuarios, RolesProceso, Pools → {RolesPool, Procesos → {Lanes, Nodos, Arcos}}}
 */
@Getter
@Builder
public class ResumenResponse {

    private Estadisticas estadisticas;
    private List<EmpresaResumen> empresas;

    // ── Contadores globales ───────────────────────────────────────────────────
    @Getter
    @Builder
    public static class Estadisticas {
        private long totalEmpresas;
        private long totalUsuarios;
        private long totalPools;
        private long totalRolesPool;
        private long totalRolesProceso;
        private long totalProcesos;
        private long totalLanes;
        private long totalNodos;
        private long totalArcos;
    }

    // ── Empresa ───────────────────────────────────────────────────────────────
    @Getter
    @Builder
    public static class EmpresaResumen {
        private Integer id;
        private String nombre;
        private String nit;
        private String emailContacto;
        private LocalDateTime createdAt;

        private List<UsuarioResumen> usuarios;
        private List<RolProcesoResumen> rolesProceso;
        private List<PoolResumen> pools;
    }

    // ── Usuario ───────────────────────────────────────────────────────────────
    @Getter
    @Builder
    public static class UsuarioResumen {
        private Integer id;
        private String email;
        private String estado;
        private LocalDateTime createdAt;
    }

    // ── Rol de Proceso (funcional) ────────────────────────────────────────────
    @Getter
    @Builder
    public static class RolProcesoResumen {
        private Integer id;
        private String nombre;
        private String descripcion;
        private boolean activo;
        private LocalDateTime createdAt;
    }

    // ── Pool ──────────────────────────────────────────────────────────────────
    @Getter
    @Builder
    public static class PoolResumen {
        private Integer id;
        private String nombre;
        private String configJson;
        private LocalDateTime createdAt;

        private List<RolPoolResumen> rolesPool;
        private List<ProcesoResumen> procesos;
    }

    // ── Rol de Pool (RBAC) ────────────────────────────────────────────────────
    @Getter
    @Builder
    public static class RolPoolResumen {
        private Integer id;
        private String nombre;
        private String descripcion;
        private boolean activo;
        private boolean esPropietario;
        private LocalDateTime createdAt;

        private List<String> permisos;
        private List<MiembroResumen> miembros;
    }

    // ── Miembro (usuario asignado a un rol del pool) ──────────────────────────
    @Getter
    @Builder
    public static class MiembroResumen {
        private Integer usuarioId;
        private String email;
        private LocalDateTime asignadoEn;
    }

    // ── Proceso ───────────────────────────────────────────────────────────────
    @Getter
    @Builder
    public static class ProcesoResumen {
        private Integer id;
        private String nombre;
        private String descripcion;
        private String categoria;
        private String estado;
        private boolean activo;
        private String creadoPorEmail;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private List<LaneResumen> lanes;
        private List<NodoResumen> nodos;
        private List<ArcoResumen> arcos;
        private List<CompartidoResumen> compartidosCon;
    }

    // ── Lane ──────────────────────────────────────────────────────────────────
    @Getter
    @Builder
    public static class LaneResumen {
        private Integer id;
        private String nombre;
        private int orden;
        private Integer rolProcesoId;
        private String rolProcesoNombre;
        private LocalDateTime createdAt;
    }

    // ── Nodo (actividad, gateway o nodo base) ─────────────────────────────────
    @Getter
    @Builder
    public static class NodoResumen {
        private Integer id;
        private String tipo;
        private String label;
        private Float posX;
        private Float posY;
        private Integer laneId;
        private LocalDateTime createdAt;

        // Solo si tipo = 'actividad'
        private String tipoActividad;

        // Solo si tipo = 'gateway'
        private String tipoGateway;
    }

    // ── Arco ──────────────────────────────────────────────────────────────────
    @Getter
    @Builder
    public static class ArcoResumen {
        private Integer id;
        private Integer fromNodoId;
        private String fromNodoLabel;
        private Integer toNodoId;
        private String toNodoLabel;
        private String condicionExpr;
        private boolean activo;
        private LocalDateTime createdAt;
    }

    // ── Proceso Compartido ────────────────────────────────────────────────────
    @Getter
    @Builder
    public static class CompartidoResumen {
        private Integer poolDestinoId;
        private String poolDestinoNombre;
        private String permiso;
        private LocalDateTime createdAt;
    }
}
