package app.apaf.backend.features.quarterly_analysis.domain.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AnalisisTrimestralCalculator {

    public BigDecimal calcularCarteraVigente(BigDecimal capitalVigente, BigDecimal interesesVigentes) {
        BigDecimal cap = capitalVigente != null ? capitalVigente : BigDecimal.ZERO;
        BigDecimal intVig = interesesVigentes != null ? interesesVigentes : BigDecimal.ZERO;
        return cap.add(intVig);
    }

    public BigDecimal calcularCarteraVencida(BigDecimal capitalVencido, BigDecimal interesesVencidos) {
        BigDecimal capV = capitalVencido != null ? capitalVencido : BigDecimal.ZERO;
        BigDecimal intV = interesesVencidos != null ? interesesVencidos : BigDecimal.ZERO;
        return capV.add(intV);
    }

    public BigDecimal calcularImor(BigDecimal carteraVencida, BigDecimal carteraTotal) {
        if (carteraTotal == null || carteraTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal cv = carteraVencida != null ? carteraVencida : BigDecimal.ZERO;
        return cv.divide(carteraTotal, 8, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }



    public BigDecimal calcularProporcion(BigDecimal parte, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal p = parte != null ? parte : BigDecimal.ZERO;
        return p.divide(total, 8, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

    public BigDecimal redondearParaPersistencia(BigDecimal valor) {
        if (valor == null) {
            return null;
        }
        return valor.setScale(4, RoundingMode.HALF_UP);
    }
}
