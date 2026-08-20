package app.apaf.backend.features.quarterly_analysis.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "analisis_trimestral_sucursal_resumen")
public class AnalisisTrimestralSucursalResumen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resumen")
    private Long idResumen;

    @Column(name = "id_ejecucion", nullable = false)
    private UUID idEjecucion;

    @Column(name = "sucursal_codigo", length = 20)
    private String sucursalCodigo;

    @Column(name = "imor_porcentaje", nullable = false, precision = 7, scale = 4)
    private BigDecimal imorPorcentaje;

    @Column(name = "cartera_total", nullable = false, precision = 19, scale = 4)
    private BigDecimal carteraTotal;

    @Column(name = "cartera_vencida", nullable = false, precision = 19, scale = 4)
    private BigDecimal carteraVencida;

    @Column(name = "proporcion_global", nullable = false, precision = 7, scale = 4)
    private BigDecimal proporcionGlobal;

    public AnalisisTrimestralSucursalResumen() {
    }

    public Long getIdResumen() {
        return idResumen;
    }

    public void setIdResumen(Long idResumen) {
        this.idResumen = idResumen;
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

    public BigDecimal getImorPorcentaje() {
        return imorPorcentaje;
    }

    public void setImorPorcentaje(BigDecimal imorPorcentaje) {
        this.imorPorcentaje = imorPorcentaje;
    }

    public BigDecimal getCarteraTotal() {
        return carteraTotal;
    }

    public void setCarteraTotal(BigDecimal carteraTotal) {
        this.carteraTotal = carteraTotal;
    }

    public BigDecimal getCarteraVencida() {
        return carteraVencida;
    }

    public void setCarteraVencida(BigDecimal carteraVencida) {
        this.carteraVencida = carteraVencida;
    }

    public BigDecimal getProporcionGlobal() {
        return proporcionGlobal;
    }

    public void setProporcionGlobal(BigDecimal proporcionGlobal) {
        this.proporcionGlobal = proporcionGlobal;
    }
}
