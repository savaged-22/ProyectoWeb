package com.lulo.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulo.company.Empresa;
import com.lulo.users.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class AuditServiceTest {

    private AuditLogRepository auditLogRepository;
    private ObjectMapper objectMapper;
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditLogRepository = mock(AuditLogRepository.class);
        objectMapper = mock(ObjectMapper.class);

        auditService = new AuditService(
                auditLogRepository,
                objectMapper
        );
    }

    @Test
    void registrar_exitoso_guardaAuditLog() throws Exception {
        Empresa empresa = new Empresa();
        Usuario usuario = new Usuario();
        UUID entidadId = UUID.randomUUID();

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"antes\":{},\"despues\":{}}");

        auditService.registrar(
                empresa,
                usuario,
                "PROCESO",
                entidadId,
                "CREAR",
                null,
                null
        );

        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void registrar_siOcurreError_noLanzaExcepcion() throws Exception {
        Empresa empresa = new Empresa();
        Usuario usuario = new Usuario();
        UUID entidadId = UUID.randomUUID();

        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new RuntimeException("Error"));

        // No debe lanzar excepción
        auditService.registrar(
                empresa,
                usuario,
                "PROCESO",
                entidadId,
                "CREAR",
                null,
                null
        );

        // Como falló antes de guardar, no debe persistir nada
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }
}