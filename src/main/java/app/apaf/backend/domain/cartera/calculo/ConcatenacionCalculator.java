package app.apaf.backend.domain.cartera.calculo;

import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ConcatenacionCalculator {

    public String concatenar(String... partes) {
        String result = Stream.of(partes)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.equalsIgnoreCase("null") && !s.equalsIgnoreCase("false"))
                .collect(Collectors.joining(""));
        return result.isEmpty() ? null : result;
    }

    public Short obtenerCarteraTipo(String tipoCarteraCalificacion) {
        if (tipoCarteraCalificacion == null) return null;
        String tipo = tipoCarteraCalificacion.toLowerCase();
        if (tipo.contains("consumo")) return 0;
        if (tipo.contains("comercial")) return 1;
        if (tipo.contains("vivienda")) return 2;
        return null;
    }

    public String obtenerNumeroProducto(String numeroContrato) {
        if (numeroContrato == null) return null;
        String left9 = numeroContrato.length() >= 9 ? numeroContrato.substring(0, 9) : numeroContrato;
        return left9.length() >= 4 ? left9.substring(left9.length() - 4) : left9;
    }
}
