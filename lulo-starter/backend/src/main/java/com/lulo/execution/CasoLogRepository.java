package com.lulo.execution;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface CasoLogRepository extends JpaRepository<CasoLog, UUID> {
    List<CasoLog> findByCasoIdOrderByFechaDesc(UUID casoId);
    
    @org.springframework.data.jpa.repository.Query("""
        SELECT l FROM CasoLog l
        LEFT JOIN l.caso c
        LEFT JOIN c.proceso cp
        LEFT JOIN l.proceso p
        WHERE p.empresa.id = :empresaId
           OR cp.empresa.id = :empresaId
        ORDER BY l.fecha DESC
    """)
    List<CasoLog> findByEmpresaIdOrderByFechaDesc(@org.springframework.data.repository.query.Param("empresaId") UUID empresaId);
}
