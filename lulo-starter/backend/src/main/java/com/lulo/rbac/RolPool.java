package com.lulo.rbac;

import com.lulo.common.audit.AuditableEntity;
import com.lulo.pool.Pool;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rol_pool")
@Filter(name = "empresaFilter",
        condition = "pool_id IN (SELECT p.id FROM pool p WHERE p.empresa_id = :empresaId)")
public class RolPool extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pool_id", nullable = false)
    private Pool pool;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "es_propietario", nullable = false)
    private boolean esPropietario = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rol_pool_permiso",
            joinColumns = @JoinColumn(name = "rol_pool_id"),
            inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<Permiso> permisos = new HashSet<>();
}
