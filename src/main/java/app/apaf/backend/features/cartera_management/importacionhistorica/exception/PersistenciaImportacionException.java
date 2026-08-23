package app.apaf.backend.features.cartera_management.importacionhistorica.exception;

public class PersistenciaImportacionException extends ImportacionHistoricaException {
    public PersistenciaImportacionException(String message, Throwable cause) {
        super("CARTERA_IMPORT_PERSISTENCE_ERROR", message, cause);
    }
}
