package app.apaf.backend.features.quarterly_analysis.domain.repository;

import app.apaf.backend.features.quarterly_analysis.domain.model.AnalisisTrimestralProductoResultado;
import app.apaf.backend.features.quarterly_analysis.domain.model.ProductoResultadoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnalisisTrimestralProductoResultadoRepository extends JpaRepository<AnalisisTrimestralProductoResultado, ProductoResultadoId> {
    List<AnalisisTrimestralProductoResultado> findByIdEjecucion(UUID idEjecucion);
}
