package com.lulo.rbac;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RolProcesoRepository extends JpaRepository<RolProceso, Integer> {

    // Hibernate Filter activo: ya filtra por empresa automáticamente
    List<RolProceso> findByActivo(boolean activo);

    boolean existsByEmpresaIdAndNombre(Integer empresaId, String nombre);

    // Verifica si un rol está asignado a alguna lane antes de eliminarlo
    @Query("SELECT COUNT(l) > 0 FROM Lane l WHERE l.rolProceso.id = :rolProcesoId")
    boolean existsEnLane(@Param("rolProcesoId") Integer rolProcesoId);
}
