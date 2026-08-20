package app.apaf.backend.features.risk_management.domain.repository;

import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RiesgoLimiteRepository extends JpaRepository<RiesgoLimiteEntity, UUID> {
    Optional<RiesgoLimiteEntity> findByAgrupacionAndClave(AgrupacionRiesgo agrupacion, String clave);
}
