package com.lulo.diagram.node.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * DTO aplanado que representa cualquier tipo de nodo del diagrama.
 * Los campos de subtipo (tipoActividad, propsJson, tipoGateway, configJson)
 * son nulos cuando el nodo no es de ese subtipo.
 */
@Getter
@Builder
public class NodoResponse {

    private UUID id;
    private UUID laneId;

    // tipo: 'actividad' | 'gateway' | 'nodo' (inicio, fin)
    private String tipo;
    private String label;
    private Float  posX;
    private Float  posY;

    // Campos exclusivos de Actividad (null si no aplica)
    private String tipoActividad;
    private String propsJson;

    // Campos exclusivos de Gateway (null si no aplica)
    private String tipoGateway;
    private String configJson;

    private LocalDateTime createdAt;
}
