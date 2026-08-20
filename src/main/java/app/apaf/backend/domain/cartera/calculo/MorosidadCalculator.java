package app.apaf.backend.domain.cartera.calculo;

import org.springframework.stereotype.Component;

@Component
public class MorosidadCalculator {

    public Short calcularIntervalo(String tipoCarteraCalificacion, Integer diasMora) {
        if (tipoCarteraCalificacion == null || diasMora == null) {
            return null;
        }
        
        String tipo = tipoCarteraCalificacion.toLowerCase();
        
        if (tipo.contains("consumo")) {
            if (diasMora == 0) return 1;
            if (diasMora <= 7) return 2;
            if (diasMora <= 30) return 3;
            if (diasMora <= 60) return 4;
            if (diasMora <= 90) return 5;
            if (diasMora <= 120) return 6;
            if (diasMora <= 180) return 7;
            return 8;
        } else if (tipo.contains("comercial")) {
            if (diasMora == 0) return 1;
            if (diasMora <= 30) return 2;
            if (diasMora <= 60) return 3;
            if (diasMora <= 90) return 4;
            if (diasMora <= 120) return 5;
            if (diasMora <= 150) return 6;
            if (diasMora <= 180) return 7;
            if (diasMora <= 210) return 8;
            if (diasMora <= 240) return 9;
            return 10;
        } else if (tipo.contains("vivienda")) {
            if (diasMora == 0) return 1;
            if (diasMora <= 30) return 2;
            if (diasMora <= 60) return 3;
            if (diasMora <= 90) return 4;
            if (diasMora <= 120) return 5;
            if (diasMora <= 150) return 6;
            if (diasMora <= 180) return 7;
            if (diasMora <= 1460) return 8;
            return 9;
        }
        
        return null;
    }
}
