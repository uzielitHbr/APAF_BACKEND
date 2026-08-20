package app.apaf.backend.features.cartera_management.importacionhistorica.exception;

public abstract class ImportacionHistoricaException extends RuntimeException {
    private final String codigo;

    protected ImportacionHistoricaException(String codigo, String message) {
        super(message);
        this.codigo = codigo;
    }

    protected ImportacionHistoricaException(String codigo, String message, Throwable cause) {
        super(message, cause);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
