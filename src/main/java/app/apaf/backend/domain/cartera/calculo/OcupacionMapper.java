package app.apaf.backend.domain.cartera.calculo;

import java.util.Map;

public class OcupacionMapper {
    private static final Map<String, String> MAPA_OCUPACIONES = Map.ofEntries(
            Map.entry("agricultor de maiz", "Agricultor"),
            Map.entry("arrendador de inmuebles", "Arrendador"),
            Map.entry("artesano", "Oficio"),
            Map.entry("asesor de ventas", "Empleado"),
            Map.entry("agricultor de chile", "Agricultor"),
            Map.entry("arriero", "Oficio"),
            Map.entry("aserrador", "Oficio"),
            Map.entry("asesor educativo", "Empleado"),
            Map.entry("asesor tecnico", "Empleado"),
            Map.entry("ayudante de albañil", "Albañil"),
            Map.entry("ayudante de cocina", "Cocinero"),
            Map.entry("ayudante de mecanico", "Mecanico"),
            Map.entry("curandero", "Oficio"),
            Map.entry("decorador", "Profesionista"),
            Map.entry("electricista", "Oficio"),
            Map.entry("fisioterapeuta", "Profesionista"),
            Map.entry("fotografo", "Oficio"),
            Map.entry("hotelero", "Comerciante"),
            Map.entry("intendente", "Empleado"),
            Map.entry("laboratorista", "Empleado"),
            Map.entry("medico especialista", "Medico"),
            Map.entry("ministro de culto", "Oficio"),
            Map.entry("musico", "Oficio"),
            Map.entry("obrero", "Otros"),
            Map.entry("operador de maquinaria", "Otros"),
            Map.entry("peluquero", "Oficio"),
            Map.entry("plomero", "Oficio"),
            Map.entry("promotor", "Empleado"),
            Map.entry("secretario", "Empleado"),
            Map.entry("soldador", "Oficio"),
            Map.entry("talachero", "Oficio"),
            Map.entry("tesorero municipal", "Empleado"),
            Map.entry("veterinario", "Medico"),
            Map.entry("zapatero", "Oficio"),
            Map.entry("auxiliar de enfermeria", "Enfermero"),
            Map.entry("ayudante de carpinteria", "Carpintero"),
            Map.entry("ayudante de herreria", "Herrero"),
            Map.entry("cafeticultor", "Agricultor"),
            Map.entry("contador publico", "Contador"),
            Map.entry("dentista", "Profesionista"),
            Map.entry("ferretero", "Comerciante"),
            Map.entry("forrajero", "Comerciante"),
            Map.entry("hojalatero", "Oficio"),
            Map.entry("huesero", "Oficio"),
            Map.entry("jornalero", "Oficio"),
            Map.entry("medico cirujano", "Medico"),
            Map.entry("medico veterinario", "Medico"),
            Map.entry("modista", "Oficio"),
            Map.entry("niñero", "Empleado"),
            Map.entry("operador", "Otros"),
            Map.entry("ovinocultor", "Ganadero"),
            Map.entry("pintor", "Oficio"),
            Map.entry("programador", "Empleado"),
            Map.entry("sastre", "Oficio"),
            Map.entry("servidor publico", "Empleado"),
            Map.entry("supervisor educativo", "Profesor"),
            Map.entry("techador", "Oficio"),
            Map.entry("vaquero", "Oficio"),
            Map.entry("vigilante", "Empleado"),
            Map.entry("administrador", "Profesionista")
    );

    public static String agrupar(String ocupacionCruda) {
        if (ocupacionCruda == null || ocupacionCruda.isBlank()) {
            return ocupacionCruda;
        }
        String crudaLimpia = ocupacionCruda.trim();
        return MAPA_OCUPACIONES.getOrDefault(crudaLimpia.toLowerCase(), crudaLimpia);
    }
}
