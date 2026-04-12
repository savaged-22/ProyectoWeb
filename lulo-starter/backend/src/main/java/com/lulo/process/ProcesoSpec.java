package com.lulo.process;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ProcesoSpec {

    private ProcesoSpec() {}

    public static Specification<Proceso> activos() {
        return (root, query, cb) -> cb.isTrue(root.get("activo"));
    }

    public static Specification<Proceso> deEmpresa(UUID empresaId) {
        return (root, query, cb) -> cb.equal(root.get("empresa").get("id"), empresaId);
    }

    public static Specification<Proceso> conEstado(String estado) {
        return (root, query, cb) -> cb.equal(root.get("estado"), estado);
    }

    public static Specification<Proceso> conCategoria(String categoria) {
        return (root, query, cb) -> cb.equal(root.get("categoria"), categoria);
    }

    public static Specification<Proceso> nombreContiene(String nombre) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
    }
}
