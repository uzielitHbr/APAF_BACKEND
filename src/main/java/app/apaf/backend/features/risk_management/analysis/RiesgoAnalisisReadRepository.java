package app.apaf.backend.features.risk_management.analysis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteEntity;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface RiesgoAnalisisReadRepository extends JpaRepository<RiesgoLimiteEntity, UUID>, RiesgoAnalisisReadCustomRepository {

    @Query(value = """
                SELECT
                    COALESCE(SUM(numero_creditos), 0) as numeroCreditos,
                    COALESCE(SUM(cartera_vigente), 0) as carteraVigente,
                    COALESCE(SUM(cartera_vencida), 0) as carteraVencida,
                    COALESCE(SUM(cartera_total), 0) as carteraTotal
                FROM view_riesgo_cartera_mensual
                WHERE mes_corte = CAST(:mesCorte AS DATE)
            """, nativeQuery = true)
    TotalesGlobalesProjection obtenerTotalesGlobales(@Param("mesCorte") LocalDate mesCorte);
}
