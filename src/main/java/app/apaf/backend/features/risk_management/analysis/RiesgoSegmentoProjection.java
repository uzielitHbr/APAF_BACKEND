package app.apaf.backend.features.risk_management.analysis;

import java.math.BigDecimal;

public interface RiesgoSegmentoProjection {
    String getIdLimite();
    String getClave();
    String getIdentificacion();
    Long getNumeroCreditos();
    BigDecimal getCarteraVigente();
    BigDecimal getCarteraVencida();
    BigDecimal getCarteraTotal();
    String getTipoLimite();
    BigDecimal getLimiteEstablecidoPorcentaje();
}
