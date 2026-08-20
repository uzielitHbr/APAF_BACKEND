package app.apaf.backend.features.cartera_management.totales;

import java.math.BigDecimal;

public record ResumenGeneralResponse(
    BigDecimal totalCartera,
    BigDecimal totalMontoOriginal,
    Long totalNumeroCreditos,
    Long totalCreditosVigentes,
    Long totalCreditosVencidos
) {}
