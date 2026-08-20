package app.apaf.backend.features.quarterly_analysis.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ProductoResultadoId implements Serializable {
    private UUID idEjecucion;
    private String productoCodigo;

    public ProductoResultadoId() {}

    public ProductoResultadoId(UUID idEjecucion, String productoCodigo) {
        this.idEjecucion = idEjecucion;
        this.productoCodigo = productoCodigo;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoResultadoId that = (ProductoResultadoId) o;
        return Objects.equals(idEjecucion, that.idEjecucion) && Objects.equals(productoCodigo, that.productoCodigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEjecucion, productoCodigo);
    }
}
