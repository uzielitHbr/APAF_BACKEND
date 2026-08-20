package app.apaf.backend.features.quarterly_analysis.domain.model;

import app.apaf.backend.features.quarterly_analysis.domain.enumtype.ClasificacionAnalisis;
import app.apaf.backend.features.quarterly_analysis.domain.enumtype.TipoCarteraAnalitica;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class BandaResultadoId implements Serializable {
    private UUID idEjecucion;
    private ClasificacionAnalisis clasificacion;
    private TipoCarteraAnalitica tipoCartera;
    private String rangoId;

    public BandaResultadoId() {}

    public BandaResultadoId(UUID idEjecucion, ClasificacionAnalisis clasificacion, TipoCarteraAnalitica tipoCartera, String rangoId) {
        this.idEjecucion = idEjecucion;
        this.clasificacion = clasificacion;
        this.tipoCartera = tipoCartera;
        this.rangoId = rangoId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BandaResultadoId that = (BandaResultadoId) o;
        return Objects.equals(idEjecucion, that.idEjecucion) && clasificacion == that.clasificacion && tipoCartera == that.tipoCartera && Objects.equals(rangoId, that.rangoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEjecucion, clasificacion, tipoCartera, rangoId);
    }
}
