package app.apaf.backend.features.cartera_eprc.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "eprc_estratificacion_detalle")
@Getter
@Setter
@NoArgsConstructor
public class EprcEstratificacionDetalleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    @Column(name = "id_ejecucion", nullable = false)
    private UUID idEjecucion;

    @Column(name = "tipo_cartera", nullable = false)
    private String tipoCartera;

    @Column(name = "codigo_intervalo", nullable = false)
    private String codigoIntervalo;

    @Column(name = "intervalo_vencimiento", nullable = false)
    private String intervaloVencimiento;

    @Column(name = "numero_creditos", nullable = false)
    private Long numeroCreditos = 0L;

    @Column(name = "saldo_capital", nullable = false)
    private BigDecimal saldoCapital = BigDecimal.ZERO;

    @Column(name = "saldo_interes_vigente", nullable = false)
    private BigDecimal saldoInteresVigente = BigDecimal.ZERO;

    @Column(name = "saldo_interes_vencido", nullable = false)
    private BigDecimal saldoInteresVencido = BigDecimal.ZERO;

    @Column(name = "saldo_cartera_total", nullable = false)
    private BigDecimal saldoCarteraTotal = BigDecimal.ZERO;

    @Column(name = "garantia_liquida", nullable = false)
    private BigDecimal garantiaLiquida = BigDecimal.ZERO;

    @Column(name = "garantia_hipotecaria", nullable = false)
    private BigDecimal garantiaHipotecaria = BigDecimal.ZERO;

    @Column(name = "eprc_parte_cubierta", nullable = false)
    private BigDecimal eprcParteCubierta = BigDecimal.ZERO;

    @Column(name = "eprc_parte_expuesta", nullable = false)
    private BigDecimal eprcParteExpuesta = BigDecimal.ZERO;

    @Column(name = "est_prev_intereses_vencidos", nullable = false)
    private BigDecimal estPrevInteresesVencidos = BigDecimal.ZERO;

    @Column(name = "importe_estimacion_preventiva", nullable = false)
    private BigDecimal importeEstimacionPreventiva = BigDecimal.ZERO;
}
