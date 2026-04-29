package com.lulo.diagram.activity;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActividadRepository extends JpaRepository<Actividad, UUID> {

    List<Actividad> findByProcesoId(UUID procesoId);

    List<Actividad> findByProcesoIdAndTipoActividad(UUID procesoId, String tipoActividad);
}
