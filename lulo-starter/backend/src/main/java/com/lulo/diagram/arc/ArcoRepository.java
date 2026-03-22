package com.lulo.diagram.arc;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

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
}
