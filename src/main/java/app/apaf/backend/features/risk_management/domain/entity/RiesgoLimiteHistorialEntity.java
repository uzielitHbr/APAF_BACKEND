package app.apaf.backend.features.risk_management.domain.entity;

import app.apaf.backend.features.risk_management.domain.AccionLimite;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "riesgo_limite_historial")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RiesgoLimiteHistorialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_historial")
    private UUID idHistorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_limite", nullable = false)
    private RiesgoLimiteEntity riesgoLimite;

    @Enumerated(EnumType.STRING)
    @Column(name = "accion")
    private AccionLimite accion;

    @Column(name = "porcentaje_anterior")
    private BigDecimal porcentajeAnterior;

    @Column(name = "porcentaje_nuevo")
    private BigDecimal porcentajeNuevo;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "realizado_por")
    private Long realizadoPor;

    @Column(name = "actor")
    private String actor;

    @Column(name = "fecha_movimiento", updatable = false)
    private LocalDateTime fechaMovimiento;

    public RiesgoLimiteHistorialEntity(RiesgoLimiteEntity riesgoLimite, AccionLimite accion, BigDecimal porcentajeAnterior, BigDecimal porcentajeNuevo, String motivo, Long realizadoPor, String actor) {
        this.riesgoLimite = riesgoLimite;
        this.accion = accion;
        this.porcentajeAnterior = porcentajeAnterior;
        this.porcentajeNuevo = porcentajeNuevo;
        this.motivo = motivo;
        this.realizadoPor = realizadoPor;
        this.actor = actor;
    }

    @PrePersist
    protected void onCreate() {
        fechaMovimiento = LocalDateTime.now();
    }
}
