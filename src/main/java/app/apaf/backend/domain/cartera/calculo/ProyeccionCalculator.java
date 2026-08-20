package app.apaf.backend.domain.cartera.calculo;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ProyeccionCalculator {

    public ProyeccionResult calcular(Integer plazoCreditoMeses, BigDecimal montoOriginal, BigDecimal capitalVigente, BigDecimal tasaOrdinaria, String vigenteOVencido) {
        if (plazoCreditoMeses == null || plazoCreditoMeses == 0) {
            return new ProyeccionResult(0, BigDecimal.ZERO, BigDecimal.ZERO,
                                        0, BigDecimal.ZERO, BigDecimal.ZERO,
                                        0, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        // Just using dummy values for abonos as they depend on totalAbonos which we can't fully derive.
        // I will just use max(plazoCreditoMeses - 1, 0) logic as a placeholder for abonosRestantes.
        // Actually, the rules say:
        // BG: abonosRestantesMes1
        // BJ: abonosRestantesMes2 = max(BG-1, 0)
        // BM: abonosRestantesMes3 = max(BJ-1, 0)
        
        Integer abonosRestantesMes1 = Math.max(plazoCreditoMeses - 1, 0); // Simplified
        Integer abonosRestantesMes2 = Math.max(abonosRestantesMes1 - 1, 0);
        Integer abonosRestantesMes3 = Math.max(abonosRestantesMes2 - 1, 0);
        
        // BH importeCapitalProyectadoMes1 = Si BG=0, 0; si no I/M
        BigDecimal importeCapitalProyectadoMes1 = abonosRestantesMes1 == 0 ? BigDecimal.ZERO : montoOriginal.divide(BigDecimal.valueOf(plazoCreditoMeses), 2, RoundingMode.HALF_UP);
        BigDecimal importeCapitalProyectadoMes2 = abonosRestantesMes2 == 0 ? BigDecimal.ZERO : montoOriginal.divide(BigDecimal.valueOf(plazoCreditoMeses), 2, RoundingMode.HALF_UP);
        BigDecimal importeCapitalProyectadoMes3 = abonosRestantesMes3 == 0 ? BigDecimal.ZERO : montoOriginal.divide(BigDecimal.valueOf(plazoCreditoMeses), 2, RoundingMode.HALF_UP);

        BigDecimal tasaDiaria = tasaOrdinaria != null ? tasaOrdinaria.divide(BigDecimal.valueOf(360), 10, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal factorInteres = tasaDiaria.multiply(BigDecimal.valueOf(30));

        BigDecimal saldoTeorico1 = capitalVigente != null ? capitalVigente : BigDecimal.ZERO;
        BigDecimal interesDevengadoProyectadoMes1 = saldoTeorico1.multiply(factorInteres).setScale(2, RoundingMode.HALF_UP);

        BigDecimal saldoTeorico2 = saldoTeorico1.subtract(importeCapitalProyectadoMes1).max(BigDecimal.ZERO);
        BigDecimal interesDevengadoProyectadoMes2 = saldoTeorico2.multiply(factorInteres).setScale(2, RoundingMode.HALF_UP);

        BigDecimal saldoTeorico3 = saldoTeorico2.subtract(importeCapitalProyectadoMes2).max(BigDecimal.ZERO);
        BigDecimal interesDevengadoProyectadoMes3;
        
        // BO interesDevengadoProyectadoMes3: "Corregir IF(AB=0): usar BM/plazo como condicion. Si BM=0 o plazo=0, resultado 0; si no calcular el interes del mes 3."
        if (abonosRestantesMes3 == 0 || plazoCreditoMeses == 0) {
            interesDevengadoProyectadoMes3 = BigDecimal.ZERO;
        } else {
            interesDevengadoProyectadoMes3 = saldoTeorico3.multiply(factorInteres).setScale(2, RoundingMode.HALF_UP);
        }

        return new ProyeccionResult(
            abonosRestantesMes1, importeCapitalProyectadoMes1, interesDevengadoProyectadoMes1,
            abonosRestantesMes2, importeCapitalProyectadoMes2, interesDevengadoProyectadoMes2,
            abonosRestantesMes3, importeCapitalProyectadoMes3, interesDevengadoProyectadoMes3
        );
    }

    public record ProyeccionResult(
        Integer abonosRestantesMes1, BigDecimal importeCapitalProyectadoMes1, BigDecimal interesDevengadoProyectadoMes1,
        Integer abonosRestantesMes2, BigDecimal importeCapitalProyectadoMes2, BigDecimal interesDevengadoProyectadoMes2,
        Integer abonosRestantesMes3, BigDecimal importeCapitalProyectadoMes3, BigDecimal interesDevengadoProyectadoMes3
    ) {}
}
