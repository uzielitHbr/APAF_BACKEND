package app.apaf.backend.features.cartera_management.totales;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CarteraTotalesResponse(
    LocalDate fechaCorte,
    ResumenGeneralResponse resumenGeneral,
    SaldosDevengadosResponse saldosDevengados,
    FlujosRecuperacionResponse flujosRecuperacion,
    RiesgoYRegulatorioResponse riesgoYRegulatorio
) {}
