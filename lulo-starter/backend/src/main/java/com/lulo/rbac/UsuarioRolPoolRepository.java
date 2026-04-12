package com.lulo.rbac;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRolPoolRepository extends JpaRepository<UsuarioRolPool, UsuarioRolPoolId> {

    List<UsuarioRolPool> findByIdUsuarioId(Integer usuarioId);

    List<UsuarioRolPool> findByIdRolPoolId(Integer rolPoolId);

    boolean existsByIdUsuarioId(Integer usuarioId);

    Optional<UsuarioRolPool> findByIdUsuarioIdAndIdRolPoolId(Integer usuarioId, Integer rolPoolId);

    boolean existsByIdUsuarioIdAndIdRolPoolId(Integer usuarioId, Integer rolPoolId);

    // Verifica si un rol tiene usuarios asignados antes de eliminarlo
    boolean existsByIdRolPoolId(Integer rolPoolId);

    // Obtiene todos los roles que un usuario tiene en un pool concreto
    @Query("""
            SELECT urp FROM UsuarioRolPool urp
            JOIN urp.rolPool rp
            WHERE urp.id.usuarioId = :usuarioId
              AND rp.pool.id = :poolId
            """)
    List<UsuarioRolPool> findByUsuarioIdAndPoolId(
            @Param("usuarioId") Integer usuarioId,
            @Param("poolId") Integer poolId);

    @Query("""
            SELECT urp FROM UsuarioRolPool urp
            JOIN urp.rolPool rp
            JOIN rp.pool p
            WHERE urp.id.usuarioId = :usuarioId
              AND p.empresa.id = :empresaId
            """)
    List<UsuarioRolPool> findByUsuarioIdAndEmpresaId(
            @Param("usuarioId") Integer usuarioId,
            @Param("empresaId") UUID empresaId);
}
