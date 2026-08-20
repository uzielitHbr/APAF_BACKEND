package app.apaf.backend.features.cartera_management.totales;

import java.math.BigDecimal;

public record RiesgoYRegulatorioResponse(
    Long totalDiasMora,
    BigDecimal garantiaLiquida,
    BigDecimal eprcParteCubierta,
    BigDecimal eprcParteExpuesta,
    BigDecimal eprcInteresCee
) {}
