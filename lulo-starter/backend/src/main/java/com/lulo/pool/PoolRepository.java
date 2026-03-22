package com.lulo.pool;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PoolRepository extends JpaRepository<Pool, Integer> {

    // Hibernate Filter activo: ya filtra por empresa automáticamente
    List<Pool> findAll();

    boolean existsByNombreAndEmpresaId(String nombre, Integer empresaId);
}
