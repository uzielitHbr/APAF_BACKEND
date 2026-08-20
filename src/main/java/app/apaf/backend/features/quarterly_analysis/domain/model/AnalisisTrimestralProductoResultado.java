package app.apaf.backend.features.quarterly_analysis.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "analisis_trimestral_producto_resultado")
@IdClass(ProductoResultadoId.class)
public class AnalisisTrimestralProductoResultado {

    @Id
    @Column(name = "id_ejecucion", nullable = false)
    private UUID idEjecucion;

    @Id
    @Column(name = "producto_codigo", nullable = false, length = 50)
    private String productoCodigo;

    @Column(name = "producto_nombre", nullable = false, length = 150)
    private String productoNombre;

    @Column(name = "creditos_vencidos", nullable = false)
    private Long creditosVencidos;

    @Column(name = "importe_vencido", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeVencido;

    @Column(name = "proporcion_porcentaje", nullable = false, precision = 7, scale = 4)
    private BigDecimal proporcionPorcentaje;

    public AnalisisTrimestralProductoResultado() {
    }

    public UUID getIdEjecucion() {
        return idEjecucion;
    }

    public void setIdEjecucion(UUID idEjecucion) {
        this.idEjecucion = idEjecucion;
    }

    public String getProductoCodigo() {
        return productoCodigo;
    }

    public void setProductoCodigo(String productoCodigo) {
        this.productoCodigo = productoCodigo;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public Long getCreditosVencidos() {
        return creditosVencidos;
    }

    public void setCreditosVencidos(Long creditosVencidos) {
        this.creditosVencidos = creditosVencidos;
    }

    public BigDecimal getImporteVencido() {
        return importeVencido;
    }

    public void setImporteVencido(BigDecimal importeVencido) {
        this.importeVencido = importeVencido;
    }

    public BigDecimal getProporcionPorcentaje() {
        return proporcionPorcentaje;
    }

    public void setProporcionPorcentaje(BigDecimal proporcionPorcentaje) {
        this.proporcionPorcentaje = proporcionPorcentaje;
    }
}
