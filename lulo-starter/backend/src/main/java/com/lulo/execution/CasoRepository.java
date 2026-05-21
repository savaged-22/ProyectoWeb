package com.lulo.execution;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface CasoRepository extends JpaRepository<Caso, UUID> {
    List<Caso> findByProcesoIdOrderByFechaInicioDesc(UUID procesoId);
    
    List<Caso> findByProceso_Empresa_IdOrderByFechaInicioDesc(UUID empresaId);
}
