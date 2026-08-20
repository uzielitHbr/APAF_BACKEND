package app.apaf.backend.features.cartera_eprc.domain.repository;

import app.apaf.backend.features.cartera_eprc.domain.entity.EprcEstratificacionDetalleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EprcEstratificacionDetalleRepository extends JpaRepository<EprcEstratificacionDetalleEntity, Long> {
    List<EprcEstratificacionDetalleEntity> findByIdEjecucion(UUID idEjecucion);
}
