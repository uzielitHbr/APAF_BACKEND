package app.apaf.backend.features.cartera_management.totales;

import java.math.BigDecimal;

public record SaldosDevengadosResponse(
    BigDecimal capitalVigente,
    BigDecimal capitalVencido,
    BigDecimal interesesVigentes,
    BigDecimal interesesVencidos,
    BigDecimal interesesOrden
) {}
