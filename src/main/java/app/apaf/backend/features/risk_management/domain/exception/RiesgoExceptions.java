package app.apaf.backend.features.risk_management.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class RiesgoExceptions {

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "RIESGO_PARAMETRO_INVALIDO")
    public static class ParametroInvalidoException extends RuntimeException {
        public ParametroInvalidoException(String message) {
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "RIESGO_LIMITE_INVALIDO")
    public static class LimiteInvalidoException extends RuntimeException {
        public LimiteInvalidoException(String message) {
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.CONFLICT, reason = "RIESGO_LIMITE_DUPLICADO")
    public static class LimiteDuplicadoException extends RuntimeException {
        public LimiteDuplicadoException(String message) {
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "RIESGO_LIMITE_NO_ENCONTRADO")
    public static class LimiteNoEncontradoException extends RuntimeException {
        public LimiteNoEncontradoException(String message) {
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.CONFLICT, reason = "RIESGO_LIMITE_CONFLICTO_VERSION")
    public static class ConflictoVersionException extends RuntimeException {
        public ConflictoVersionException(String message) {
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "RIESGO_DATOS_INCONSISTENTES")
    public static class DatosInconsistentesException extends RuntimeException {
        public DatosInconsistentesException(String message) {
            super(message);
        }
    }
}
