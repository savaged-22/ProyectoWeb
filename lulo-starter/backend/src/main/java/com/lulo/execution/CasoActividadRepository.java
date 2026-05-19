package com.lulo.execution;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface CasoActividadRepository extends JpaRepository<CasoActividad, UUID> {
    List<CasoActividad> findByCasoIdOrderByFechaInicioDesc(UUID casoId);
}
