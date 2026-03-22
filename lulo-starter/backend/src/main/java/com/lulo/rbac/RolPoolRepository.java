package com.lulo.rbac;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolPoolRepository extends JpaRepository<RolPool, Integer> {

    // Hibernate Filter activo: filtra por empresa vía subquery en pool
    List<RolPool> findByPoolId(Integer poolId);

    List<RolPool> findByPoolIdAndActivo(Integer poolId, boolean activo);

    Optional<RolPool> findByPoolIdAndEsPropietarioTrue(Integer poolId);

    boolean existsByPoolIdAndNombre(Integer poolId, String nombre);
}
