package com.lulo.rbac;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RolProcesoRepository extends JpaRepository<RolProceso, Integer> {

    // Hibernate Filter activo: ya filtra por empresa automáticamente
    List<RolProceso> findByActivo(boolean activo);

    boolean existsByEmpresaIdAndNombre(Integer empresaId, String nombre);

    boolean existsByEmpresaIdAndNombreAndActivoTrue(Integer empresaId, String nombre);

    List<RolProceso> findByEmpresaIdOrderByNombreAsc(Integer empresaId);

    List<RolProceso> findByEmpresaIdAndActivoTrueOrderByNombreAsc(Integer empresaId);

    List<RolProceso> findByEmpresaIdAndActivoOrderByNombreAsc(Integer empresaId, boolean activo);

    Optional<RolProceso> findByIdAndActivoTrue(Integer id);

    @Query("""
            SELECT COUNT(r) > 0
            FROM RolProceso r
            WHERE r.empresa.id = :empresaId
              AND r.nombre = :nombre
              AND r.activo = true
              AND r.id <> :rolProcesoId
            """)
    boolean existsActivoByEmpresaIdAndNombreExcluyendoId(@Param("empresaId") Integer empresaId,
                                                         @Param("nombre") String nombre,
                                                         @Param("rolProcesoId") Integer rolProcesoId);

    // Verifica si un rol está asignado a alguna lane antes de eliminarlo
    @Query("SELECT COUNT(l) > 0 FROM Lane l WHERE l.rolProceso.id = :rolProcesoId")
    boolean existsEnLane(@Param("rolProcesoId") Integer rolProcesoId);
}
