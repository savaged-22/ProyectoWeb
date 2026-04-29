package com.lulo.diagram.lane;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LaneRepository extends JpaRepository<Lane, UUID> {

    List<Lane> findByProcesoIdOrderByOrdenAsc(UUID procesoId);

    boolean existsByProcesoIdAndNombre(UUID procesoId, String nombre);

    @Query("""
            SELECT COUNT(l) > 0
            FROM Lane l
            WHERE l.proceso.id = :procesoId
              AND l.nombre = :nombre
              AND l.id <> :laneId
            """)
    boolean existsByProcesoIdAndNombreExcluyendoId(@Param("procesoId") UUID procesoId,
                                                   @Param("nombre") String nombre,
                                                   @Param("laneId") UUID laneId);
}
