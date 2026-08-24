package app.apaf.backend.features.risk_management.analysis;

import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface RiesgoAnalisisReadCustomRepository {
    Page<RiesgoSegmentoProjection> obtenerAnalisisPorAgrupacion(AgrupacionRiesgo agrupacion, LocalDate mesCorte, Pageable pageable);
}
