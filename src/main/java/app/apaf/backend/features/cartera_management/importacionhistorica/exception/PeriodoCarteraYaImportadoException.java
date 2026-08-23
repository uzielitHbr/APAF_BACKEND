package app.apaf.backend.features.cartera_management.importacionhistorica.exception;

public class PeriodoCarteraYaImportadoException extends ImportacionHistoricaException {
    public PeriodoCarteraYaImportadoException(String message) {
        super("CARTERA_PERIOD_ALREADY_EXISTS", message);
    }
}
