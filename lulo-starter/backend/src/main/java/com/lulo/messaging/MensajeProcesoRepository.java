package com.lulo.messaging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeProcesoRepository extends JpaRepository<MensajeProceso, Integer> {

    List<MensajeProceso> findByProcesoOrigenIdAndEstado(Integer procesoOrigenId, String estado);

    List<MensajeProceso> findByEmpresaIdAndNombreMensajeAndEstado(
            Integer empresaId, String nombreMensaje, String estado);
}
