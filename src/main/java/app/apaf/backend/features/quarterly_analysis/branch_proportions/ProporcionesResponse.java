package app.apaf.backend.features.quarterly_analysis.branch_proportions;

import java.math.BigDecimal;
import java.util.List;

public record ProporcionesResponse(
    MetaProporcionDto meta,
    List<ProporcionDataDto> data
) {
    public record MetaProporcionDto(String fechaCorte, String tipoConsulta) {}
    public record ProporcionDataDto(String sucursalId, String nombre, BigDecimal porcentaje) {}
}
