package app.apaf.backend.features.risk_management.domain.entity;

import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import app.apaf.backend.features.risk_management.domain.TipoLimite;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "riesgo_limite")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RiesgoLimiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_limite")
    private UUID idLimite;

    @Enumerated(EnumType.STRING)
    @Column(name = "agrupacion")
    private AgrupacionRiesgo agrupacion;

    @Column(name = "clave")
    private String clave;

    @Column(name = "identificacion")
    private String identificacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_limite")
    private TipoLimite tipoLimite;

    @Column(name = "porcentaje_actual")
    private BigDecimal porcentajeActual;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Version
    @Column(name = "version_lock")
    private Long versionLock;

    public RiesgoLimiteEntity(AgrupacionRiesgo agrupacion, String clave, String identificacion, TipoLimite tipoLimite, BigDecimal porcentajeActual) {
        this.agrupacion = agrupacion;
        this.clave = clave;
        this.identificacion = identificacion;
        this.tipoLimite = tipoLimite;
        this.porcentajeActual = porcentajeActual;
        this.activo = true;
    }

    public void updateLimite(TipoLimite tipoLimite, BigDecimal nuevoPorcentaje) {
        this.tipoLimite = tipoLimite;
        this.porcentajeActual = nuevoPorcentaje;
        this.activo = true;
    }
    
    public void desactivar() {
        this.activo = false;
    }

    public void reactivar() {
        this.activo = true;
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        versionLock = 0L;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
