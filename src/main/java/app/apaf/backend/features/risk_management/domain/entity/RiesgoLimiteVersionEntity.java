package app.apaf.backend.features.risk_management.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import app.apaf.backend.features.risk_management.domain.TipoLimite;
import app.apaf.backend.features.risk_management.domain.AccionLimite;

@Entity
@Table(name = "riesgo_limite_version")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RiesgoLimiteVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_version", updatable = false, nullable = false)
    private UUID idVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_limite", nullable = false)
    private RiesgoLimiteEntity riesgoLimite;

    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_limite", nullable = false, length = 10)
    private TipoLimite tipoLimite;

    @Column(name = "limite_porcentaje", nullable = false, precision = 7, scale = 4)
    private BigDecimal limitePorcentaje;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Enumerated(EnumType.STRING)
    @Column(name = "accion", nullable = false, length = 20)
    private AccionLimite accion;

    @Column(name = "vigente_desde", nullable = false)
    private LocalDateTime vigenteDesde;

    @Column(name = "vigente_hasta")
    private LocalDateTime vigenteHasta;

    @Column(name = "realizado_por")
    private Long realizadoPor;

    @Column(name = "actor", nullable = false, length = 150)
    private String actor;

    @Column(name = "origen", nullable = false, length = 30)
    private String origen;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public RiesgoLimiteVersionEntity(RiesgoLimiteEntity riesgoLimite, Integer numeroVersion, TipoLimite tipoLimite,
                               BigDecimal limitePorcentaje, Boolean activo, AccionLimite accion,
                               Long realizadoPor, String actor, String origen) {
        this.riesgoLimite = riesgoLimite;
        this.numeroVersion = numeroVersion;
        this.tipoLimite = tipoLimite;
        this.limitePorcentaje = limitePorcentaje;
        this.activo = activo;
        this.accion = accion;
        this.realizadoPor = realizadoPor;
        this.actor = actor;
        this.origen = origen;
        this.vigenteDesde = LocalDateTime.now();
        this.fechaRegistro = LocalDateTime.now();
    }
    
    public void closeVersion() {
        this.vigenteHasta = LocalDateTime.now();
    }
}
