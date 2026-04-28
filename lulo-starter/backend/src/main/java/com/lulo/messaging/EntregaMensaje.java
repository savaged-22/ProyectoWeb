package com.lulo.messaging;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "entrega_mensaje",
        uniqueConstraints = @UniqueConstraint(columnNames = {"mensaje_id", "suscripcion_id"}))
public class EntregaMensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensaje_id", nullable = false)
    private MensajeProceso mensaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suscripcion_id", nullable = false)
    private SuscripcionMensaje suscripcion;

    /** pendiente | confirmado */
    @Column(nullable = false)
    private String estado = "pendiente";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "confirmado_at")
    private LocalDateTime confirmadoAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
