package com.lulo.pool;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PoolRepository extends JpaRepository<Pool, UUID> {

    // Hibernate Filter activo: ya filtra por empresa automáticamente
    List<Pool> findAll();

    List<Pool> findByEmpresaIdOrderByNombreAsc(UUID empresaId);

    Optional<Pool> findByIdAndEmpresaId(UUID id, UUID empresaId);

    boolean existsByNombreAndEmpresaId(String nombre, UUID empresaId);
}
