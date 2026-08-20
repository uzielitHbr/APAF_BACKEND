package app.apaf.backend.features.cartera_eprc.application.stratification;

import java.math.BigDecimal;

public record TotalesEprcDto(
    String intervaloVencimiento,
    Long numeroCreditos,
    BigDecimal saldoCapital,
    BigDecimal saldoInteresVigente,
    BigDecimal saldoInteresVencido,
    BigDecimal saldoCarteraTotal,
    BigDecimal garantiaLiquida,
    BigDecimal garantiaHipotecaria,
    BigDecimal eprcParteCubierta,
    BigDecimal eprcParteExpuesta,
    BigDecimal estPrevInteresesVencidos,
    BigDecimal importeEstimacionPreventiva
) {}
