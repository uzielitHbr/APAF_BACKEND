package app.apaf.backend.features.follow_up_management.domain.repository;

import app.apaf.backend.features.follow_up_management.domain.entity.SeguimientoPlazoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeguimientoPlazoRepository extends JpaRepository<SeguimientoPlazoEntity, UUID> {
}
