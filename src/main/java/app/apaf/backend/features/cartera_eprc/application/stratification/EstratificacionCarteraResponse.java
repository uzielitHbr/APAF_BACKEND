package app.apaf.backend.features.cartera_eprc.application.stratification;

public record EstratificacionCarteraResponse(
    String fechaCorte,
    SegmentosEprcDto segmentos,
    TotalesEprcDto sumaTotalGlobal,
    IndicadoresGlobalesDto indicadoresGlobales
) {}
