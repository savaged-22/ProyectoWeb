package com.lulo.diagram.gateway;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GatewayRepository extends JpaRepository<Gateway, UUID> {

    List<Gateway> findByProcesoId(UUID procesoId);

    List<Gateway> findByProcesoIdAndTipoGateway(UUID procesoId, String tipoGateway);

    Optional<Gateway> findByIdAndProcesoId(UUID id, UUID procesoId);
}
