package app.apaf.backend.features.quarterly_analysis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AnalisisTrimestralExceptions {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class PeriodoInvalidoException extends RuntimeException {
        public PeriodoInvalidoException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class ClasificacionInvalidaException extends RuntimeException {
        public ClasificacionInvalidaException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class EjecucionNoEncontradaException extends RuntimeException {
        public EjecucionNoEncontradaException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class PeriodoSinCarteraException extends RuntimeException {
        public PeriodoSinCarteraException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ConflictoVersionException extends RuntimeException {
        public ConflictoVersionException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class InconsistenciaDatosException extends RuntimeException {
        public InconsistenciaDatosException(String message) {
            super(message);
        }
    }
}
