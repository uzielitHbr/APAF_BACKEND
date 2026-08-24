package app.apaf.backend.features.cartera_management.importacionhistorica.repository;

import app.apaf.backend.features.cartera_management.importacionhistorica.domain.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarteraImportacionHistoricaRepository extends JpaRepository<CarteraImportacionHistorica, UUID> {
    Optional<CarteraImportacionHistorica> findByMesCorteAndEstado(LocalDate mesCorte, String estado);

    Optional<CarteraImportacionHistorica> findByHashSha256AndEstado(String hashSha256, String estado);
}
