package app.apaf.backend.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**

 Global exception interceptor

  @Author Uziel Abraham
  @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // URL
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Parámetro Inválido");

        String parameter = ex.getName();
        String valorIncorrect = ex.getValue() != null ? ex.getValue().toString() : "null";
        String format = String.format("El valor '%s' no es válido para el parámetro '%s'. Verifique los valores permitidos.",
                valorIncorrect, parameter);

        response.put("message", format);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Validations
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Error de Validación en el Formulario");

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("messages", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // send wrong JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedJson(HttpMessageNotReadableException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "JSON Mal Formado");
        response.put("message", "El cuerpo de la petición (JSON) tiene un error de sintaxis o un tipo de dato incompatible.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Trows
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Regla de Negocio");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Error 404
    @ExceptionHandler({NoResourceFoundException.class, NoSuchElementException.class})
    public ResponseEntity<Map<String, Object>> handleNotFoundExceptions(Exception ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Recurso No Encontrado");

        if (ex instanceof NoResourceFoundException) {
            response.put("message", "Ruta no encontrada.");
        } else {
            response.put("message", ex.getMessage() != null ? ex.getMessage() : "El elemento solicitado no existe.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    // Error 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Error Interno del Servidor");
        response.put("message", "Ocurrió un error inesperado en el sistema. Contacte al administrador de APAF.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
