package app.apaf.backend.features.follow_up_management.application.plazo;

import java.math.BigDecimal;
import java.util.List;

public record PlazoRemanenteResponse(
    String fechaCorte, 
    String tipo, 
    List<PlazoDetalleDto> detalle, 
    TotalesPlazoDto totales
) {
    public record PlazoDetalleDto(
        String plazoRemanente,
        String sucursal,
        Integer numeroCreditos,
        BigDecimal capitalVigente,
        BigDecimal interesOrdVigente,
        BigDecimal capitalVencido,
        BigDecimal interesOrdVencido,
        BigDecimal cuentasOrden,
        BigDecimal saldoTotal,
        Integer creditosConMovimiento,
        Integer creditosSinMovimiento,
        Integer creditosOtorgadosMes,
        BigDecimal imor,
        BigDecimal proporcion
    ) {}

    public record TotalesPlazoDto(
        Integer numeroCreditos,
        BigDecimal capitalVigente,
        BigDecimal interesOrdVigente,
        BigDecimal capitalVencido,
        BigDecimal interesOrdVencido,
        BigDecimal cuentasOrden,
        BigDecimal saldoTotal,
        Integer creditosConMovimiento,
        Integer creditosSinMovimiento,
        Integer creditosOtorgadosMes,
        BigDecimal imor,
        BigDecimal proporcion
    ) {}
}
