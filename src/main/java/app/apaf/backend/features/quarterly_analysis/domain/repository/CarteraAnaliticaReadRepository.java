package app.apaf.backend.features.quarterly_analysis.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CarteraAnaliticaReadRepository extends JpaRepository<app.apaf.backend.features.quarterly_analysis.domain.model.AnalisisTrimestralEjecucion, java.util.UUID> {

    interface ResumenCarteraProjection {
        String getSucursalCodigo();
        String getTipoCartera();
        Long getCreditosVigentes();
        BigDecimal getCapitalVigente();
        BigDecimal getInteresesVigentes();
        Long getCreditosVencidos();
        BigDecimal getCapitalVencido();
        BigDecimal getInteresesVencidos();
        Long getCreditosTotal();
        BigDecimal getCarteraTotal();
    }

    @Query(value = "SELECT COUNT(cd.id_analisis_mensual) FROM cartera_datos cd WHERE cd.mes_corte = :mesCorte", nativeQuery = true)
    long contarRegistrosPorMesCorte(@Param("mesCorte") LocalDate mesCorte);

    @Query(value = """
        SELECT 
            cd.sucursal AS sucursalCodigo,
            CASE cdc.cartera_tipo WHEN 0 THEN 'CONSUMO' WHEN 1 THEN 'COMERCIAL' WHEN 2 THEN 'VIVIENDA' ELSE 'CONSUMO' END AS tipoCartera,
            COUNT(CASE WHEN COALESCE(cd.capital_vencido, 0) + COALESCE(cd.int_dev_no_cobrados_vencidos, 0) = 0 THEN cd.id_analisis_mensual END) AS creditosVigentes,
            SUM(COALESCE(cd.capital_vigente, 0)) AS capitalVigente,
            SUM(COALESCE(cd.int_dev_no_cobrados_vigentes, 0)) AS interesesVigentes,
            COUNT(CASE WHEN COALESCE(cd.capital_vencido, 0) + COALESCE(cd.int_dev_no_cobrados_vencidos, 0) > 0 THEN cd.id_analisis_mensual END) AS creditosVencidos,
            SUM(COALESCE(cd.capital_vencido, 0)) AS capitalVencido,
            SUM(COALESCE(cd.int_dev_no_cobrados_vencidos, 0)) AS interesesVencidos,
            COUNT(cd.id_analisis_mensual) AS creditosTotal,
            SUM(COALESCE(cdc.cartera_total, 0)) AS carteraTotal
        FROM cartera_datos cd
        JOIN cartera_datos_calculados cdc ON cdc.id_analisis_mensual = cd.id_analisis_mensual
        WHERE cd.mes_corte = :mesCorte
        GROUP BY cd.sucursal, CASE cdc.cartera_tipo WHEN 0 THEN 'CONSUMO' WHEN 1 THEN 'COMERCIAL' WHEN 2 THEN 'VIVIENDA' ELSE 'CONSUMO' END
        """, nativeQuery = true)
    List<ResumenCarteraProjection> obtenerResumenPorSucursalYTipo(@Param("mesCorte") LocalDate mesCorte);

    interface ProductoVencidoProjection {
        String getProductoCodigo();
        String getProductoNombre();
        Long getCreditosVencidos();
        BigDecimal getImporteVencido();
    }

    @Query(value = """
        SELECT 
            cdc.numero_producto AS productoCodigo,
            cd.producto_credito AS productoNombre,
            COUNT(cd.id_analisis_mensual) AS creditosVencidos,
            SUM(COALESCE(cd.capital_vencido, 0) + COALESCE(cd.int_dev_no_cobrados_vencidos, 0)) AS importeVencido
        FROM cartera_datos cd
        JOIN cartera_datos_calculados cdc ON cdc.id_analisis_mensual = cd.id_analisis_mensual
        WHERE cd.mes_corte = :mesCorte 
          AND (COALESCE(cd.capital_vencido, 0) + COALESCE(cd.int_dev_no_cobrados_vencidos, 0)) > 0
        GROUP BY cdc.numero_producto, cd.producto_credito
        """, nativeQuery = true)
    List<ProductoVencidoProjection> obtenerProductosVencidos(@Param("mesCorte") LocalDate mesCorte);

    interface BandaMorosidadProjection {
        String getTipoCartera();
        String getRangoId();
        String getRangoEtiqueta();
        Integer getOrden();
        Long getCreditos();
        BigDecimal getImporte();
    }

    @Query(value = """
        SELECT 
            CASE cdc.cartera_tipo WHEN 0 THEN 'CONSUMO' WHEN 1 THEN 'COMERCIAL' WHEN 2 THEN 'VIVIENDA' ELSE 'CONSUMO' END AS tipoCartera,
            COALESCE(cdc.intervalo_morosidad_y_tipo_cartera, CASE cdc.cartera_tipo WHEN 0 THEN '01' WHEN 1 THEN '11' WHEN 2 THEN '21' ELSE '01' END) AS rangoId,
            CASE COALESCE(cdc.intervalo_morosidad_y_tipo_cartera, CASE cdc.cartera_tipo WHEN 0 THEN '01' WHEN 1 THEN '11' WHEN 2 THEN '21' ELSE '01' END)
                WHEN '01' THEN '0 días' WHEN '02' THEN '1-7 días' WHEN '03' THEN '8-30 días' WHEN '04' THEN '31-60 días' WHEN '05' THEN '61-90 días' WHEN '06' THEN '91-120 días' WHEN '07' THEN '121- 180 días' WHEN '08' THEN '181 ó más'
                WHEN '11' THEN '0 días' WHEN '12' THEN '1-30 días' WHEN '13' THEN '31-60 días' WHEN '14' THEN '61-90 días' WHEN '15' THEN '91-120 días' WHEN '16' THEN '121- 150 días' WHEN '17' THEN '151 a 180 días' WHEN '18' THEN '181 a 210 días' WHEN '19' THEN '211 a 240 días' WHEN '110' THEN 'más de 240'
                WHEN '21' THEN '0 días' WHEN '22' THEN '1-30 días' WHEN '23' THEN '31-60 días' WHEN '24' THEN '61-90 días' WHEN '25' THEN '91-120 días' WHEN '26' THEN '121- 150 días' WHEN '27' THEN '151 a 180 días' WHEN '28' THEN '181 a 1460 días' WHEN '29' THEN 'más de 1460'
                ELSE 'No Registrado'
            END AS rangoEtiqueta,
            99 AS orden,
            COUNT(cd.id_analisis_mensual) AS creditos,
            SUM(COALESCE(cd.capital_vencido, 0) + COALESCE(cd.int_dev_no_cobrados_vencidos, 0)) AS importe
        FROM cartera_datos cd
        JOIN cartera_datos_calculados cdc ON cdc.id_analisis_mensual = cd.id_analisis_mensual
        WHERE cd.mes_corte = :mesCorte 
          AND (COALESCE(cd.capital_vencido, 0) + COALESCE(cd.int_dev_no_cobrados_vencidos, 0)) > 0
        GROUP BY cdc.cartera_tipo, cdc.intervalo_morosidad_y_tipo_cartera
        """, nativeQuery = true)
    List<BandaMorosidadProjection> obtenerBandasMorosidadVencida(@Param("mesCorte") LocalDate mesCorte);

    @Query(value = """
        SELECT 
            CASE cdc.cartera_tipo WHEN 0 THEN 'CONSUMO' WHEN 1 THEN 'COMERCIAL' WHEN 2 THEN 'VIVIENDA' ELSE 'CONSUMO' END AS tipoCartera,
            COALESCE(cdc.intervalo_morosidad_y_tipo_cartera, CASE cdc.cartera_tipo WHEN 0 THEN '01' WHEN 1 THEN '11' WHEN 2 THEN '21' ELSE '01' END) AS rangoId,
            CASE COALESCE(cdc.intervalo_morosidad_y_tipo_cartera, CASE cdc.cartera_tipo WHEN 0 THEN '01' WHEN 1 THEN '11' WHEN 2 THEN '21' ELSE '01' END)
                WHEN '01' THEN '0 días' WHEN '02' THEN '1-7 días' WHEN '03' THEN '8-30 días' WHEN '04' THEN '31-60 días' WHEN '05' THEN '61-90 días' WHEN '06' THEN '91-120 días' WHEN '07' THEN '121- 180 días' WHEN '08' THEN '181 ó más'
                WHEN '11' THEN '0 días' WHEN '12' THEN '1-30 días' WHEN '13' THEN '31-60 días' WHEN '14' THEN '61-90 días' WHEN '15' THEN '91-120 días' WHEN '16' THEN '121- 150 días' WHEN '17' THEN '151 a 180 días' WHEN '18' THEN '181 a 210 días' WHEN '19' THEN '211 a 240 días' WHEN '110' THEN 'más de 240'
                WHEN '21' THEN '0 días' WHEN '22' THEN '1-30 días' WHEN '23' THEN '31-60 días' WHEN '24' THEN '61-90 días' WHEN '25' THEN '91-120 días' WHEN '26' THEN '121- 150 días' WHEN '27' THEN '151 a 180 días' WHEN '28' THEN '181 a 1460 días' WHEN '29' THEN 'más de 1460'
                ELSE 'No Registrado'
            END AS rangoEtiqueta,
            99 AS orden,
            COUNT(cd.id_analisis_mensual) AS creditos,
            SUM(COALESCE(cd.capital_vigente, 0) + COALESCE(cd.capital_vencido, 0) + COALESCE(cd.int_dev_no_cobrados_vigentes, 0) + COALESCE(cd.int_dev_no_cobrados_vencidos, 0)) AS importe
        FROM cartera_datos cd
        JOIN cartera_datos_calculados cdc ON cdc.id_analisis_mensual = cd.id_analisis_mensual
        WHERE cd.mes_corte = :mesCorte 
        GROUP BY cdc.cartera_tipo, cdc.intervalo_morosidad_y_tipo_cartera
        """, nativeQuery = true)
    List<BandaMorosidadProjection> obtenerBandasMorosidadTotal(@Param("mesCorte") LocalDate mesCorte);

    @Query(value = """
        SELECT 
            COUNT(cd.id_analisis_mensual) AS base,
            COUNT(cdc.id_analisis_mensual) AS calculados,
            MIN(cd.fecha_corte) AS fechaMin,
            MAX(cd.fecha_corte) AS fechaMax
        FROM cartera_datos cd
        LEFT JOIN cartera_datos_calculados cdc ON cdc.id_analisis_mensual = cd.id_analisis_mensual
        WHERE cd.mes_corte = :mesCorte
        """, nativeQuery = true)
    List<Object[]> obtenerConteoPeriodo(@Param("mesCorte") LocalDate mesCorte);

    interface ProporcionSucursalProjection {
        String getSucursalId();
        String getNombre();
        BigDecimal getCarteraTotal();
    }

    @Query(value = """
        SELECT 
            cs.codigo AS sucursalId,
            cs.nombre AS nombre,
            SUM(COALESCE(cdc.cartera_total, 0)) AS carteraTotal
        FROM cartera_datos cd
        JOIN cartera_datos_calculados cdc ON cdc.id_analisis_mensual = cd.id_analisis_mensual
        JOIN catalogo_sucursal cs ON cd.sucursal = cs.codigo
        WHERE cd.mes_corte = :mesCorte
        GROUP BY cs.codigo, cs.nombre
        """, nativeQuery = true)
    List<ProporcionSucursalProjection> obtenerProporcionesPorSucursal(@Param("mesCorte") LocalDate mesCorte);

    interface DatosGraficaProjection {
        String getRango();
        BigDecimal getMonto();
    }

    @Query(value = """
        SELECT
            CASE 
                WHEN cd.dias_mora BETWEEN 0 AND 30 THEN 'r-1'
                WHEN cd.dias_mora BETWEEN 31 AND 60 THEN 'r-2'
                WHEN cd.dias_mora BETWEEN 61 AND 90 THEN 'r-3'
                WHEN cd.dias_mora BETWEEN 91 AND 120 THEN 'r-4'
                ELSE 'r-5'
            END AS rango,
            SUM(COALESCE(cdc.cartera_total, 0)) AS monto
        FROM cartera_datos cd
        JOIN cartera_datos_calculados cdc ON cd.id_analisis_mensual = cdc.id_analisis_mensual
        WHERE cd.mes_corte = :mesCorte
        GROUP BY 
            CASE 
                WHEN cd.dias_mora BETWEEN 0 AND 30 THEN 'r-1'
                WHEN cd.dias_mora BETWEEN 31 AND 60 THEN 'r-2'
                WHEN cd.dias_mora BETWEEN 61 AND 90 THEN 'r-3'
                WHEN cd.dias_mora BETWEEN 91 AND 120 THEN 'r-4'
                ELSE 'r-5'
            END
        """, nativeQuery = true)
    List<DatosGraficaProjection> obtenerDatosGrafica(@Param("mesCorte") LocalDate mesCorte);
}
