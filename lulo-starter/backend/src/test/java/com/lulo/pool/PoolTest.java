package com.lulo.pool;

import com.lulo.company.Empresa;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PoolTest {

    @Test
    void gettersYSetters_funcionanCorrectamente() {
        Pool pool = new Pool();
        UUID id = UUID.randomUUID();
        Empresa empresa = new Empresa();

        pool.setId(id);
        pool.setEmpresa(empresa);
        pool.setNombre("Pool Principal");
        pool.setConfigJson("{\"key\":\"value\"}");

        assertEquals(id, pool.getId());
        assertEquals(empresa, pool.getEmpresa());
        assertEquals("Pool Principal", pool.getNombre());
        assertEquals("{\"key\":\"value\"}", pool.getConfigJson());
    }

    @Test
    void constructorVacio_creaObjetoConValoresNulos() {
        Pool pool = new Pool();

        assertNotNull(pool);
        assertNull(pool.getId());
        assertNull(pool.getEmpresa());
        assertNull(pool.getNombre());
        assertNull(pool.getConfigJson());
    }
}