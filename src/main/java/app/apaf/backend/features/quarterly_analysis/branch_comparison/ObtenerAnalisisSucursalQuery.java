package app.apaf.backend.features.quarterly_analysis.branch_comparison;

public record ObtenerAnalisisSucursalQuery(
    String fechaCorte,
    String sucursal
) {}
