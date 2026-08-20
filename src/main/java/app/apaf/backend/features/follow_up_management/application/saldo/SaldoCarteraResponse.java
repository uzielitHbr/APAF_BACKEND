package app.apaf.backend.features.follow_up_management.application.saldo;

import java.math.BigDecimal;
import java.util.List;

public record SaldoCarteraResponse(
    String fechaCorte, 
    DataSaldo data
) {
    public record DataSaldo(
        List<SucursalSaldoDto> sucursales, 
        TotalesSaldoDto totales
    ) {}

    public record SucursalSaldoDto(
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
        BigDecimal imorSucursal,
        BigDecimal imorProyectado,
        BigDecimal proporcionCartera,
        BigDecimal imorGeneral
    ) {}

    public record TotalesSaldoDto(
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
        BigDecimal imorSucursal,
        BigDecimal imorProyectado,
        BigDecimal proporcionCartera,
        BigDecimal imorGeneral
    ) {}
}
