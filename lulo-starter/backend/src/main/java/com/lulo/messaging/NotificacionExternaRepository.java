package com.lulo.messaging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionExternaRepository extends JpaRepository<NotificacionExterna, Integer> {

    List<NotificacionExterna> findByProcesoIdAndActivoTrue(Integer procesoId);

    List<NotificacionExterna> findByEmpresaIdAndNombreMensajeAndActivoTrue(
            Integer empresaId, String nombreMensaje);
}
