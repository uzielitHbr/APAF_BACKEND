package app.apaf.backend.features.cartera_management.importacionhistorica.exception;

public class FormatoCsvInvalidoException extends ImportacionHistoricaException {
    public FormatoCsvInvalidoException(String message) {
        super("CSV_FORMAT_INVALID", message);
    }
}
