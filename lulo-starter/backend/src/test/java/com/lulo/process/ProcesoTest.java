package com.lulo.process;

import com.lulo.company.Empresa;
import com.lulo.pool.Pool;
import com.lulo.users.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProcesoTest {

    @Test
    void gettersYSetters_funcionanCorrectamente() {
        Proceso proceso = new Proceso();

        UUID id = UUID.randomUUID();
        Empresa empresa = new Empresa();
        Pool pool = new Pool();
        Usuario usuario = new Usuario();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        proceso.setId(id);
        proceso.setEmpresa(empresa);
        proceso.setPool(pool);
        proceso.setCreatedByUser(usuario);
        proceso.setNombre("Proceso 1");
        proceso.setDescripcion("Descripción");
        proceso.setCategoria("Categoría");
        proceso.setEstado("activo");
        proceso.setActivo(true);
        proceso.setCreatedAt(createdAt);
        proceso.setUpdatedAt(updatedAt);

        assertEquals(id, proceso.getId());
        assertEquals(empresa, proceso.getEmpresa());
        assertEquals(pool, proceso.getPool());
        assertEquals(usuario, proceso.getCreatedByUser());
        assertEquals("Proceso 1", proceso.getNombre());
        assertEquals("Descripción", proceso.getDescripcion());
        assertEquals("Categoría", proceso.getCategoria());
        assertEquals("activo", proceso.getEstado());
        assertTrue(proceso.isActivo());
        assertEquals(createdAt, proceso.getCreatedAt());
        assertEquals(updatedAt, proceso.getUpdatedAt());
    }

    @Test
    void constructorVacio_tieneValoresPorDefecto() {
        Proceso proceso = new Proceso();

        assertNotNull(proceso);
        assertEquals("borrador", proceso.getEstado());
        assertTrue(proceso.isActivo());
        assertNull(proceso.getCreatedAt());
        assertNull(proceso.getUpdatedAt());
    }

    @Test
    void prePersist_asignaCreatedAt() {
        Proceso proceso = new Proceso();

        assertNull(proceso.getCreatedAt());

        proceso.prePersist();

        assertNotNull(proceso.getCreatedAt());
    }

    @Test
    void preUpdate_asignaUpdatedAt() {
        Proceso proceso = new Proceso();

        assertNull(proceso.getUpdatedAt());

        proceso.preUpdate();

        assertNotNull(proceso.getUpdatedAt());
    }
}