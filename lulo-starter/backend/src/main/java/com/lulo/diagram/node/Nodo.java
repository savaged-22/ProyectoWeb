package com.lulo.diagram.node;

import java.util.UUID;

import com.lulo.common.audit.AuditableEntity;
import com.lulo.diagram.lane.Lane;
import com.lulo.process.Proceso;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad base del diagrama. Usa herencia JOINED:
 *   - 'actividad' → Actividad (tabla propia)
 *   - 'gateway'   → Gateway   (tabla propia)
 *   - 'nodo'      → Nodo base (inicio, fin, evento — sin tabla extra)
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "nodo")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("nodo")
public class Nodo extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proceso_id", nullable = false)
    private Proceso proceso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lane_id")
    private Lane lane;

    // Columna gestionada por Hibernate como discriminador — solo lectura desde la app
    @Column(name = "tipo", insertable = false, updatable = false)
    private String tipo;

    private String label;

    @Column(name = "pos_x")
    private Float posX;

    @Column(name = "pos_y")
    private Float posY;
}
