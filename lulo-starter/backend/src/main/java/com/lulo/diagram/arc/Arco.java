package com.lulo.diagram.arc;

import com.lulo.common.audit.AuditableEntity;
import com.lulo.diagram.node.Nodo;
import com.lulo.process.Proceso;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "arco")
public class Arco extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proceso_id", nullable = false)
    private Proceso proceso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_nodo_id", nullable = false)
    private Nodo fromNodo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_nodo_id", nullable = false)
    private Nodo toNodo;

    @Column(name = "condicion_expr")
    private String condicionExpr;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "props_json", columnDefinition = "jsonb")
    private String propsJson;

    @Column(nullable = false)
    private boolean activo = true;
}
