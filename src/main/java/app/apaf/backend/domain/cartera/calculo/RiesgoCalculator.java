package app.apaf.backend.domain.cartera.calculo;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class RiesgoCalculator {

    public Short calcularCartRiesgoTraspasoAVencida(String vigenteOVencido, Integer diasMora) {
        if (!"Vigente".equalsIgnoreCase(vigenteOVencido) || diasMora == null) {
            return null;
        }
        if (diasMora == 0) return 0;
        if (diasMora <= 29) return 1;
        if (diasMora <= 60) return 2;
        if (diasMora <= 89) return 3;
        return null;
    }

    public Short calcularDiasPorVencer(LocalDate fechaCorteAnterior, LocalDate fechaVencimiento) {
        if (fechaCorteAnterior == null || fechaVencimiento == null) return null;
        long diasRestantes = ChronoUnit.DAYS.between(fechaCorteAnterior, fechaVencimiento);
        if (diasRestantes <= 30) return 1;
        if (diasRestantes <= 61) return 2;
        return 3;
    }
}
