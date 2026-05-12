package com.lulo.audit;

import com.lulo.company.Empresa;
import com.lulo.users.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogTest {

    @Test
    void gettersYSetters_funcionanCorrectamente() {
        AuditLog auditLog = new AuditLog();

        UUID id = UUID.randomUUID();
        UUID entidadId = UUID.randomUUID();
        Empresa empresa = new Empresa();
        Usuario usuario = new Usuario();
        LocalDateTime createdAt = LocalDateTime.now();

        auditLog.setId(id);
        auditLog.setEmpresa(empresa);
        auditLog.setUsuario(usuario);
        auditLog.setEntidad("Proceso");
        auditLog.setEntidadId(entidadId);
        auditLog.setAccion("CREAR");
        auditLog.setDiffJson("{\"campo\":\"valor\"}");
        auditLog.setCreatedAt(createdAt);

        assertEquals(id, auditLog.getId());
        assertEquals(empresa, auditLog.getEmpresa());
        assertEquals(usuario, auditLog.getUsuario());
        assertEquals("Proceso", auditLog.getEntidad());
        assertEquals(entidadId, auditLog.getEntidadId());
        assertEquals("CREAR", auditLog.getAccion());
        assertEquals("{\"campo\":\"valor\"}", auditLog.getDiffJson());
        assertEquals(createdAt, auditLog.getCreatedAt());
    }

    @Test
    void constructorVacio_creaObjetoConValoresNulos() {
        AuditLog auditLog = new AuditLog();

        assertNotNull(auditLog);
        assertNull(auditLog.getId());
        assertNull(auditLog.getEmpresa());
        assertNull(auditLog.getUsuario());
        assertNull(auditLog.getEntidad());
        assertNull(auditLog.getEntidadId());
        assertNull(auditLog.getAccion());
        assertNull(auditLog.getDiffJson());
        assertNull(auditLog.getCreatedAt());
    }

    @Test
    void prePersist_asignaCreatedAt() {
        AuditLog auditLog = new AuditLog();

        assertNull(auditLog.getCreatedAt());

        auditLog.prePersist();

        assertNotNull(auditLog.getCreatedAt());
    }
}