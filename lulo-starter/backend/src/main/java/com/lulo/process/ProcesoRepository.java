package com.lulo.process;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProcesoRepository extends JpaRepository<Proceso, Integer>,
        JpaSpecificationExecutor<Proceso> {

    // Hibernate Filter activo: ya filtra por empresa automáticamente.
    // JpaSpecificationExecutor permite búsquedas dinámicas por estado, categoría, nombre, etc.

    List<Proceso> findByPoolIdAndActivoTrue(Integer poolId);

    List<Proceso> findByPoolIdAndEstadoAndActivoTrue(Integer poolId, String estado);

    Optional<Proceso> findByIdAndActivoTrue(Integer id);

    boolean existsByPoolIdAndNombre(Integer poolId, String nombre);

    List<Proceso> findByEmpresaId(Integer empresaId);
}
