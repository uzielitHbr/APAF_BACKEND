package app.apaf.backend.features.quarterly_analysis.domain.repository;

import app.apaf.backend.features.quarterly_analysis.domain.model.AnalisisTrimestralEjecucion;
import app.apaf.backend.features.quarterly_analysis.domain.enumtype.EstadoEjecucionTrimestral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalisisTrimestralEjecucionRepository extends JpaRepository<AnalisisTrimestralEjecucion, UUID> {
    Optional<AnalisisTrimestralEjecucion> findByMesCorteAndEstado(LocalDate mesCorte, EstadoEjecucionTrimestral estado);
}
