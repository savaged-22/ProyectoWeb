package com.lulo.messaging;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuscripcionMensajeRepository extends JpaRepository<SuscripcionMensaje, UUID> {

    List<SuscripcionMensaje> findByProcesoIdAndActivoTrue(UUID procesoId);

    List<SuscripcionMensaje> findByEmpresaIdAndNombreMensajeAndActivoTrue(
            UUID empresaId, String nombreMensaje);
}
