package com.lulo.messaging;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeProcesoRepository extends JpaRepository<MensajeProceso, UUID> {

    List<MensajeProceso> findByProcesoOrigenIdAndEstado(UUID procesoOrigenId, String estado);

    List<MensajeProceso> findByEmpresaIdAndNombreMensajeAndEstado(
            UUID empresaId, String nombreMensaje, String estado);
}
