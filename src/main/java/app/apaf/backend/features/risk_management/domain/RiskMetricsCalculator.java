package app.apaf.backend.features.risk_management.domain;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RiskMetricsCalculator {

    public BigDecimal calcularConcentracionPorcentaje(BigDecimal carteraTotalSegmento, BigDecimal carteraTotalGlobal) {
        if (carteraTotalSegmento == null || carteraTotalGlobal == null || carteraTotalGlobal.compareTo(BigDecimal.ZERO) == 0 || carteraTotalSegmento.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return carteraTotalSegmento.divide(carteraTotalGlobal, 10, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

    public BigDecimal calcularImorSegmentoPorcentaje(BigDecimal carteraVencidaSegmento, BigDecimal carteraTotalSegmento) {
        if (carteraVencidaSegmento == null || carteraTotalSegmento == null || carteraTotalSegmento.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return carteraVencidaSegmento.divide(carteraTotalSegmento, 10, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

    public BigDecimal calcularAportacionMorosidadPorcentaje(BigDecimal carteraVencidaSegmento, BigDecimal carteraTotalGlobal) {
        if (carteraVencidaSegmento == null || carteraTotalGlobal == null || carteraTotalGlobal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return carteraVencidaSegmento.divide(carteraTotalGlobal, 10, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

    public BigDecimal calcularDesviacionPorcentaje(BigDecimal concentracionPorcentaje, BigDecimal limitePorcentaje) {
        if (concentracionPorcentaje == null || limitePorcentaje == null) return BigDecimal.ZERO;
        return concentracionPorcentaje.subtract(limitePorcentaje);
    }

    public BigDecimal calcularExcesoPorcentaje(BigDecimal desviacionPorcentaje, TipoLimite tipoLimite) {
        if (desviacionPorcentaje == null || tipoLimite == null) return BigDecimal.ZERO;
        
        if (tipoLimite == TipoLimite.MAXIMO) {
            return desviacionPorcentaje.max(BigDecimal.ZERO);
        } else {
            return desviacionPorcentaje.negate().max(BigDecimal.ZERO);
        }
    }

    public BigDecimal calcularIhh(BigDecimal concentracionPorcentaje) {
        if (concentracionPorcentaje == null) return BigDecimal.ZERO;
        BigDecimal concentracionDecimal = concentracionPorcentaje.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
        return concentracionDecimal.pow(2);
    }

    public EstadoEvaluacionLimite evaluarEstadoLimite(BigDecimal carteraTotalSegmento, BigDecimal concentracionPorcentaje, BigDecimal limitePorcentaje, TipoLimite tipoLimite) {
        if (carteraTotalSegmento == null || carteraTotalSegmento.compareTo(BigDecimal.ZERO) == 0) {
            return EstadoEvaluacionLimite.SIN_DATOS;
        }
        if (limitePorcentaje == null || tipoLimite == null) {
            return EstadoEvaluacionLimite.SIN_LIMITE;
        }
        if (tipoLimite == TipoLimite.MAXIMO) {
            if (concentracionPorcentaje.compareTo(limitePorcentaje) <= 0) {
                return EstadoEvaluacionLimite.DENTRO;
            } else {
                return EstadoEvaluacionLimite.EXCEDIDO;
            }
        } else {
            if (concentracionPorcentaje.compareTo(limitePorcentaje) >= 0) {
                return EstadoEvaluacionLimite.DENTRO;
            } else {
                return EstadoEvaluacionLimite.EXCEDIDO;
            }
        }
    }
}
