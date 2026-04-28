package com.lulo.audit;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulo.company.Empresa;
import com.lulo.users.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper       objectMapper;

    /**
     * Registra un cambio en el log de auditoría.
     * Debe llamarse dentro de la misma transacción que el cambio que se audita.
     *
     * @param empresa   empresa a la que pertenece la entidad
     * @param usuario   usuario que realizó la acción
     * @param entidad   nombre de la entidad (ej: "PROCESO", "ROL_POOL")
     * @param entidadId ID del registro modificado
     * @param accion    CREAR | EDITAR | PUBLICAR | ARCHIVAR | COMPARTIR | ASIGNAR_ROL | REVOCAR_ROL
     * @param antes     estado previo (null si es creación)
     * @param despues   estado posterior (null si es eliminación)
     */
    public void registrar(Empresa empresa,
                          Usuario usuario,
                          String entidad,
                          UUID entidadId,
                          String accion,
                          Object antes,
                          Object despues) {
        try {
            Map<String, Object> diff = Map.of(
                    "antes",   antes   != null ? antes   : Map.of(),
                    "despues", despues != null ? despues : Map.of()
            );

            AuditLog log = new AuditLog();
            log.setEmpresa(empresa);
            log.setUsuario(usuario);
            log.setEntidad(entidad);
            log.setEntidadId(entidadId);
            log.setAccion(accion);
            log.setDiffJson(objectMapper.writeValueAsString(diff));

            auditLogRepository.save(log);

        } catch (Exception e) {
            // Loguea el error pero no interrumpe la transacción principal
            log.error("Error al registrar auditoría [{} id={}]: {}", entidad, entidadId, e.getMessage());
        }
    }
}
