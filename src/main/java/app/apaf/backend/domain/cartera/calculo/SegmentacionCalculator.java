package app.apaf.backend.domain.cartera.calculo;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class SegmentacionCalculator {

    public Short calcularIntervaloEdad(Short edad) {
        if (edad == null) return null;
        if (edad < 26) return 1;
        if (edad < 31) return 2;
        if (edad < 36) return 3;
        if (edad < 41) return 4;
        if (edad < 46) return 5;
        if (edad < 51) return 6;
        if (edad < 56) return 7;
        if (edad < 61) return 8;
        if (edad < 66) return 9;
        if (edad < 71) return 10;
        if (edad < 76) return 11;
        if (edad < 81) return 12;
        if (edad < 86) return 13;
        return 14;
    }

    public Short calcularPlazoRemanente(LocalDate fechaCorteAnterior, LocalDate fechaVencimiento) {
        if (fechaCorteAnterior == null || fechaVencimiento == null) return null;
        long diasRestantes = ChronoUnit.DAYS.between(fechaCorteAnterior, fechaVencimiento);
        if (diasRestantes <= 0) return 0;

        int years = (int) Math.ceil(diasRestantes / 365.0);
        if (years < 2) return 1;
        if (years == 2) return 2;
        if (years == 3) return 3;
        if (years == 4) return 4;
        if (years == 5) return 5;
        if (years == 6) return 6;
        if (years == 7) return 7;
        return 8;
    }
}
