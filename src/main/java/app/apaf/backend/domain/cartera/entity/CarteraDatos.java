package app.apaf.backend.domain.cartera.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cartera_datos", uniqueConstraints = @UniqueConstraint(name = "uk_cartera_mes_contrato", columnNames = {
        "mes_corte", "numero_contrato" }))
@Getter
@Setter
@NoArgsConstructor
public class CarteraDatos {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_analisis_mensual", updatable = false, nullable = false)
    private UUID idAnalisisMensual;

    @Column(name = "mes_corte", nullable = false)
    private LocalDate mesCorte;

    @Column(name = "fecha_corte", nullable = false)
    private LocalDate fechaCorte;

    // A-AU fields
    @Column(name = "nombre_acreditado")
    private String nombreAcreditado;

    @Column(name = "numero_socio", nullable = false)
    private String numeroSocio;

    @Column(name = "numero_contrato", nullable = false)
    private String numeroContrato;

    @Column(name = "sucursal", nullable = false)
    private String sucursal;

    @Column(name = "clasificacion_credito")
    private String clasificacionCredito;

    @Column(name = "producto_credito")
    private String productoCredito;

    @Column(name = "modalidad_pago")
    private String modalidadPago;

    @Column(name = "fecha_otorgamiento")
    private LocalDate fechaOtorgamiento;

    @Column(name = "monto_original")
    private BigDecimal montoOriginal;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "tasa_ordinaria_nominal_anual")
    private BigDecimal tasaOrdinariaNominalAnual;

    @Column(name = "tasa_moratoria_nominal_anual")
    private BigDecimal tasaMoratoriaNominalAnual;

    @Column(name = "plazo_credito_meses")
    private Integer plazoCreditoMeses;

    @Column(name = "frecuencia_pago_capital")
    private String frecuenciaPagoCapital;

    @Column(name = "frecuencia_pago_intereses")
    private String frecuenciaPagoIntereses;

    @Column(name = "dias_mora", nullable = false)
    private Integer diasMora;

    @Column(name = "capital_vigente", nullable = false)
    private BigDecimal capitalVigente;

    @Column(name = "capital_vencido", nullable = false)
    private BigDecimal capitalVencido;

    @Column(name = "int_dev_no_cobrados_vigentes", nullable = false)
    private BigDecimal intDevNoCobradosVigentes;

    @Column(name = "int_dev_no_cobrados_vencidos", nullable = false)
    private BigDecimal intDevNoCobradosVencidos;

    @Column(name = "int_dev_no_cobrados_ctas_orden", nullable = false)
    private BigDecimal intDevNoCobradosCtasOrden;

    @Column(name = "fecha_ultimo_pago_capital")
    private LocalDate fechaUltimoPagoCapital;

    @Column(name = "monto_ultimo_pago_capital", nullable = false)
    private BigDecimal montoUltimoPagoCapital;

    @Column(name = "fecha_ultimo_pago_intereses")
    private LocalDate fechaUltimoPagoIntereses;

    @Column(name = "monto_ultimo_pago_intereses", nullable = false)
    private BigDecimal montoUltimoPagoIntereses;

    @Column(name = "renovado_reestructurado_normal")
    private String renovadoReestructuradoNormal;

    @Column(name = "emproblemado", nullable = false)
    private Boolean emproblemado;

    @Column(name = "vigente_o_vencido")
    private String vigenteOVencido;

    @Column(name = "cargo_acreditado_parte_relacionada")
    private String cargoAcreditadoParteRelacionada;

    @Column(name = "monto_garantia_liquida", nullable = false)
    private BigDecimal montoGarantiaLiquida;

    @Column(name = "cuenta_garantia_liquida")
    private String cuentaGarantiaLiquida;

    @Column(name = "monto_garantia_prendaria", nullable = false)
    private BigDecimal montoGarantiaPrendaria;

    @Column(name = "monto_garantia_hipotecaria", nullable = false)
    private BigDecimal montoGarantiaHipotecaria;

    @Column(name = "eprc_contable_parte_cubierta", nullable = false)
    private BigDecimal eprcContableParteCubierta;

    @Column(name = "eprc_contable_parte_expuesta", nullable = false)
    private BigDecimal eprcContableParteExpuesta;

    @Column(name = "eprc_contable_x_intereses_cee", nullable = false)
    private BigDecimal eprcContableXInteresesCee;

    @Column(name = "importe_estimacion_adicional", nullable = false)
    private BigDecimal importeEstimacionAdicional;

    @Column(name = "localidad")
    private String localidad;

    @Column(name = "estado")
    private String estado;

    @Column(name = "ocupacion")
    private String ocupacion;

    @Column(name = "municipio")
    private String municipio;

    @Column(name = "genero")
    private String genero;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "edad")
    private Short edad;

    @Column(name = "tipo_cartera_calificacion")
    private String tipoCarteraCalificacion;

    @Column(name = "finalidad_credito")
    private String finalidadCredito;

    @Column(name = "cce")
    private String cce;

    @Column(name = "id_importacion")
    private UUID idImportacion;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion;
}
