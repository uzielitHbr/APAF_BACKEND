package app.apaf.backend.features.cartera_management.importacionhistorica;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.YearMonth;

public record ImportarCarteraHistoricaCommand(
        YearMonth periodo,
        Path archivo,
        Charset charset,
        int batchSize) {
}
