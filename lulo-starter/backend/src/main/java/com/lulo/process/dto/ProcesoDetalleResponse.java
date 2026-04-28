package com.lulo.process.dto;

import java.util.UUID;

import com.lulo.diagram.arc.dto.ArcoResponse;
import com.lulo.diagram.lane.dto.LaneResponse;
import com.lulo.diagram.node.dto.NodoResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProcesoDetalleResponse {

    // ── Datos del proceso ─────────────────────────────────────────────────────
    private UUID id;
    private UUID empresaId;
    private String        empresaNombre;
    private UUID poolId;
    private String        poolNombre;
    private UUID creadoPorId;
    private String        creadoPorEmail;
    private String        nombre;
    private String        descripcion;
    private String        categoria;
    private String        estado;
    private boolean       activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── Elementos del diagrama ────────────────────────────────────────────────
    private List<LaneResponse> lanes;
    private List<NodoResponse> nodos;
    private List<ArcoResponse> arcos;
}
