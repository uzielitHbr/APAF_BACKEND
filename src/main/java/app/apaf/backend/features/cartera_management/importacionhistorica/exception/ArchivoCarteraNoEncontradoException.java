package app.apaf.backend.features.cartera_management.importacionhistorica.exception;

public class ArchivoCarteraNoEncontradoException extends ImportacionHistoricaException {
    public ArchivoCarteraNoEncontradoException(String message) {
        super("CSV_FILE_NOT_FOUND", message);
    }
}
