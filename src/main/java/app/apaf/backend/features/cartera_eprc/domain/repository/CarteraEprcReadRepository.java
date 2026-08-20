package app.apaf.backend.features.cartera_eprc.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CarteraEprcReadRepository extends JpaRepository<app.apaf.backend.features.cartera_eprc.domain.entity.EprcEjecucionEntity, java.util.UUID> {

    @Query(value = "SELECT COUNT(cd.id_analisis_mensual) FROM cartera_datos cd WHERE cd.mes_corte = :mesCorte", nativeQuery = true)
    long contarRegistrosPorMesCorte(@Param("mesCorte") LocalDate mesCorte);

    interface EstratificacionCarteraProjection {
        String getTipoCartera();
        String getCodigoIntervalo();
        String getIntervaloVencimiento();
        Long getNumeroCreditos();
        BigDecimal getSaldoCapital();
        BigDecimal getSaldoInteresVigente();
        BigDecimal getSaldoInteresVencido();
        BigDecimal getSaldoCarteraTotal();
        BigDecimal getGarantiaLiquida();
        BigDecimal getGarantiaHipotecaria();
        BigDecimal getEprcParteCubierta();
        BigDecimal getEprcParteExpuesta();
        BigDecimal getEstPrevInteresesVencidos();
        BigDecimal getImporteEstimacionPreventiva();
    }

    @Query(value = """
        SELECT 
            CASE cdc.cartera_tipo 
                WHEN 0 THEN 'CONSUMO' 
                WHEN 1 THEN 'COMERCIAL' 
                WHEN 2 THEN 'VIVIENDA' 
                ELSE 'CONSUMO' 
            END AS tipoCartera,
            cdc.intervalo_morosidad_y_tipo_cartera AS codigoIntervalo,
            CASE cdc.intervalo_morosidad_y_tipo_cartera
                WHEN '01' THEN '0 días'
                WHEN '02' THEN '1-7 días'
                WHEN '03' THEN '8-30 días'
                WHEN '04' THEN '31-60 días'
                WHEN '05' THEN '61-90 días'
                WHEN '06' THEN '91-120 días'
                WHEN '07' THEN '121- 180 días'
                WHEN '08' THEN '181 o más'
                WHEN '11' THEN '0 días'
                WHEN '12' THEN '1-30 días'
                WHEN '13' THEN '31-60 días'
                WHEN '14' THEN '61-90 días'
                WHEN '15' THEN '91-120 días'
                WHEN '16' THEN '121- 150 días'
                WHEN '17' THEN '151 a 180 días'
                WHEN '18' THEN '181 a 210 días'
                WHEN '19' THEN '211 a 240 días'
                WHEN '110' THEN 'más de 240'
                WHEN '21' THEN '0 días'
                WHEN '22' THEN '1-30 días'
                WHEN '23' THEN '31-60 días'
                WHEN '24' THEN '61-90 días'
                WHEN '25' THEN '91-120 días'
                WHEN '26' THEN '121- 150 días'
                WHEN '27' THEN '151 a 180 días'
                WHEN '28' THEN '181 a 1460 días'
                WHEN '29' THEN 'más de 1460'
                ELSE COALESCE(cdc.intervalo_morosidad_y_tipo_cartera, 'No Registrado')
            END AS intervaloVencimiento,
            COUNT(cd.id_analisis_mensual) AS numeroCreditos,
            SUM(COALESCE(cd.capital_vigente, 0) + COALESCE(cd.capital_vencido, 0)) AS saldoCapital,
            SUM(COALESCE(cd.int_dev_no_cobrados_vigentes, 0)) AS saldoInteresVigente,
            SUM(COALESCE(cd.int_dev_no_cobrados_vencidos, 0)) AS saldoInteresVencido,
            SUM(COALESCE(cd.capital_vigente, 0) + COALESCE(cd.capital_vencido, 0) + 
                COALESCE(cd.int_dev_no_cobrados_vigentes, 0) + COALESCE(cd.int_dev_no_cobrados_vencidos, 0)) AS saldoCarteraTotal,
            SUM(COALESCE(cd.monto_garantia_liquida, 0)) AS garantiaLiquida,
            SUM(CASE WHEN cdc.cartera_tipo = 2 THEN COALESCE(cd.monto_garantia_hipotecaria, 0) ELSE 0 END) AS garantiaHipotecaria,
            SUM(COALESCE(cd.eprc_contable_parte_cubierta, 0)) AS eprcParteCubierta,
            SUM(COALESCE(cd.eprc_contable_parte_expuesta, 0)) AS eprcParteExpuesta,
            SUM(COALESCE(cd.eprc_contable_x_intereses_cee, 0)) AS estPrevInteresesVencidos,
            SUM(COALESCE(cd.eprc_contable_parte_cubierta, 0) + 
                COALESCE(cd.eprc_contable_parte_expuesta, 0) + 
                COALESCE(cd.eprc_contable_x_intereses_cee, 0)) AS importeEstimacionPreventiva
        FROM cartera_datos cd
        JOIN cartera_datos_calculados cdc ON cdc.id_analisis_mensual = cd.id_analisis_mensual
        WHERE cd.mes_corte = :mesCorte 
        GROUP BY 
            CASE cdc.cartera_tipo 
                WHEN 0 THEN 'CONSUMO' 
                WHEN 1 THEN 'COMERCIAL' 
                WHEN 2 THEN 'VIVIENDA' 
                ELSE 'CONSUMO' 
            END,
            cdc.intervalo_morosidad_y_tipo_cartera
        """, nativeQuery = true)
    List<EstratificacionCarteraProjection> agruparEstratificacionPorMesCorte(@Param("mesCorte") LocalDate mesCorte);
}
