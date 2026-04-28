package com.lulo.audit;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {

    // Historial paginado de una entidad específica (ej: proceso con id=5)
    Page<AuditLog> findByEmpresaIdAndEntidadAndEntidadId(
            UUID empresaId, String entidad, UUID entidadId, Pageable pageable);

    // Historial paginado de toda la empresa (para el panel de auditoría)
    Page<AuditLog> findByEmpresaId(UUID empresaId, Pageable pageable);

    // Historial por usuario
    List<AuditLog> findByUsuarioIdAndEmpresaId(UUID usuarioId, UUID empresaId);
}
