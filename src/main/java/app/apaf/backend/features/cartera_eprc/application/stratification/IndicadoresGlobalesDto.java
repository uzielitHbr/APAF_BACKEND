package app.apaf.backend.features.cartera_eprc.application.stratification;

import java.math.BigDecimal;

public record IndicadoresGlobalesDto(
    BigDecimal reservasRequeridas,
    BigDecimal carteraTotalCuadro
) {}
