package com.lulo.users;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    // Hibernate Filter activo: ya filtra por empresa automáticamente
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findAllByEstado(String estado);

    List<Usuario> findByEmpresaId(UUID empresaId);
}
