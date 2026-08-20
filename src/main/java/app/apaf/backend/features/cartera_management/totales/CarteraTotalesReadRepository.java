package app.apaf.backend.features.cartera_management.totales;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import app.apaf.backend.domain.cartera.entity.CarteraDatos;

public interface CarteraTotalesReadRepository extends Repository<CarteraDatos, UUID> {

    @Query("""
        SELECT 
            MIN(cd.fechaCorte) AS fechaCorteMinima,
            MAX(cd.fechaCorte) AS fechaCorteMaxima,
            COUNT(cd.idAnalisisMensual) AS totalBase,
            COUNT(cdc.idAnalisisMensual) AS totalCalculados,
            COALESCE(SUM(cdc.carteraTotal), 0) AS totalCartera,
            COALESCE(SUM(cd.montoOriginal), 0) AS totalMontoOriginal,
            SUM(CASE WHEN LOWER(TRIM(cd.vigenteOVencido)) = 'vigente' THEN 1 ELSE 0 END) AS totalCreditosVigentes,
            SUM(CASE WHEN LOWER(TRIM(cd.vigenteOVencido)) = 'vencido' THEN 1 ELSE 0 END) AS totalCreditosVencidos,
            COALESCE(SUM(cd.capitalVigente), 0) AS capitalVigente,
            COALESCE(SUM(cd.capitalVencido), 0) AS capitalVencido,
            COALESCE(SUM(cd.intDevNoCobradosVigentes), 0) AS interesesVigentes,
            COALESCE(SUM(cd.intDevNoCobradosVencidos), 0) AS interesesVencidos,
            COALESCE(SUM(cd.intDevNoCobradosCtasOrden), 0) AS interesesOrden,
            COALESCE(SUM(cd.montoUltimoPagoCapital), 0) AS ultimosPagosCapital,
            COALESCE(SUM(cd.montoUltimoPagoIntereses), 0) AS ultimosPagosInteres,
            COALESCE(SUM(cd.diasMora), 0) AS totalDiasMora,
            COALESCE(SUM(cd.montoGarantiaLiquida), 0) AS garantiaLiquida,
            COALESCE(SUM(cd.eprcContableParteCubierta), 0) AS eprcParteCubierta,
            COALESCE(SUM(cd.eprcContableParteExpuesta), 0) AS eprcParteExpuesta,
            COALESCE(SUM(cd.eprcContableXInteresesCee), 0) AS eprcInteresCee
        FROM CarteraDatos cd
        LEFT JOIN CarteraDatosCalculados cdc ON cd.idAnalisisMensual = cdc.idAnalisisMensual
        WHERE cd.mesCorte = :mesCorte
    """)
    Optional<CarteraTotalesProjection> obtenerTotalesPorMesCorte(@Param("mesCorte") LocalDate mesCorte);
}
