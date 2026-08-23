package app.apaf.backend.features.cartera_management.importacionhistorica.repository;

import app.apaf.backend.features.cartera_management.importacionhistorica.controller.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.commands.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.dto.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.services.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.domain.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.repository.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.config.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.events.*;
import app.apaf.backend.features.cartera_management.importacionhistorica.exception.*;


import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarteraImportacionHistoricaRepository extends JpaRepository<CarteraImportacionHistorica, UUID> {
    Optional<CarteraImportacionHistorica> findByMesCorteAndEstado(LocalDate mesCorte, String estado);
    Optional<CarteraImportacionHistorica> findByHashSha256AndEstado(String hashSha256, String estado);
}
