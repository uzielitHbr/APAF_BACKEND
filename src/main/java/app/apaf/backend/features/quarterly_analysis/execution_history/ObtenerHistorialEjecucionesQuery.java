package app.apaf.backend.features.quarterly_analysis.execution_history;

public record ObtenerHistorialEjecucionesQuery(
    String mesCorte,
    int page,
    int size
) {}
