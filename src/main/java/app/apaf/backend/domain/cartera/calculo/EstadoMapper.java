package app.apaf.backend.domain.cartera.calculo;

import java.util.Map;

public class EstadoMapper {
    private static final Map<String, String> MAPA_ESTADOS = Map.ofEntries(
            Map.entry("baja california", "2"),
            Map.entry("baja california sur", "3"),
            Map.entry("coahuila de zaragoza", "5"),
            Map.entry("ciudad de mexico", "9"),
            Map.entry("guerrero", "12"),
            Map.entry("hidalgo", "13"),
            Map.entry("jalisco", "14"),
            Map.entry("mexico", "15"),
            Map.entry("morelos", "17"),
            Map.entry("nuevo leon", "19"),
            Map.entry("puebla", "21"),
            Map.entry("queretaro", "22"),
            Map.entry("sonora", "26"),
            Map.entry("tamaulipas", "28"),
            Map.entry("tlaxcala", "29"),
            Map.entry("veracruz de ignacio de la llave", "30"),
            Map.entry("yucatan", "31"),
            Map.entry("zacatecas", "32")
    );

    public static String mapearCodigo(String estadoCrudo) {
        if (estadoCrudo == null || estadoCrudo.isBlank()) {
            return null;
        }
        return MAPA_ESTADOS.get(estadoCrudo.trim().toLowerCase());
    }
}
