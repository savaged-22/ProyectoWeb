package com.lulo.sharing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProcesoCompartidoRepository extends JpaRepository<ProcesoCompartido, Integer> {

    List<ProcesoCompartido> findByProcesoId(Integer procesoId);

    List<ProcesoCompartido> findByPoolDestinoId(Integer poolDestinoId);

    Optional<ProcesoCompartido> findByProcesoIdAndPoolDestinoId(Integer procesoId, Integer poolDestinoId);

    boolean existsByProcesoIdAndPoolDestinoId(Integer procesoId, Integer poolDestinoId);
}
