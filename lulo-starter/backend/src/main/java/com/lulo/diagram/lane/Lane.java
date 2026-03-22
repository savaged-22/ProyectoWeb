package com.lulo.diagram.lane;

import com.lulo.common.audit.AuditableEntity;
import com.lulo.process.Proceso;
import com.lulo.rbac.RolProceso;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lane")
public class Lane extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proceso_id", nullable = false)
    private Proceso proceso;

    // Nullable: una lane puede no estar asociada a un rol funcional
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_proceso_id")
    private RolProceso rolProceso;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int orden = 0;
}
