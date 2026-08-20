package app.apaf.backend.features.cartera_management.importacionhistorica.exception;

public class ContratoDuplicadoEnArchivoException extends ImportacionHistoricaException {
    public ContratoDuplicadoEnArchivoException(String message) {
        super("CSV_DUPLICATE_CONTRACT", message);
    }
}
