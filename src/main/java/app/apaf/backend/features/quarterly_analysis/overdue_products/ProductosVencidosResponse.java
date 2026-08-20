package app.apaf.backend.features.quarterly_analysis.overdue_products;

import java.math.BigDecimal;
import java.util.List;

public record ProductosVencidosResponse(
    String fechaCorte,
    List<ProductoVencidoDto> data,
    TotalProductosDto resumenTotal
) {
    public record ProductoVencidoDto(String productoId, String producto, Long numeroCreditos, BigDecimal importeTotal) {}
    public record TotalProductosDto(Long numeroCreditos, BigDecimal importeTotal) {}
}
