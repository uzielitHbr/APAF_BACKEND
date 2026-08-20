package app.apaf.backend.features.risk_management.analysis;

import java.math.BigDecimal;

public interface TotalesGlobalesProjection {
    Long getNumeroCreditos();
    BigDecimal getCarteraVigente();
    BigDecimal getCarteraVencida();
    BigDecimal getCarteraTotal();
}
