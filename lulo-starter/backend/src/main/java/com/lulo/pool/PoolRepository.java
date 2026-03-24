package com.lulo.pool;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PoolRepository extends JpaRepository<Pool, Integer> {

    // Hibernate Filter activo: ya filtra por empresa automáticamente
    List<Pool> findAll();

    List<Pool> findByEmpresaIdOrderByNombreAsc(Integer empresaId);

    Optional<Pool> findByIdAndEmpresaId(Integer id, Integer empresaId);

    boolean existsByNombreAndEmpresaId(String nombre, Integer empresaId);
}
