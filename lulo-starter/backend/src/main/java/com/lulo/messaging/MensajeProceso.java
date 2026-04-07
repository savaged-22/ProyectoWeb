package com.lulo.messaging;

import com.lulo.company.Empresa;
import com.lulo.process.Proceso;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mensaje_proceso")
public class MensajeProceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proceso_origen_id", nullable = false)
    private Proceso procesoOrigen;

    @Column(name = "nombre_mensaje", nullable = false)
    private String nombreMensaje;

    @Column(name = "payload_json", columnDefinition = "jsonb")
    private String payloadJson;

    @Column(name = "correlation_key")
    private String correlationKey;

    @Column(nullable = false)
    private String estado = "pendiente";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
