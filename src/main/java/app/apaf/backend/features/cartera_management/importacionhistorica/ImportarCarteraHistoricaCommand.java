package app.apaf.backend.features.cartera_management.importacionhistorica;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.YearMonth;

public record ImportarCarteraHistoricaCommand(
                YearMonth mesCorte,
                Path archivo,
                String nombreArchivo,
                Charset charset,
                int batchSize,
                String idUsuarioCreacion) {
}
