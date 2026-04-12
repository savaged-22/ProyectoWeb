package com.lulo.pool;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PoolRepository extends JpaRepository<Pool, Integer> {

    // Hibernate Filter activo: ya filtra por empresa automáticamente
    List<Pool> findAll();

    List<Pool> findByEmpresaIdOrderByNombreAsc(UUID empresaId);

    Optional<Pool> findByIdAndEmpresaId(Integer id, UUID empresaId);

    boolean existsByNombreAndEmpresaId(String nombre, UUID empresaId);
}
