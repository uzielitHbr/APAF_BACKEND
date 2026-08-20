package app.apaf.backend.features.cartera_eprc.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "eprc_resumen_global")
@Getter
@Setter
@NoArgsConstructor
public class EprcResumenGlobalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_resumen")
    private UUID idResumen;

    @Column(name = "id_ejecucion", nullable = false, unique = true)
    private UUID idEjecucion;

    @Column(name = "reservas_requeridas", nullable = false)
    private BigDecimal reservasRequeridas = BigDecimal.ZERO;

    @Column(name = "cartera_total_cuadro", nullable = false)
    private BigDecimal carteraTotalCuadro = BigDecimal.ZERO;
}
