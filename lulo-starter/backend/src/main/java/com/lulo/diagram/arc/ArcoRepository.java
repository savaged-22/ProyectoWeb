package com.lulo.diagram.arc;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArcoRepository extends JpaRepository<Arco, Integer> {

    List<Arco> findByProcesoIdAndActivoTrue(UUID procesoId);

    // Arcos activos que salen/llegan a un nodo (para consultas del diagrama)
    List<Arco> findByFromNodoIdAndActivoTrue(UUID fromNodoId);
    List<Arco> findByToNodoIdAndActivoTrue(UUID toNodoId);

    // Todos los arcos conectados a un nodo (activos e inactivos) — necesario
    // para eliminarlos físicamente antes de borrar el nodo (integridad referencial)
    List<Arco> findByFromNodoId(UUID fromNodoId);
    List<Arco> findByToNodoId(UUID toNodoId);

    // Todos los arcos de un proceso (para archivado masivo)
    List<Arco> findByProcesoId(UUID procesoId);

    Optional<Arco> findByIdAndActivoTrue(UUID id);

    boolean existsByProcesoIdAndFromNodoIdAndToNodoIdAndActivoTrue(
            UUID procesoId, UUID fromNodoId, UUID toNodoId);

    @Query("""
            SELECT COUNT(a) > 0
            FROM Arco a
            WHERE a.proceso.id = :procesoId
              AND a.fromNodo.id = :fromNodoId
              AND a.toNodo.id = :toNodoId
              AND a.activo = true
              AND a.id <> :arcoId
            """)
    boolean existsActivoDuplicadoExcluyendoId(@Param("procesoId") UUID procesoId,
                                              @Param("fromNodoId") UUID fromNodoId,
                                              @Param("toNodoId") UUID toNodoId,
                                              @Param("arcoId") UUID arcoId);
}
