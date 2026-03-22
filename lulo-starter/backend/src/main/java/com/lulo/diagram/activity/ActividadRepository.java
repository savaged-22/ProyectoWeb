package com.lulo.diagram.activity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActividadRepository extends JpaRepository<Actividad, Integer> {

    List<Actividad> findByProcesoId(Integer procesoId);

    List<Actividad> findByProcesoIdAndTipoActividad(Integer procesoId, String tipoActividad);
}
