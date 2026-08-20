
package app.apaf.backend.features.cartera_management.totales;

import java.math.BigDecimal;

public record FlujosRecuperacionResponse(
        BigDecimal ultimosPagosCapital,
        BigDecimal ultimosPagosInteres) {
}
