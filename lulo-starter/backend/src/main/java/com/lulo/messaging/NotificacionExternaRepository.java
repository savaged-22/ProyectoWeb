package com.lulo.messaging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificacionExternaRepository extends JpaRepository<NotificacionExterna, Integer> {

    List<NotificacionExterna> findByProcesoIdAndActivoTrue(Integer procesoId);

    List<NotificacionExterna> findByEmpresaIdAndNombreMensajeAndActivoTrue(
            UUID empresaId, String nombreMensaje);
}
