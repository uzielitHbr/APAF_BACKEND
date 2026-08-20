package app.apaf.backend.features.follow_up_management.application.snapshot;

import app.apaf.backend.features.follow_up_management.domain.entity.SeguimientoEjecucionEntity;
import app.apaf.backend.features.follow_up_management.domain.entity.SeguimientoMorosidadEntity;
import app.apaf.backend.features.follow_up_management.domain.entity.SeguimientoPlazoEntity;
import app.apaf.backend.features.follow_up_management.domain.entity.SeguimientoSaldoEntity;
import app.apaf.backend.features.follow_up_management.domain.repository.SeguimientoEjecucionRepository;
import app.apaf.backend.features.follow_up_management.domain.repository.SeguimientoMorosidadRepository;
import app.apaf.backend.features.follow_up_management.domain.repository.SeguimientoPlazoRepository;
import app.apaf.backend.features.follow_up_management.domain.repository.SeguimientoSaldoRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GenerarSnapshotSeguimientoHandler {

    private final SeguimientoEjecucionRepository ejecucionRepository;
    private final SeguimientoSaldoRepository saldoRepository;
    private final SeguimientoMorosidadRepository morosidadRepository;
    private final SeguimientoPlazoRepository plazoRepository;
    private final JdbcTemplate jdbcTemplate;

    public GenerarSnapshotSeguimientoHandler(SeguimientoEjecucionRepository ejecucionRepository,
                                             SeguimientoSaldoRepository saldoRepository,
                                             SeguimientoMorosidadRepository morosidadRepository,
                                             SeguimientoPlazoRepository plazoRepository,
                                             JdbcTemplate jdbcTemplate) {
        this.ejecucionRepository = ejecucionRepository;
        this.saldoRepository = saldoRepository;
        this.morosidadRepository = morosidadRepository;
        this.plazoRepository = plazoRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void generarSiNoExiste(YearMonth mesCorte) {
        LocalDate fecha = mesCorte.atDay(1);
        if (ejecucionRepository.existsByMesCorte(fecha)) {
            return;
        }

        String baseQuery = """
            SELECT 
                %s,
                COUNT(cd.id_analisis_mensual) as num_creditos,
                COALESCE(SUM(cd.capital_vigente), 0) as capital_vigente,
                COALESCE(SUM(cd.int_dev_no_cobrados_vigentes), 0) as int_vigente,
                COALESCE(SUM(cd.capital_vencido), 0) as capital_vencido,
                COALESCE(SUM(cd.int_dev_no_cobrados_vencidos), 0) as int_vencido,
                COALESCE(SUM(cd.int_dev_no_cobrados_ctas_orden), 0) as cuentas_orden,
                COALESCE(SUM(cdc.cartera_total), 0) as saldo_total,
                SUM(CASE WHEN cdc.recuperacion_en_el_mes_capital > 0 OR cdc.recuperacion_en_el_mes_intereses > 0 THEN 1 ELSE 0 END) as con_movimiento,
                SUM(CASE WHEN COALESCE(cdc.recuperacion_en_el_mes_capital, 0) = 0 AND COALESCE(cdc.recuperacion_en_el_mes_intereses, 0) = 0 THEN 1 ELSE 0 END) as sin_movimiento,
                SUM(CASE WHEN DATE_TRUNC('month', cd.fecha_otorgamiento) = cd.mes_corte THEN 1 ELSE 0 END) as otorgados_mes
            FROM cartera_datos cd
            JOIN cartera_datos_calculados cdc ON cd.id_analisis_mensual = cdc.id_analisis_mensual
            WHERE cd.mes_corte = ?
            GROUP BY %s
        """;

        // 1. SALDO
        String querySaldo = String.format(baseQuery, "cd.sucursal as agrupacion", "cd.sucursal");
        List<Map<String, Object>> saldoRows = jdbcTemplate.queryForList(querySaldo, fecha);

        BigDecimal carteraTotalGlobal = saldoRows.stream()
            .map(row -> (BigDecimal) row.get("saldo_total"))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal carteraVencidaGlobal = saldoRows.stream()
            .map(row -> ((BigDecimal) row.get("capital_vencido")).add((BigDecimal) row.get("int_vencido")))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal imorGeneral = carteraTotalGlobal.compareTo(BigDecimal.ZERO) > 0 ? 
            carteraVencidaGlobal.divide(carteraTotalGlobal, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) : BigDecimal.ZERO;

        int numCreditosGlobal = 0;
        BigDecimal capitalVigenteGlobal = BigDecimal.ZERO;
        BigDecimal intVigenteGlobal = BigDecimal.ZERO;
        BigDecimal cuentasOrdenGlobal = BigDecimal.ZERO;
        int conMovGlobal = 0;
        int sinMovGlobal = 0;
        int otorgadosGlobal = 0;

        List<SeguimientoSaldoEntity> saldos = new ArrayList<>();
        for (Map<String, Object> row : saldoRows) {
            BigDecimal saldoSuc = (BigDecimal) row.get("saldo_total");
            BigDecimal cvSuc = (BigDecimal) row.get("capital_vencido");
            BigDecimal ivSuc = (BigDecimal) row.get("int_vencido");
            BigDecimal carteraVencidaSuc = cvSuc.add(ivSuc);

            BigDecimal proporcion = carteraTotalGlobal.compareTo(BigDecimal.ZERO) > 0 ?
                saldoSuc.divide(carteraTotalGlobal, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) : BigDecimal.ZERO;
            
            BigDecimal imorSuc = saldoSuc.compareTo(BigDecimal.ZERO) > 0 ?
                carteraVencidaSuc.divide(saldoSuc, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) : BigDecimal.ZERO;

            saldos.add(SeguimientoSaldoEntity.builder()
                .mesCorte(fecha)
                .sucursal((String) row.get("agrupacion"))
                .numeroCreditos(((Number) row.get("num_creditos")).intValue())
                .capitalVigente((BigDecimal) row.get("capital_vigente"))
                .interesOrdVigente((BigDecimal) row.get("int_vigente"))
                .capitalVencido(cvSuc)
                .interesOrdVencido(ivSuc)
                .cuentasOrden((BigDecimal) row.get("cuentas_orden"))
                .saldoTotal(saldoSuc)
                .creditosConMovimiento(((Number) row.get("con_movimiento")).intValue())
                .creditosSinMovimiento(((Number) row.get("sin_movimiento")).intValue())
                .creditosOtorgadosMes(((Number) row.get("otorgados_mes")).intValue())
                .imorGeneral(imorGeneral)
                .proporcionCartera(proporcion)
                .imorSucursal(imorSuc)
                .imorProyectado(imorSuc) 
                .esTotal(false)
                .build());

            numCreditosGlobal += ((Number) row.get("num_creditos")).intValue();
            capitalVigenteGlobal = capitalVigenteGlobal.add((BigDecimal) row.get("capital_vigente"));
            intVigenteGlobal = intVigenteGlobal.add((BigDecimal) row.get("int_vigente"));
            cuentasOrdenGlobal = cuentasOrdenGlobal.add((BigDecimal) row.get("cuentas_orden"));
            conMovGlobal += ((Number) row.get("con_movimiento")).intValue();
            sinMovGlobal += ((Number) row.get("sin_movimiento")).intValue();
            otorgadosGlobal += ((Number) row.get("otorgados_mes")).intValue();
        }

        saldos.add(SeguimientoSaldoEntity.builder()
            .mesCorte(fecha)
            .sucursal("TOTAL")
            .numeroCreditos(numCreditosGlobal)
            .capitalVigente(capitalVigenteGlobal)
            .interesOrdVigente(intVigenteGlobal)
            .capitalVencido(carteraVencidaGlobal.subtract(intVigenteGlobal.max(BigDecimal.ZERO) /* aprox */))
            .interesOrdVencido(BigDecimal.ZERO) 
            .cuentasOrden(cuentasOrdenGlobal)
            .saldoTotal(carteraTotalGlobal)
            .creditosConMovimiento(conMovGlobal)
            .creditosSinMovimiento(sinMovGlobal)
            .creditosOtorgadosMes(otorgadosGlobal)
            .imorGeneral(imorGeneral)
            .proporcionCartera(new BigDecimal("100"))
            .imorSucursal(imorGeneral)
            .imorProyectado(imorGeneral)
            .esTotal(true)
            .build());

        saldoRepository.saveAll(saldos);

        // 2. MOROSIDAD
        String morosidadAgrupacion = """
            cd.sucursal, 
            CASE 
                WHEN cd.dias_mora BETWEEN 1 AND 29 THEN '1a29'
                WHEN cd.dias_mora BETWEEN 30 AND 60 THEN '30a60'
                WHEN cd.dias_mora BETWEEN 61 AND 89 THEN '61a89'
                ELSE 'OTRO'
            END as rango_mora
        """;
        String queryMorosidad = String.format(baseQuery, morosidadAgrupacion, "cd.sucursal, rango_mora");
        List<Map<String, Object>> morosidadRows = jdbcTemplate.queryForList(queryMorosidad, fecha);
        
        List<SeguimientoMorosidadEntity> morosidades = new ArrayList<>();
        for (Map<String, Object> row : morosidadRows) {
            String rango = (String) row.get("rango_mora");
            if ("OTRO".equals(rango)) continue;

            morosidades.add(SeguimientoMorosidadEntity.builder()
                .mesCorte(fecha)
                .sucursal((String) row.get("sucursal"))
                .rangoMora(rango)
                .numeroCreditos(((Number) row.get("num_creditos")).intValue())
                .capitalVigente((BigDecimal) row.get("capital_vigente"))
                .interesOrdVigente((BigDecimal) row.get("int_vigente"))
                .capitalVencido((BigDecimal) row.get("capital_vencido"))
                .interesOrdVencido((BigDecimal) row.get("int_vencido"))
                .cuentasOrden((BigDecimal) row.get("cuentas_orden"))
                .saldoTotal((BigDecimal) row.get("saldo_total"))
                .creditosConMovimiento(((Number) row.get("con_movimiento")).intValue())
                .creditosSinMovimiento(((Number) row.get("sin_movimiento")).intValue())
                .creditosOtorgadosMes(((Number) row.get("otorgados_mes")).intValue())
                .build());
        }
        morosidadRepository.saveAll(morosidades);

        // 3. PLAZO
        String plazoBucket = """
            CASE 
                WHEN cd.fecha_vencimiento <= (cd.mes_corte + INTERVAL '1 year') THEN '1 Año'
                WHEN cd.fecha_vencimiento <= (cd.mes_corte + INTERVAL '2 years') THEN '2 Años'
                WHEN cd.fecha_vencimiento <= (cd.mes_corte + INTERVAL '3 years') THEN '3 Años'
                WHEN cd.fecha_vencimiento <= (cd.mes_corte + INTERVAL '4 years') THEN '4 Años'
                WHEN cd.fecha_vencimiento <= (cd.mes_corte + INTERVAL '5 years') THEN '5 Años'
                WHEN cd.fecha_vencimiento <= (cd.mes_corte + INTERVAL '6 years') THEN '6 Años'
                WHEN cd.fecha_vencimiento <= (cd.mes_corte + INTERVAL '7 years') THEN '7 Años'
                ELSE '8 Años'
            END""";

        String plazoAgrupacion = "cd.sucursal,\n" + plazoBucket + " as plazo_remanente";
        String queryPlazo = String.format(baseQuery, plazoAgrupacion, "cd.sucursal, " + plazoBucket);
        List<Map<String, Object>> plazoRows = jdbcTemplate.queryForList(queryPlazo, fecha);
        
        List<SeguimientoPlazoEntity> plazos = new ArrayList<>();
        for (Map<String, Object> row : plazoRows) {
            String plazoRem = (String) row.get("plazo_remanente");
            if (plazoRem == null) plazoRem = "Desconocido";

            BigDecimal saldoPlazo = (BigDecimal) row.get("saldo_total");
            BigDecimal cvPlazo = (BigDecimal) row.get("capital_vencido");
            BigDecimal ivPlazo = (BigDecimal) row.get("int_vencido");
            BigDecimal proporcion = carteraTotalGlobal.compareTo(BigDecimal.ZERO) > 0 ?
                saldoPlazo.divide(carteraTotalGlobal, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) : BigDecimal.ZERO;
            BigDecimal imorPlazo = saldoPlazo.compareTo(BigDecimal.ZERO) > 0 ?
                cvPlazo.add(ivPlazo).divide(saldoPlazo, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) : BigDecimal.ZERO;

            plazos.add(SeguimientoPlazoEntity.builder()
                .mesCorte(fecha)
                .sucursal((String) row.get("sucursal"))
                .tipoVista("SUCURSAL")
                .plazoRemanente(plazoRem)
                .numeroCreditos(((Number) row.get("num_creditos")).intValue())
                .capitalVigente((BigDecimal) row.get("capital_vigente"))
                .interesOrdVigente((BigDecimal) row.get("int_vigente"))
                .capitalVencido((BigDecimal) row.get("capital_vencido"))
                .interesOrdVencido((BigDecimal) row.get("int_vencido"))
                .cuentasOrden((BigDecimal) row.get("cuentas_orden"))
                .saldoTotal(saldoPlazo)
                .creditosConMovimiento(((Number) row.get("con_movimiento")).intValue())
                .creditosSinMovimiento(((Number) row.get("sin_movimiento")).intValue())
                .creditosOtorgadosMes(((Number) row.get("otorgados_mes")).intValue())
                .imor(imorPlazo)
                .proporcion(proporcion)
                .build());
        }

        String queryPlazoConsolidado = String.format(baseQuery, 
            plazoBucket + " as plazo_remanente", 
            plazoBucket);
        List<Map<String, Object>> plazoConsolidadoRows = jdbcTemplate.queryForList(queryPlazoConsolidado, fecha);
        
        for (Map<String, Object> row : plazoConsolidadoRows) {
            String plazoRem = (String) row.get("plazo_remanente");
            if (plazoRem == null) plazoRem = "Desconocido";

            BigDecimal saldoPlazo = (BigDecimal) row.get("saldo_total");
            BigDecimal cvPlazo = (BigDecimal) row.get("capital_vencido");
            BigDecimal ivPlazo = (BigDecimal) row.get("int_vencido");
            BigDecimal proporcion = carteraTotalGlobal.compareTo(BigDecimal.ZERO) > 0 ?
                saldoPlazo.divide(carteraTotalGlobal, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) : BigDecimal.ZERO;
            BigDecimal imorPlazo = saldoPlazo.compareTo(BigDecimal.ZERO) > 0 ?
                cvPlazo.add(ivPlazo).divide(saldoPlazo, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) : BigDecimal.ZERO;

            plazos.add(SeguimientoPlazoEntity.builder()
                .mesCorte(fecha)
                .sucursal(null)
                .tipoVista("CONSOLIDADO")
                .plazoRemanente(plazoRem)
                .numeroCreditos(((Number) row.get("num_creditos")).intValue())
                .capitalVigente((BigDecimal) row.get("capital_vigente"))
                .interesOrdVigente((BigDecimal) row.get("int_vigente"))
                .capitalVencido((BigDecimal) row.get("capital_vencido"))
                .interesOrdVencido((BigDecimal) row.get("int_vencido"))
                .cuentasOrden((BigDecimal) row.get("cuentas_orden"))
                .saldoTotal(saldoPlazo)
                .creditosConMovimiento(((Number) row.get("con_movimiento")).intValue())
                .creditosSinMovimiento(((Number) row.get("sin_movimiento")).intValue())
                .creditosOtorgadosMes(((Number) row.get("otorgados_mes")).intValue())
                .imor(imorPlazo)
                .proporcion(proporcion)
                .build());
        }
        plazoRepository.saveAll(plazos);

        SeguimientoEjecucionEntity ejecucion = new SeguimientoEjecucionEntity();
        ejecucion.setMesCorte(fecha);
        ejecucion.setFechaEjecucion(LocalDateTime.now());
        ejecucionRepository.save(ejecucion);
    }
}
