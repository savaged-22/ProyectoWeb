package com.lulo.process;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProcesoRepository extends JpaRepository<Proceso, UUID>,
        JpaSpecificationExecutor<Proceso> {

    // Hibernate Filter activo: ya filtra por empresa automáticamente.
    // JpaSpecificationExecutor permite búsquedas dinámicas por estado, categoría, nombre, etc.

    List<Proceso> findByPoolIdAndActivoTrue(UUID poolId);

    List<Proceso> findByPoolIdAndEstadoAndActivoTrue(UUID poolId, String estado);

    Optional<Proceso> findByIdAndActivoTrue(UUID id);

    boolean existsByPoolIdAndNombre(UUID poolId, String nombre);

    List<Proceso> findByEmpresaId(UUID empresaId);
}
