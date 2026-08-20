package app.apaf.backend.features.cartera_eprc.application.stratification;

import java.util.List;

public record SegmentoDetalleDto(
    List<DetalleIntervaloEprcDto> detalle,
    TotalesEprcDto totales
) {}
