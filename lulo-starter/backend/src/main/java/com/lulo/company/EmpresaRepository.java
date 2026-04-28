package com.lulo.company;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {

    Optional<Empresa> findByNit(String nit);

    boolean existsByNit(String nit);
}
