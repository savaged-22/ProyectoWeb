package com.lulo.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulo.company.Empresa;
import com.lulo.users.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock private AuditLogRepository auditLogRepository;
    @Mock private ObjectMapper       objectMapper;

    @InjectMocks private AuditService service;

    private Empresa empresa() {
        Empresa e = new Empresa();
        e.setId(1);
        return e;
    }

    private Usuario usuario() {
        Usuario u = new Usuario();
        u.setId(2);
        return u;
    }

    @Test
    void registrar_exitoso_persiste() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"antes\":{},\"despues\":{}}");

        service.registrar(empresa(), usuario(), "PROCESO", 10, "CREAR", null, Map.of("nombre", "X"));

        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void registrar_errorAlSerializar_noLanzaExcepcion() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("fallo") {});

        service.registrar(empresa(), usuario(), "PROCESO", 10, "EDITAR", Map.of(), Map.of());

        verify(auditLogRepository, never()).save(any());
    }
}
