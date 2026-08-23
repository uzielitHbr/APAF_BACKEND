package app.apaf.backend.features.cartera_management.importacionhistorica.services;

import app.apaf.backend.features.cartera_management.importacionhistorica.controller.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.commands.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.dto.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.services.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.domain.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.repository.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.config.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.events.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.exception.*;


import app.apaf.backend.features.cartera_management.importacionhistorica.exception.CampoCsvInvalidoException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Component;

@Component
public class CsvValueParser {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public LocalDate parseLocalDate(String value, String fieldName, int lineIndex) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new CampoCsvInvalidoException(String.format("Error en línea %d, campo %s: valor '%s' no es una fecha válida (dd/MM/yyyy)", lineIndex, fieldName, value));
        }
    }

    public BigDecimal parseBigDecimal(String value, String fieldName, int lineIndex) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new CampoCsvInvalidoException(String.format("Error en línea %d, campo %s: valor '%s' no es un decimal válido", lineIndex, fieldName, value));
        }
    }

    public Integer parseInteger(String value, String fieldName, int lineIndex) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            throw new CampoCsvInvalidoException(String.format("Error en línea %d, campo %s: valor '%s' no es un entero válido", lineIndex, fieldName, value));
        }
    }

    public Short parseShort(String value, String fieldName, int lineIndex) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Short.valueOf(value.trim());
        } catch (NumberFormatException e) {
            throw new CampoCsvInvalidoException(String.format("Error en línea %d, campo %s: valor '%s' no es un número corto (short) válido", lineIndex, fieldName, value));
        }
    }

    public Boolean parseBoolean(String value, String fieldName, int lineIndex) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if ("1".equals(trimmed) || "true".equalsIgnoreCase(trimmed)) {
            return true;
        }
        if ("0".equals(trimmed) || "false".equalsIgnoreCase(trimmed)) {
            return false;
        }
        throw new CampoCsvInvalidoException(String.format("Error en línea %d, campo %s: valor '%s' no es un booleano válido (0/1 o true/false)", lineIndex, fieldName, value));
    }
    public String parseEmproblemado(String value, String fieldName, int lineIndex) {
        if (value == null || value.isBlank()) {
            throw new CampoCsvInvalidoException(String.format("Error en línea %d, campo %s: el valor es obligatorio", lineIndex, fieldName));
        }
        String trimmed = value.trim();
        if ("0".equals(trimmed) || "1".equals(trimmed) || "Emproblemado".equals(trimmed)) {
            return trimmed;
        }
        throw new CampoCsvInvalidoException(String.format("Error en línea %d, campo %s: valor '%s' no es válido. Debe ser '0', '1' o 'Emproblemado'", lineIndex, fieldName, value));
    }
}
