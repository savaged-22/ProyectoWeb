package com.lulo.messaging;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntregaMensajeRepository extends JpaRepository<EntregaMensaje, UUID> {

    List<EntregaMensaje> findByMensajeId(UUID mensajeId);

    List<EntregaMensaje> findBySuscripcionIdAndEstado(UUID suscripcionId, String estado);

    List<EntregaMensaje> findBySuscripcion_ProcesoIdAndEstado(UUID procesoId, String estado);
}
