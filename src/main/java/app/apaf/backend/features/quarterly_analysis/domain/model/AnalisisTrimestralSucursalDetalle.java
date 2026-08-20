package app.apaf.backend.features.quarterly_analysis.domain.model;

import app.apaf.backend.features.quarterly_analysis.domain.enumtype.PeriodoRol;
import app.apaf.backend.features.quarterly_analysis.domain.enumtype.TipoCarteraAnalitica;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "analisis_trimestral_sucursal_detalle")
public class AnalisisTrimestralSucursalDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    @Column(name = "id_ejecucion", nullable = false)
    private UUID idEjecucion;


    @Column(name = "sucursal_codigo", length = 20)
    private String sucursalCodigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cartera", nullable = false, length = 20)
    private TipoCarteraAnalitica tipoCartera;

    @Column(name = "creditos_vigentes", nullable = false)
    private Long creditosVigentes;

    @Column(name = "capital_vigente", nullable = false, precision = 19, scale = 4)
    private BigDecimal capitalVigente;

    @Column(name = "intereses_vigentes", nullable = false, precision = 19, scale = 4)
    private BigDecimal interesesVigentes;

    @Column(name = "cartera_vigente", nullable = false, precision = 19, scale = 4)
    private BigDecimal carteraVigente;

    @Column(name = "creditos_vencidos", nullable = false)
    private Long creditosVencidos;

    @Column(name = "capital_vencido", nullable = false, precision = 19, scale = 4)
    private BigDecimal capitalVencido;

    @Column(name = "intereses_vencidos", nullable = false, precision = 19, scale = 4)
    private BigDecimal interesesVencidos;

    @Column(name = "cartera_vencida", nullable = false, precision = 19, scale = 4)
    private BigDecimal carteraVencida;

    @Column(name = "creditos_total", nullable = false)
    private Long creditosTotal;

    @Column(name = "cartera_total", nullable = false, precision = 19, scale = 4)
    private BigDecimal carteraTotal;

    @Column(name = "proporcion_global_porcentaje", precision = 7, scale = 4)
    private BigDecimal proporcionGlobalPorcentaje;

    @Column(name = "proporcion_dentro_sucursal_porcentaje", precision = 7, scale = 4)
    private BigDecimal proporcionDentroSucursalPorcentaje;

    public AnalisisTrimestralSucursalDetalle() {
    }

    public Long getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Long idDetalle) {
        this.idDetalle = idDetalle;
    }

    public UUID getIdEjecucion() {
        return idEjecucion;
    }

    public void setIdEjecucion(UUID idEjecucion) {
        this.idEjecucion = idEjecucion;
    }



    public String getSucursalCodigo() {
        return sucursalCodigo;
    }

    public void setSucursalCodigo(String sucursalCodigo) {
        this.sucursalCodigo = sucursalCodigo;
    }

    public TipoCarteraAnalitica getTipoCartera() {
        return tipoCartera;
    }

    public void setTipoCartera(TipoCarteraAnalitica tipoCartera) {
        this.tipoCartera = tipoCartera;
    }

    public Long getCreditosVigentes() {
        return creditosVigentes;
    }

    public void setCreditosVigentes(Long creditosVigentes) {
        this.creditosVigentes = creditosVigentes;
    }

    public BigDecimal getCapitalVigente() {
        return capitalVigente;
    }

    public void setCapitalVigente(BigDecimal capitalVigente) {
        this.capitalVigente = capitalVigente;
    }

    public BigDecimal getInteresesVigentes() {
        return interesesVigentes;
    }

    public void setInteresesVigentes(BigDecimal interesesVigentes) {
        this.interesesVigentes = interesesVigentes;
    }

    public BigDecimal getCarteraVigente() {
        return carteraVigente;
    }

    public void setCarteraVigente(BigDecimal carteraVigente) {
        this.carteraVigente = carteraVigente;
    }

    public Long getCreditosVencidos() {
        return creditosVencidos;
    }

    public void setCreditosVencidos(Long creditosVencidos) {
        this.creditosVencidos = creditosVencidos;
    }

    public BigDecimal getCapitalVencido() {
        return capitalVencido;
    }

    public void setCapitalVencido(BigDecimal capitalVencido) {
        this.capitalVencido = capitalVencido;
    }

    public BigDecimal getInteresesVencidos() {
        return interesesVencidos;
    }

    public void setInteresesVencidos(BigDecimal interesesVencidos) {
        this.interesesVencidos = interesesVencidos;
    }

    public BigDecimal getCarteraVencida() {
        return carteraVencida;
    }

    public void setCarteraVencida(BigDecimal carteraVencida) {
        this.carteraVencida = carteraVencida;
    }

    public Long getCreditosTotal() {
        return creditosTotal;
    }

    public void setCreditosTotal(Long creditosTotal) {
        this.creditosTotal = creditosTotal;
    }

    public BigDecimal getCarteraTotal() {
        return carteraTotal;
    }

    public void setCarteraTotal(BigDecimal carteraTotal) {
        this.carteraTotal = carteraTotal;
    }

    public BigDecimal getProporcionGlobalPorcentaje() {
        return proporcionGlobalPorcentaje;
    }

    public void setProporcionGlobalPorcentaje(BigDecimal proporcionGlobalPorcentaje) {
        this.proporcionGlobalPorcentaje = proporcionGlobalPorcentaje;
    }

    public BigDecimal getProporcionDentroSucursalPorcentaje() {
        return proporcionDentroSucursalPorcentaje;
    }

    public void setProporcionDentroSucursalPorcentaje(BigDecimal proporcionDentroSucursalPorcentaje) {
        this.proporcionDentroSucursalPorcentaje = proporcionDentroSucursalPorcentaje;
    }
}
