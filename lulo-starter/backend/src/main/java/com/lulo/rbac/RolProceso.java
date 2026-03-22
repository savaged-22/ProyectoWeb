package com.lulo.rbac;

import com.lulo.common.audit.AuditableEntity;
import com.lulo.company.Empresa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rol_proceso")
@Filter(name = "empresaFilter", condition = "empresa_id = :empresaId")
public class RolProceso extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;
}
