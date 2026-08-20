package app.apaf.backend.features.cartera_management.expediente.saldos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaldosPagosBaseResponse(
    Integer diasMora,
    String vigenteOVencido,
    BigDecimal tasaOrdinariaNominalAnual,
    BigDecimal tasaMoratoriaNominalAnual,
    BigDecimal capitalVigente,
    BigDecimal capitalVencido,
    BigDecimal intDevNoCobradosVigentes,
    BigDecimal intDevNoCobradosVencidos,
    BigDecimal intDevNoCobradosCtasOrden,
    String frecuenciaPagoCapital,
    String frecuenciaPagoIntereses,
    LocalDate fechaUltimoPagoCapital,
    BigDecimal montoUltimoPagoCapital,
    LocalDate fechaUltimoPagoIntereses,
    BigDecimal montoUltimoPagoIntereses
) {}
