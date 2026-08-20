package app.apaf.backend.features.cartera_eprc.application.stratification;

public record SegmentosEprcDto(
    SegmentoDetalleDto consumo,
    SegmentoDetalleDto comercial,
    SegmentoDetalleDto vivienda
) {}
