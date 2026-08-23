package app.apaf.backend.features.cartera_management.importacionhistorica.exception;

import app.apaf.backend.features.cartera_management.importacionhistorica.controller.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.commands.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.dto.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.services.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.domain.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.repository.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.config.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.events.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.exception.*;


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
