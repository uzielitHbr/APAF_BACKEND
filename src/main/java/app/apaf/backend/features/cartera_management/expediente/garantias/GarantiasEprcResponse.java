package app.apaf.backend.features.cartera_management.expediente.garantias;

import java.math.BigDecimal;

public record GarantiasEprcResponse(
    String cargoAcreditadoParteRelacionada,
    BigDecimal montoGarantiaLiquida,
    String cuentaGarantiaLiquida,
    BigDecimal montoGarantiaPrendaria,
    BigDecimal montoGarantiaHipotecaria,
    BigDecimal eprcContableParteCubierta,
    BigDecimal eprcContableParteExpuesta,
    BigDecimal eprcContableXInteresesCee,
    BigDecimal importeEstimacionAdicional
) {}
