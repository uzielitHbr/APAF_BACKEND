package app.apaf.backend.features.follow_up_management.domain.repository;

import app.apaf.backend.features.follow_up_management.domain.entity.SeguimientoEjecucionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface SeguimientoEjecucionRepository extends JpaRepository<SeguimientoEjecucionEntity, UUID> {
    
    boolean existsByMesCorte(LocalDate mesCorte);
}
