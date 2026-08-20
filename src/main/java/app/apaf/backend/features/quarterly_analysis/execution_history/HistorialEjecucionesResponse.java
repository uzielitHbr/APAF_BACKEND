package app.apaf.backend.features.quarterly_analysis.execution_history;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record HistorialEjecucionesResponse(
    Meta meta,
    List<HistorialEjecucionItem> data
) {
    public record Meta(
        String mesCorte,
        int page,
        int size,
        long totalElements,
        int totalPages,
        String tipoConsulta
    ) {}

    public record HistorialEjecucionItem(
        UUID idEjecucion,
        String mesCorte,
        Integer numeroVersion,
        String estado,
        String actor,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin
    ) {}
}
