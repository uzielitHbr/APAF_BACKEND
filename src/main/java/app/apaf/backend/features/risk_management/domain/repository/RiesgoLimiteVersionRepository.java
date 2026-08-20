package app.apaf.backend.features.risk_management.domain.repository;

import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteEntity;
import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteVersionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RiesgoLimiteVersionRepository extends JpaRepository<RiesgoLimiteVersionEntity, UUID> {
    
    Optional<RiesgoLimiteVersionEntity> findByRiesgoLimiteAndVigenteHastaIsNull(RiesgoLimiteEntity riesgoLimite);

    @Query(value = "SELECT v FROM RiesgoLimiteVersionEntity v JOIN FETCH v.riesgoLimite l WHERE l.agrupacion = :agrupacion AND v.vigenteHasta IS NULL AND (LOWER(l.identificacion) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(l.clave) LIKE LOWER(CONCAT('%', :search, '%')))", countQuery = "SELECT count(v) FROM RiesgoLimiteVersionEntity v JOIN v.riesgoLimite l WHERE l.agrupacion = :agrupacion AND v.vigenteHasta IS NULL AND (LOWER(l.identificacion) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(l.clave) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<RiesgoLimiteVersionEntity> findActiveByAgrupacionAndSearch(@Param("agrupacion") app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo agrupacion, @Param("search") String search, Pageable pageable);

    @Query(value = "SELECT v FROM RiesgoLimiteVersionEntity v JOIN FETCH v.riesgoLimite l WHERE l.agrupacion = :agrupacion AND v.vigenteHasta IS NULL", countQuery = "SELECT count(v) FROM RiesgoLimiteVersionEntity v JOIN v.riesgoLimite l WHERE l.agrupacion = :agrupacion AND v.vigenteHasta IS NULL")
    Page<RiesgoLimiteVersionEntity> findActiveByAgrupacion(@Param("agrupacion") app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo agrupacion, Pageable pageable);

    @Query(value = "SELECT v FROM RiesgoLimiteVersionEntity v JOIN FETCH v.riesgoLimite l WHERE v.vigenteHasta IS NULL AND (LOWER(l.identificacion) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(l.clave) LIKE LOWER(CONCAT('%', :search, '%')))", countQuery = "SELECT count(v) FROM RiesgoLimiteVersionEntity v JOIN v.riesgoLimite l WHERE v.vigenteHasta IS NULL AND (LOWER(l.identificacion) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(l.clave) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<RiesgoLimiteVersionEntity> findActiveBySearch(@Param("search") String search, Pageable pageable);

    @Query(value = "SELECT v FROM RiesgoLimiteVersionEntity v JOIN FETCH v.riesgoLimite l WHERE v.vigenteHasta IS NULL", countQuery = "SELECT count(v) FROM RiesgoLimiteVersionEntity v JOIN v.riesgoLimite l WHERE v.vigenteHasta IS NULL")
    Page<RiesgoLimiteVersionEntity> findActiveAll(Pageable pageable);

    @Query(value = "SELECT v FROM RiesgoLimiteVersionEntity v JOIN FETCH v.riesgoLimite l WHERE l.agrupacion = :agrupacion AND (LOWER(l.identificacion) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(l.clave) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(v.actor) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(CAST(v.accion AS string)) LIKE LOWER(CONCAT('%', :search, '%')))", countQuery = "SELECT count(v) FROM RiesgoLimiteVersionEntity v JOIN v.riesgoLimite l WHERE l.agrupacion = :agrupacion AND (LOWER(l.identificacion) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(l.clave) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(v.actor) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(CAST(v.accion AS string)) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<RiesgoLimiteVersionEntity> findHistoryByAgrupacionAndSearch(@Param("agrupacion") app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo agrupacion, @Param("search") String search, Pageable pageable);

    @Query(value = "SELECT v FROM RiesgoLimiteVersionEntity v JOIN FETCH v.riesgoLimite l WHERE l.agrupacion = :agrupacion", countQuery = "SELECT count(v) FROM RiesgoLimiteVersionEntity v JOIN v.riesgoLimite l WHERE l.agrupacion = :agrupacion")
    Page<RiesgoLimiteVersionEntity> findHistoryByAgrupacion(@Param("agrupacion") app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo agrupacion, Pageable pageable);
}
