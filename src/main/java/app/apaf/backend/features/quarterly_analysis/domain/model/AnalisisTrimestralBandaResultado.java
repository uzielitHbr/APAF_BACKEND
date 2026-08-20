package app.apaf.backend.features.quarterly_analysis.domain.model;

import app.apaf.backend.features.quarterly_analysis.domain.enumtype.ClasificacionAnalisis;
import app.apaf.backend.features.quarterly_analysis.domain.enumtype.TipoCarteraAnalitica;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "analisis_trimestral_banda_resultado")
@IdClass(BandaResultadoId.class)
public class AnalisisTrimestralBandaResultado {

    @Id
    @Column(name = "id_ejecucion", nullable = false)
    private UUID idEjecucion;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "clasificacion", nullable = false, length = 20)
    private ClasificacionAnalisis clasificacion;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cartera", nullable = false, length = 20)
    private TipoCarteraAnalitica tipoCartera;

    @Id
    @Column(name = "rango_id", nullable = false, length = 20)
    private String rangoId;

    @Column(name = "rango_etiqueta", nullable = false, length = 100)
    private String rangoEtiqueta;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "numero_creditos", nullable = false)
    private Long numeroCreditos;

    @Column(name = "importe_total", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeTotal;

    public AnalisisTrimestralBandaResultado() {
    }

    public UUID getIdEjecucion() {
        return idEjecucion;
    }

    public void setIdEjecucion(UUID idEjecucion) {
        this.idEjecucion = idEjecucion;
    }

    public ClasificacionAnalisis getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(ClasificacionAnalisis clasificacion) {
        this.clasificacion = clasificacion;
    }

    public TipoCarteraAnalitica getTipoCartera() {
        return tipoCartera;
    }

    public void setTipoCartera(TipoCarteraAnalitica tipoCartera) {
        this.tipoCartera = tipoCartera;
    }

    public String getRangoId() {
        return rangoId;
    }

    public void setRangoId(String rangoId) {
        this.rangoId = rangoId;
    }

    public String getRangoEtiqueta() {
        return rangoEtiqueta;
    }

    public void setRangoEtiqueta(String rangoEtiqueta) {
        this.rangoEtiqueta = rangoEtiqueta;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Long getNumeroCreditos() {
        return numeroCreditos;
    }

    public void setNumeroCreditos(Long numeroCreditos) {
        this.numeroCreditos = numeroCreditos;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }
}
