package app.apaf.backend.features.quarterly_analysis.domain.repository;

import app.apaf.backend.features.quarterly_analysis.domain.model.AnalisisTrimestralSucursalDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalisisTrimestralSucursalDetalleRepository extends JpaRepository<AnalisisTrimestralSucursalDetalle, Long> {
}
