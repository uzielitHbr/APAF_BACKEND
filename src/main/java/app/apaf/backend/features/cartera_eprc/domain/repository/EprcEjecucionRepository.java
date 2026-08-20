package app.apaf.backend.features.cartera_eprc.domain.repository;

import app.apaf.backend.features.cartera_eprc.domain.entity.EprcEjecucionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EprcEjecucionRepository extends JpaRepository<EprcEjecucionEntity, UUID> {
    Optional<EprcEjecucionEntity> findByMesCorteAndEstado(LocalDate mesCorte, String estado);
}
