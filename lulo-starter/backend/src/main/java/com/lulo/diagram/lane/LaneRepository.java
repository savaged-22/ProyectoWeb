package com.lulo.diagram.lane;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LaneRepository extends JpaRepository<Lane, Integer> {

    List<Lane> findByProcesoIdOrderByOrdenAsc(Integer procesoId);

    boolean existsByProcesoIdAndNombre(Integer procesoId, String nombre);
}
