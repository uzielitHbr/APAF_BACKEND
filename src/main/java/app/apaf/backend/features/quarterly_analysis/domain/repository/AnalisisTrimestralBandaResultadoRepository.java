package app.apaf.backend.features.quarterly_analysis.domain.repository;

import app.apaf.backend.features.quarterly_analysis.domain.model.AnalisisTrimestralBandaResultado;
import app.apaf.backend.features.quarterly_analysis.domain.model.BandaResultadoId;
import app.apaf.backend.features.quarterly_analysis.domain.enumtype.ClasificacionAnalisis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnalisisTrimestralBandaResultadoRepository extends JpaRepository<AnalisisTrimestralBandaResultado, BandaResultadoId> {
    List<AnalisisTrimestralBandaResultado> findByIdEjecucionAndClasificacion(UUID idEjecucion, ClasificacionAnalisis clasificacion);
    List<AnalisisTrimestralBandaResultado> findByIdEjecucion(UUID idEjecucion);
}
