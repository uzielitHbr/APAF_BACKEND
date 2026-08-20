package app.apaf.backend.domain.cartera.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CarteraNoEncontradaException extends RuntimeException {
    public CarteraNoEncontradaException(String message) {
        super(message);
    }
}
