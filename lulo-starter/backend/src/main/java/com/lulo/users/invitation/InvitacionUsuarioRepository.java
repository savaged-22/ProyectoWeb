package com.lulo.users.invitation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitacionUsuarioRepository extends JpaRepository<InvitacionUsuario, Integer> {

    // Hibernate Filter activo: ya filtra por empresa automáticamente
    Optional<InvitacionUsuario> findByTokenHash(String tokenHash);

    List<InvitacionUsuario> findByEmailAndEstado(String email, String estado);

    List<InvitacionUsuario> findByEstado(String estado);

    boolean existsByEmailAndEstado(String email, String estado);
}
