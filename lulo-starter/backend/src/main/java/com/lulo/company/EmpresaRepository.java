package com.lulo.company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {

    Optional<Empresa> findByNit(String nit);

    boolean existsByNit(String nit);
}
