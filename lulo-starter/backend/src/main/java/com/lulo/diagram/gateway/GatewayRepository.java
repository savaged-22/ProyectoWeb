package com.lulo.diagram.gateway;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GatewayRepository extends JpaRepository<Gateway, Integer> {

    List<Gateway> findByProcesoId(Integer procesoId);

    List<Gateway> findByProcesoIdAndTipoGateway(Integer procesoId, String tipoGateway);

    Optional<Gateway> findByIdAndProcesoId(Integer id, Integer procesoId);
}
