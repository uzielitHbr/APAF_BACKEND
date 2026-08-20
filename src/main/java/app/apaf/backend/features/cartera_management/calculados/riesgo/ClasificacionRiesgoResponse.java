package app.apaf.backend.features.cartera_management.calculados.riesgo;

public record ClasificacionRiesgoResponse(
        Short diasPorVencer,
        Short intervaloEdad,
        Short cartRiesgoTraspasoAVencida,
        String nivelDeRiesgoSic,
        String nivelDeRiesgoSicVencida,
        String nivelDeRiesgoSicGestionada,
        Short plazoRemanente) {
}
