package com.lulo.execution;

import java.time.LocalDateTime;
import java.util.UUID;

import com.lulo.process.Proceso;
import com.lulo.users.Usuario;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "caso")
public class Caso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proceso_id", nullable = false)
    private Proceso proceso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iniciado_por_id", nullable = false)
    private Usuario iniciadoPor;

    // PENDIENTE, EN_PROGRESO, COMPLETADO, ERROR, CANCELADO
    @Column(nullable = false)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_inicio", nullable = false, updatable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @PrePersist
    void prePersist() {
        this.fechaInicio = LocalDateTime.now();
    }
}
