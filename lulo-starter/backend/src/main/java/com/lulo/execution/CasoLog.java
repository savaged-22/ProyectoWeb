package com.lulo.execution;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "caso_log")
public class CasoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caso_id", nullable = true)
    private Caso caso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proceso_id", nullable = true)
    private com.lulo.process.Proceso proceso;

    @Column(nullable = true)
    private String estado;

    @Column(nullable = false)
    private String nivel; // INFO, WARN, ERROR

    @Column(nullable = false, length = 1000)
    private String mensaje;

    @Column(name = "fecha", nullable = false, updatable = false)
    private LocalDateTime fecha;

    @PrePersist
    void prePersist() {
        this.fecha = LocalDateTime.now();
    }
}
