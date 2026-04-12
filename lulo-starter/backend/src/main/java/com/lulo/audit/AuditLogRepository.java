package com.lulo.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {

    // Historial paginado de una entidad específica (ej: proceso con id=5)
    Page<AuditLog> findByEmpresaIdAndEntidadAndEntidadId(
            UUID empresaId, String entidad, Integer entidadId, Pageable pageable);

    // Historial paginado de toda la empresa (para el panel de auditoría)
    Page<AuditLog> findByEmpresaId(UUID empresaId, Pageable pageable);

    // Historial por usuario
    List<AuditLog> findByUsuarioIdAndEmpresaId(Integer usuarioId, UUID empresaId);
}
