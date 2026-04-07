package com.lulo.messaging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntregaMensajeRepository extends JpaRepository<EntregaMensaje, Integer> {

    List<EntregaMensaje> findByMensajeId(Integer mensajeId);

    List<EntregaMensaje> findBySuscripcionIdAndEstado(Integer suscripcionId, String estado);

    List<EntregaMensaje> findBySuscripcion_ProcesoIdAndEstado(Integer procesoId, String estado);
}
