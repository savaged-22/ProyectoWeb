package com.lulo.messaging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuscripcionMensajeRepository extends JpaRepository<SuscripcionMensaje, Integer> {

    List<SuscripcionMensaje> findByProcesoIdAndActivoTrue(Integer procesoId);

    List<SuscripcionMensaje> findByEmpresaIdAndNombreMensajeAndActivoTrue(
            Integer empresaId, String nombreMensaje);
}
