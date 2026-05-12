package com.lulo.company;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmpresaTest {

    @Test
    void gettersYSetters_funcionanCorrectamente() {
        Empresa empresa = new Empresa();
        UUID id = UUID.randomUUID();

        empresa.setId(id);
        empresa.setNombre("Mi Empresa");
        empresa.setNit("900123456");
        empresa.setEmailContacto("contacto@empresa.com");

        assertEquals(id, empresa.getId());
        assertEquals("Mi Empresa", empresa.getNombre());
        assertEquals("900123456", empresa.getNit());
        assertEquals("contacto@empresa.com", empresa.getEmailContacto());
    }

    @Test
    void constructorVacio_creaObjetoConValoresNulos() {
        Empresa empresa = new Empresa();

        assertNotNull(empresa);
        assertNull(empresa.getId());
        assertNull(empresa.getNombre());
        assertNull(empresa.getNit());
        assertNull(empresa.getEmailContacto());
    }
}