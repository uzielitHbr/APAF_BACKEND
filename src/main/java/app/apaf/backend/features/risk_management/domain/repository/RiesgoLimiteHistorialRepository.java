package app.apaf.backend.features.risk_management.domain.repository;

import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteHistorialEntity;
import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RiesgoLimiteHistorialRepository extends JpaRepository<RiesgoLimiteHistorialEntity, UUID> {
    
    @Query("SELECT h FROM RiesgoLimiteHistorialEntity h WHERE h.riesgoLimite.agrupacion = :agrupacion")
    Page<RiesgoLimiteHistorialEntity> findByAgrupacion(@Param("agrupacion") AgrupacionRiesgo agrupacion, Pageable pageable);

    @Query("SELECT h FROM RiesgoLimiteHistorialEntity h WHERE h.riesgoLimite.agrupacion = :agrupacion AND " +
           "(LOWER(h.riesgoLimite.identificacion) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(h.riesgoLimite.clave) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<RiesgoLimiteHistorialEntity> findByAgrupacionAndSearch(@Param("agrupacion") AgrupacionRiesgo agrupacion, @Param("search") String search, Pageable pageable);
}
