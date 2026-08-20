package app.apaf.backend.features.cartera_eprc.domain.repository;

import app.apaf.backend.features.cartera_eprc.domain.entity.EprcResumenGlobalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EprcResumenGlobalRepository extends JpaRepository<EprcResumenGlobalEntity, UUID> {
    Optional<EprcResumenGlobalEntity> findByIdEjecucion(UUID idEjecucion);
}
