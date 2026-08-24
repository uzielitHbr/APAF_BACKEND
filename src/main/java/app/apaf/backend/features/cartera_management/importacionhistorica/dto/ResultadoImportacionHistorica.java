package app.apaf.backend.features.cartera_management.importacionhistorica.dto;

import java.time.YearMonth;
import lombok.Builder;

@Builder
public record ResultadoImportacionHistorica(
                YearMonth periodo,
                boolean exitoso,
                int totalFilas,
                int filasInsertadas,
                String mensaje) {
}
