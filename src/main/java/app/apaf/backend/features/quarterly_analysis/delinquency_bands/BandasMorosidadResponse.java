package app.apaf.backend.features.quarterly_analysis.delinquency_bands;

import java.math.BigDecimal;
import java.util.List;

public record BandasMorosidadResponse(
    String fechaCorte,
    String clasificacion,
    List<CategoriaBandaDto> categorias,
    TotalBandaDto resumenTotal
) {
    public record CategoriaBandaDto(String tipo, List<DetalleBandaDto> detalle, TotalBandaDto total) {}
    public record DetalleBandaDto(String rangoId, String rangoVencimiento, Long numeroCreditos, BigDecimal importeTotal) {}
    public record TotalBandaDto(Long numeroCreditos, BigDecimal importeTotal) {}
}
