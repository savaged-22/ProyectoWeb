package com.lulo.process;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProcesoSpecTest {

    @Test
    void activos_retornaSpecificationNoNula() {
        Specification<Proceso> spec = ProcesoSpec.activos();

        assertNotNull(spec);
    }

    @Test
    void deEmpresa_retornaSpecificationNoNula() {
        UUID empresaId = UUID.randomUUID();

        Specification<Proceso> spec = ProcesoSpec.deEmpresa(empresaId);

        assertNotNull(spec);
    }

    @Test
    void conEstado_retornaSpecificationNoNula() {
        Specification<Proceso> spec = ProcesoSpec.conEstado("publicado");

        assertNotNull(spec);
    }

    @Test
    void conCategoria_retornaSpecificationNoNula() {
        Specification<Proceso> spec = ProcesoSpec.conCategoria("Operativo");

        assertNotNull(spec);
    }

    @Test
    void nombreContiene_retornaSpecificationNoNula() {
        Specification<Proceso> spec = ProcesoSpec.nombreContiene("compras");

        assertNotNull(spec);
    }

    @Test
    void nombreContiene_aceptaMayusculasYMinusculas() {
        Specification<Proceso> spec = ProcesoSpec.nombreContiene("CoMpRaS");

        assertNotNull(spec);
    }
}