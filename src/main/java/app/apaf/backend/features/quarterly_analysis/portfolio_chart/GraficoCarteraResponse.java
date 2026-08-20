package app.apaf.backend.features.quarterly_analysis.portfolio_chart;

import java.math.BigDecimal;
import java.util.List;

public record GraficoCarteraResponse(
    String fechaCorte,
    List<RangoGraficoDto> data,
    BigDecimal totalMonto
) {
    public record RangoGraficoDto(String rangoId, String rango, BigDecimal monto) {}
}
