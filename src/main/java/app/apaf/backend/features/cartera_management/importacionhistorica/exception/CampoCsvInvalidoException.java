package app.apaf.backend.features.cartera_management.importacionhistorica.exception;

public class CampoCsvInvalidoException extends ImportacionHistoricaException {
    public CampoCsvInvalidoException(String message) {
        super("CSV_FIELD_INVALID", message);
    }
}
