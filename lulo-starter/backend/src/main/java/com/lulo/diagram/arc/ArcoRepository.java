package com.lulo.diagram.arc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArcoRepository extends JpaRepository<Arco, Integer> {

    List<Arco> findByProcesoIdAndActivoTrue(Integer procesoId);

    // Arcos activos que salen/llegan a un nodo (para consultas del diagrama)
    List<Arco> findByFromNodoIdAndActivoTrue(Integer fromNodoId);
    List<Arco> findByToNodoIdAndActivoTrue(Integer toNodoId);

    // Todos los arcos conectados a un nodo (activos e inactivos) — necesario
    // para eliminarlos físicamente antes de borrar el nodo (integridad referencial)
    List<Arco> findByFromNodoId(Integer fromNodoId);
    List<Arco> findByToNodoId(Integer toNodoId);

    // Todos los arcos de un proceso (para archivado masivo)
    List<Arco> findByProcesoId(Integer procesoId);

    Optional<Arco> findByIdAndActivoTrue(Integer id);

    boolean existsByProcesoIdAndFromNodoIdAndToNodoIdAndActivoTrue(
            Integer procesoId, Integer fromNodoId, Integer toNodoId);

    @Query("""
            SELECT COUNT(a) > 0
            FROM Arco a
            WHERE a.proceso.id = :procesoId
              AND a.fromNodo.id = :fromNodoId
              AND a.toNodo.id = :toNodoId
              AND a.activo = true
              AND a.id <> :arcoId
            """)
    boolean existsActivoDuplicadoExcluyendoId(@Param("procesoId") Integer procesoId,
                                              @Param("fromNodoId") Integer fromNodoId,
                                              @Param("toNodoId") Integer toNodoId,
                                              @Param("arcoId") Integer arcoId);
}
