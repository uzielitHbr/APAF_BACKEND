package app.apaf.backend.features.cartera_management.importacionhistorica.exception;

public class CalculoCarteraImportacionException extends ImportacionHistoricaException {
    public CalculoCarteraImportacionException(String message, Throwable cause) {
        super("CARTERA_CALCULATION_ERROR", message, cause);
    }
}
