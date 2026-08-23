package app.apaf.backend.features.cartera_management.importacionhistorica.dto;

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
import java.util.ArrayList;
import java.util.List;

public class ReporteValidacionCsv {
    private final List<ErrorValidacionCsv> errores = new ArrayList<>();
    private final List<ErrorValidacionCsv> advertencias = new ArrayList<>();
    private final int maxErrores;

    public ReporteValidacionCsv(int maxErrores) {
        this.maxErrores = maxErrores;
    }

    public void addError(int lineNumber, String field, String message) {
        if (errores.size() >= maxErrores) {
            throw new CampoCsvInvalidoException("Se alcanzó el límite máximo de errores (" + maxErrores + "). Último error: " + message);
        }
        errores.add(ErrorValidacionCsv.builder()
                .lineNumber(lineNumber)
                .field(field)
                .message(message)
                .bloqueante(true)
                .build());
    }

    public void addAdvertencia(int lineNumber, String field, String message) {
        advertencias.add(ErrorValidacionCsv.builder()
                .lineNumber(lineNumber)
                .field(field)
                .message(message)
                .bloqueante(false)
                .build());
    }

    public boolean hasErrors() {
        return !errores.isEmpty();
    }

    public List<ErrorValidacionCsv> getErrores() {
        return errores;
    }

    public List<ErrorValidacionCsv> getAdvertencias() {
        return advertencias;
    }

    public void throwIfInvalid() {
        if (hasErrors()) {
            throw new CampoCsvInvalidoException(String.format("El archivo contiene %d errores bloqueantes y %d advertencias. Primer error: Línea %d, Campo %s - %s", 
                errores.size(), advertencias.size(), errores.get(0).getLineNumber(), errores.get(0).getField(), errores.get(0).getMessage()));
        }
    }
}
