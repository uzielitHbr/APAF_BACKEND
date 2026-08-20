package app.apaf.backend.features.follow_up_management.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "seguimiento_plazo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeguimientoPlazoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_plazo", updatable = false, nullable = false)
    private UUID idPlazo;

    @Column(name = "mes_corte", nullable = false)
    private LocalDate mesCorte;

    @Column(name = "sucursal")
    private String sucursal;

    @Column(name = "tipo_vista", nullable = false, length = 20)
    private String tipoVista;

    @Column(name = "plazo_remanente", nullable = false, length = 50)
    private String plazoRemanente;

    @Column(name = "numero_creditos", nullable = false)
    private Integer numeroCreditos;

    @Column(name = "capital_vigente", nullable = false, precision = 19, scale = 4)
    private BigDecimal capitalVigente;

    @Column(name = "interes_ord_vigente", nullable = false, precision = 19, scale = 4)
    private BigDecimal interesOrdVigente;

    @Column(name = "capital_vencido", nullable = false, precision = 19, scale = 4)
    private BigDecimal capitalVencido;

    @Column(name = "interes_ord_vencido", nullable = false, precision = 19, scale = 4)
    private BigDecimal interesOrdVencido;

    @Column(name = "cuentas_orden", nullable = false, precision = 19, scale = 4)
    private BigDecimal cuentasOrden;

    @Column(name = "saldo_total", nullable = false, precision = 19, scale = 4)
    private BigDecimal saldoTotal;

    @Column(name = "creditos_con_movimiento", nullable = false)
    private Integer creditosConMovimiento;

    @Column(name = "creditos_sin_movimiento", nullable = false)
    private Integer creditosSinMovimiento;

    @Column(name = "creditos_otorgados_mes", nullable = false)
    private Integer creditosOtorgadosMes;

    @Column(name = "imor", nullable = false, precision = 19, scale = 4)
    private BigDecimal imor;

    @Column(name = "proporcion", nullable = false, precision = 19, scale = 4)
    private BigDecimal proporcion;
}
