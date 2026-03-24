package com.lulo.diagram.lane;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LaneRepository extends JpaRepository<Lane, Integer> {

    List<Lane> findByProcesoIdOrderByOrdenAsc(Integer procesoId);

    boolean existsByProcesoIdAndNombre(Integer procesoId, String nombre);

    @Query("""
            SELECT COUNT(l) > 0
            FROM Lane l
            WHERE l.proceso.id = :procesoId
              AND l.nombre = :nombre
              AND l.id <> :laneId
            """)
    boolean existsByProcesoIdAndNombreExcluyendoId(@Param("procesoId") Integer procesoId,
                                                   @Param("nombre") String nombre,
                                                   @Param("laneId") Integer laneId);
}
