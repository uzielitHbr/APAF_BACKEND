package app.apaf.backend.features.cartera_eprc.application.stratification;

import java.math.BigDecimal;

public record DetalleIntervaloEprcDto(
    String intervaloVencimiento,
    Long numeroCreditos,
    BigDecimal saldoCapital,
    BigDecimal saldoInteresVigente,
    BigDecimal saldoInteresVencido,
    BigDecimal saldoCarteraTotal,
    BigDecimal garantiaLiquida,
    BigDecimal eprcParteCubierta,
    BigDecimal eprcParteExpuesta,
    BigDecimal estPrevInteresesVencidos,
    BigDecimal importeEstimacionPreventiva
) {}
