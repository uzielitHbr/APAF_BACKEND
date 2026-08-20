package app.apaf.backend.features.quarterly_analysis.branch_comparison;

import java.math.BigDecimal;
import java.util.List;

public record AnalisisSucursalesResponse(
    MetaDto meta,
    KpisDto kpis,
    List<SucursalDataDto> data,
    ResumenTotalDto resumenTotal
) {
    public record MetaDto(String fechaCorte, String tipoConsulta, String sucursalNombre) {}
    public record KpisDto(BigDecimal imorPorcentaje) {}
    public record CarteraDetalleDto(Integer numeroCreditos, BigDecimal saldo) {}
    public record CarteraTotalDto(Integer numeroCreditos, BigDecimal saldo, BigDecimal proporcionPorcentaje) {}
    public record SucursalDataDto(String tipoCartera, CarteraDetalleDto vigente, CarteraDetalleDto vencida, CarteraTotalDto total) {}
    public record ResumenTotalDto(CarteraDetalleDto vigente, CarteraDetalleDto vencida, CarteraTotalDto total) {}
}
