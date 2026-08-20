package app.apaf.backend.features.follow_up_management.application.morosidad;

import java.math.BigDecimal;
import java.util.List;

public record RiesgosMorosidadResponse(
    String fechaCorte, 
    DataMorosidad data
) {
    public record DataMorosidad(
        BloqueMoraDto mora61a89, 
        BloqueMoraDto mora30a60, 
        BloqueMoraDto mora1a29
    ) {}
    
    public record BloqueMoraDto(
        List<DetalleMoraDto> detalle, 
        TotalesMoraDto totales
    ) {}

    public record DetalleMoraDto(
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
        Integer creditosOtorgadosMes
    ) {}

    public record TotalesMoraDto(
        Integer numeroCreditos,
        BigDecimal capitalVigente,
        BigDecimal interesOrdVigente,
        BigDecimal capitalVencido,
        BigDecimal interesOrdVencido,
        BigDecimal cuentasOrden,
        BigDecimal saldoTotal,
        Integer creditosConMovimiento,
        Integer creditosSinMovimiento,
        Integer creditosOtorgadosMes
    ) {}
}
