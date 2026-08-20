package app.apaf.backend.features.cartera_management.importacionhistorica;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorValidacionCsv {
    private final int lineNumber;
    private final String field;
    private final String message;
    private final boolean bloqueante;
}
