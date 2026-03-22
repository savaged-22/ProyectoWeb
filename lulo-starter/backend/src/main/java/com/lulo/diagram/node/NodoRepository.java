package com.lulo.diagram.node;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodoRepository extends JpaRepository<Nodo, Integer> {

    // Devuelve todos los nodos del proceso (polimórfico: incluye Actividad y Gateway)
    List<Nodo> findByProcesoId(Integer procesoId);

    List<Nodo> findByProcesoIdAndLaneId(Integer procesoId, Integer laneId);

    List<Nodo> findByProcesoIdAndTipo(Integer procesoId, String tipo);
}
