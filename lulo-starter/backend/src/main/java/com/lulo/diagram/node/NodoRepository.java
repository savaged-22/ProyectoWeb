package com.lulo.diagram.node;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodoRepository extends JpaRepository<Nodo, UUID> {

    // Devuelve todos los nodos del proceso (polimórfico: incluye Actividad y Gateway)
    List<Nodo> findByProcesoId(UUID procesoId);

    List<Nodo> findByProcesoIdAndLaneId(UUID procesoId, UUID laneId);

    List<Nodo> findByProcesoIdAndTipo(UUID procesoId, String tipo);
}
