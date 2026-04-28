package com.lulo.rbac;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRolPoolRepository extends JpaRepository<UsuarioRolPool, UsuarioRolPoolId> {

    List<UsuarioRolPool> findByIdUsuarioId(UUID usuarioId);

    List<UsuarioRolPool> findByIdRolPoolId(UUID rolPoolId);

    boolean existsByIdUsuarioId(UUID usuarioId);

    Optional<UsuarioRolPool> findByIdUsuarioIdAndIdRolPoolId(UUID usuarioId, UUID rolPoolId);

    boolean existsByIdUsuarioIdAndIdRolPoolId(UUID usuarioId, UUID rolPoolId);

    // Verifica si un rol tiene usuarios asignados antes de eliminarlo
    boolean existsByIdRolPoolId(UUID rolPoolId);

    // Obtiene todos los roles que un usuario tiene en un pool concreto
    @Query("""
            SELECT urp FROM UsuarioRolPool urp
            JOIN urp.rolPool rp
            WHERE urp.id.usuarioId = :usuarioId
              AND rp.pool.id = :poolId
            """)
    List<UsuarioRolPool> findByUsuarioIdAndPoolId(
            @Param("usuarioId") UUID usuarioId,
            @Param("poolId") UUID poolId);

    @Query("""
            SELECT urp FROM UsuarioRolPool urp
            JOIN urp.rolPool rp
            JOIN rp.pool p
            WHERE urp.id.usuarioId = :usuarioId
              AND p.empresa.id = :empresaId
            """)
    List<UsuarioRolPool> findByUsuarioIdAndEmpresaId(
            @Param("usuarioId") UUID usuarioId,
            @Param("empresaId") UUID empresaId);
}
