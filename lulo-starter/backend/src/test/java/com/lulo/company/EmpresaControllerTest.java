package com.lulo.company;

import com.lulo.company.dto.RegistroEmpresaRequest;
import com.lulo.company.dto.RegistroEmpresaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmpresaControllerTest {

    private EmpresaService empresaService;
    private EmpresaController empresaController;

    @BeforeEach
    void setUp() {
        empresaService = mock(EmpresaService.class);
        empresaController = new EmpresaController(empresaService);
    }

    @Test
    void registrar_delegaEnEmpresaServiceYRetornaRespuesta() {
        // Arrange
        RegistroEmpresaRequest request = new RegistroEmpresaRequest();
        RegistroEmpresaResponse responseEsperada = mock(RegistroEmpresaResponse.class);

        when(empresaService.registrar(request))
                .thenReturn(responseEsperada);

        // Act
        RegistroEmpresaResponse response = empresaController.registrar(request);

        // Assert
        assertEquals(responseEsperada, response);
        verify(empresaService).registrar(request);
    }
}