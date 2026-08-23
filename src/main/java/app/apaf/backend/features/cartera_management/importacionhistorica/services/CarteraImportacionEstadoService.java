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


import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarteraImportacionEstadoService {

    private final CarteraImportacionHistoricaRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void marcarComoFallida(UUID idImportacion, String mensajeError) {
        repository.findById(idImportacion).ifPresent(auditoria -> {
            auditoria.setEstado("FALLIDA");
            auditoria.setMensajeError(mensajeError);
            repository.save(auditoria);
        });
    }
}
