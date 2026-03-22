package com.lulo.rbac;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermisoRepository extends JpaRepository<Permiso, Integer> {

    // Sin Hibernate Filter: Permiso es catálogo global
    Optional<Permiso> findByCodigo(String codigo);

    List<Permiso> findByCodigoIn(List<String> codigos);
}
