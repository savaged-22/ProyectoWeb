package com.lulo.rbac;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RolPoolRepository extends JpaRepository<RolPool, Integer> {

    // Hibernate Filter activo: filtra por empresa vía subquery en pool
    List<RolPool> findByPoolId(Integer poolId);

    List<RolPool> findByPoolIdAndActivo(Integer poolId, boolean activo);

    List<RolPool> findByPoolIdAndActivoTrueOrderByNombreAsc(Integer poolId);

    Optional<RolPool> findByPoolIdAndEsPropietarioTrue(Integer poolId);

    Optional<RolPool> findByIdAndActivoTrue(Integer id);

    boolean existsByPoolIdAndNombre(Integer poolId, String nombre);

    boolean existsByPoolIdAndNombreAndActivoTrue(Integer poolId, String nombre);

    @Query("""
            SELECT COUNT(r) > 0
            FROM RolPool r
            WHERE r.pool.id = :poolId
              AND r.nombre = :nombre
              AND r.activo = true
              AND r.id <> :rolPoolId
            """)
    boolean existsActivoByPoolIdAndNombreExcluyendoId(@Param("poolId") Integer poolId,
                                                      @Param("nombre") String nombre,
                                                      @Param("rolPoolId") Integer rolPoolId);
}
