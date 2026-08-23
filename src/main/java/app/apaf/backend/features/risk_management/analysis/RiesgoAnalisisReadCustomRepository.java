package app.apaf.backend.features.risk_management.analysis;

import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import app.apaf.backend.features.risk_management.options_limit.OpcionLimiteDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface RiesgoAnalisisReadCustomRepository {
    Page<RiesgoSegmentoProjection> obtenerAnalisisPorAgrupacion(AgrupacionRiesgo agrupacion, LocalDate mesCorte, Pageable pageable);
    List<OpcionLimiteDto> obtenerOpcionesDisponiblesPorAgrupacion(AgrupacionRiesgo agrupacion);
}
