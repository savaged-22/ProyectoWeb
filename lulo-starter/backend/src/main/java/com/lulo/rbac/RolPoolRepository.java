package com.lulo.rbac;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RolPoolRepository extends JpaRepository<RolPool, UUID> {

    // Hibernate Filter activo: filtra por empresa vía subquery en pool
    List<RolPool> findByPoolId(UUID poolId);

    List<RolPool> findByPoolIdAndActivo(UUID poolId, boolean activo);

    List<RolPool> findByPoolIdAndActivoTrueOrderByNombreAsc(UUID poolId);

    Optional<RolPool> findByPoolIdAndEsPropietarioTrue(UUID poolId);

    Optional<RolPool> findByIdAndActivoTrue(UUID id);

    boolean existsByPoolIdAndNombre(UUID poolId, String nombre);

    boolean existsByPoolIdAndNombreAndActivoTrue(UUID poolId, String nombre);

    @Query("""
            SELECT COUNT(r) > 0
            FROM RolPool r
            WHERE r.pool.id = :poolId
              AND r.nombre = :nombre
              AND r.activo = true
              AND r.id <> :rolPoolId
            """)
    boolean existsActivoByPoolIdAndNombreExcluyendoId(@Param("poolId") UUID poolId,
                                                      @Param("nombre") String nombre,
                                                      @Param("rolPoolId") UUID rolPoolId);
}
