package com.lulo.messaging;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionExternaRepository extends JpaRepository<NotificacionExterna, UUID> {

    List<NotificacionExterna> findByProcesoIdAndActivoTrue(UUID procesoId);

    List<NotificacionExterna> findByEmpresaIdAndNombreMensajeAndActivoTrue(
            UUID empresaId, String nombreMensaje);
}
