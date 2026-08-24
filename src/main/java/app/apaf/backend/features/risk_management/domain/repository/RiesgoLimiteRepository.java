package app.apaf.backend.features.risk_management.domain.repository;

import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteEntity;
import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RiesgoLimiteRepository extends JpaRepository<RiesgoLimiteEntity, UUID> {
    Optional<RiesgoLimiteEntity> findByAgrupacionAndClave(AgrupacionRiesgo agrupacion, String clave);
    
    @Query("SELECT r FROM RiesgoLimiteEntity r WHERE r.agrupacion = :agrupacion")
    Page<RiesgoLimiteEntity> findByAgrupacion(@Param("agrupacion") AgrupacionRiesgo agrupacion, Pageable pageable);
    
    @Query("SELECT r FROM RiesgoLimiteEntity r WHERE r.agrupacion = :agrupacion AND " +
           "(LOWER(r.identificacion) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.clave) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<RiesgoLimiteEntity> findByAgrupacionAndSearch(@Param("agrupacion") AgrupacionRiesgo agrupacion, @Param("search") String search, Pageable pageable);
    
    @Query("SELECT r FROM RiesgoLimiteEntity r WHERE " +
           "(LOWER(r.identificacion) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.clave) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<RiesgoLimiteEntity> findBySearch(@Param("search") String search, Pageable pageable);
}
