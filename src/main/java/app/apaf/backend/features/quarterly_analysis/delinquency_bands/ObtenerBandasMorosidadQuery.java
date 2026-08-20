package app.apaf.backend.features.quarterly_analysis.delinquency_bands;

public record ObtenerBandasMorosidadQuery(
    String fechaCorte,
    String clasificacion
) {}
