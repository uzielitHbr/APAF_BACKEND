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
 * 
 * Global exception interceptor
 * 
 * @Author Uziel Abraham
 * @version 1.0
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
        String format = String.format(
                "El valor '%s' no es válido para el parámetro '%s'. Verifique los valores permitidos.",
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
        response.put("message",
                "El cuerpo de la petición (JSON) tiene un error de sintaxis o un tipo de dato incompatible.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(app.apaf.backend.domain.cartera.exception.CarteraPeriodoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleCarteraPeriodoInvalidoException(
            app.apaf.backend.domain.cartera.exception.CarteraPeriodoInvalidoException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "CARTERA_PERIODO_INVALIDO");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(app.apaf.backend.domain.cartera.exception.CarteraPeriodoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleCarteraPeriodoNoEncontradoException(
            app.apaf.backend.domain.cartera.exception.CarteraPeriodoNoEncontradoException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "CARTERA_PERIODO_NO_ENCONTRADO");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(app.apaf.backend.domain.cartera.exception.CarteraTotalesInconsistentesException.class)
    public ResponseEntity<Map<String, Object>> handleCarteraTotalesInconsistentesException(
            app.apaf.backend.domain.cartera.exception.CarteraTotalesInconsistentesException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "CARTERA_TOTALES_INCONSISTENTES");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
    @ExceptionHandler({ NoResourceFoundException.class, NoSuchElementException.class })
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

    @ExceptionHandler({
            app.apaf.backend.features.cartera_management.importacionhistorica.exception.FormatoCsvInvalidoException.class,
            app.apaf.backend.features.cartera_management.importacionhistorica.exception.CampoCsvInvalidoException.class,
            app.apaf.backend.features.cartera_management.importacionhistorica.exception.ContratoDuplicadoEnArchivoException.class,
            app.apaf.backend.features.cartera_management.importacionhistorica.exception.PeriodoCarteraYaImportadoException.class,
            app.apaf.backend.features.cartera_management.importacionhistorica.exception.IncongruenciaFechaArchivoException.class
    })
    public ResponseEntity<Map<String, Object>> handleImportacionCarteraExceptions(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Error en Importación de Cartera");
        response.put("message", ex.getMessage());

        // Extraer detalles si el mensaje los contiene para facilitar al frontend
        // Ej: "Error en línea 15, campo emproblemado: ..."
        String msg = ex.getMessage();
        if (msg != null && msg.contains("línea") && msg.contains("campo")) {
            try {
                String lineaStr = msg.split("línea")[1].split(",")[0].trim();
                String campoStr = msg.split("campo")[1].split(":")[0].trim();
                response.put("linea", lineaStr);
                response.put("columna", campoStr);
            } catch (Exception ignored) {
                // Si falla el parseo, solo devolvemos el mensaje general
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParams(
            org.springframework.web.bind.MissingServletRequestParameterException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Parámetro faltante");
        response.put("message", "Falta el parámetro requerido: " + ex.getParameterName());
        response.put("status", 400);
        return ResponseEntity.badRequest().body(response);
    }

    // Error 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Error Interno del Servidor");
        response.put("message", "Ocurrió un error inesperado en el sistema. Contacte al administrador.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
