package com.lulo.sharing;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProcesoCompartidoRepository extends JpaRepository<ProcesoCompartido, UUID> {

    List<ProcesoCompartido> findByProcesoId(UUID procesoId);

    List<ProcesoCompartido> findByPoolDestinoId(UUID poolDestinoId);

    List<ProcesoCompartido> findByPoolDestinoIdIn(List<UUID> poolDestinoIds);

    Optional<ProcesoCompartido> findByProcesoIdAndPoolDestinoId(UUID procesoId, UUID poolDestinoId);

    boolean existsByProcesoIdAndPoolDestinoId(UUID procesoId, UUID poolDestinoId);
}
