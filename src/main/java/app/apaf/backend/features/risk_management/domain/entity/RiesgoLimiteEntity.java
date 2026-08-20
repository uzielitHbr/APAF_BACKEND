package app.apaf.backend.features.risk_management.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;

@Entity
@Table(name = "riesgo_limite")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RiesgoLimiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_limite", updatable = false, nullable = false)
    private UUID idLimite;

    @Enumerated(EnumType.STRING)
    @Column(name = "agrupacion", nullable = false, length = 40)
    private AgrupacionRiesgo agrupacion;

    @Column(name = "clave", nullable = false, length = 160)
    private String clave;

    @Column(name = "identificacion", nullable = false, length = 255)
    private String identificacion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Version
    @Column(name = "version_lock", nullable = false)
    private Long versionLock;

    public RiesgoLimiteEntity(AgrupacionRiesgo agrupacion, String clave, String identificacion) {
        this.agrupacion = agrupacion;
        this.clave = clave;
        this.identificacion = identificacion;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void markUpdated() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
