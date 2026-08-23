package app.apaf.backend.features.risk_management.analysis;

import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import app.apaf.backend.features.risk_management.options_limit.OpcionLimiteDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class RiesgoAnalisisReadRepositoryImpl implements RiesgoAnalisisReadCustomRepository {

    private final JdbcTemplate jdbcTemplate;

    public RiesgoAnalisisReadRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Page<RiesgoSegmentoProjection> obtenerAnalisisPorAgrupacion(AgrupacionRiesgo agrupacion, LocalDate mesCorte, Pageable pageable) {
        String columnClave;
        String columnIdentificacion;

        switch (agrupacion) {
            case PRODUCTO -> { columnClave = "v.numero_producto"; columnIdentificacion = "v.producto_credito"; }
            case MUNICIPIO -> { columnClave = "UPPER(v.municipio)"; columnIdentificacion = "v.municipio"; }
            case ESTADO -> { columnClave = "UPPER(v.estado)"; columnIdentificacion = "v.estado"; }
            case OCUPACION -> { columnClave = "UPPER(v.ocupacion_agrupada)"; columnIdentificacion = "v.ocupacion_agrupada"; }
            case EDAD -> { columnClave = "CAST(v.intervalo_edad AS VARCHAR)"; columnIdentificacion = "CAST(v.intervalo_edad AS VARCHAR)"; }
            case GENERO -> { columnClave = "UPPER(v.genero)"; columnIdentificacion = "v.genero"; }
            case SUCURSAL -> { columnClave = "UPPER(v.sucursal)"; columnIdentificacion = "v.sucursal"; }
            case ACREDITADO -> { columnClave = "UPPER(v.cargo_acreditado_parte_relacionada)"; columnIdentificacion = "v.cargo_acreditado_parte_relacionada"; }
            case MODALIDAD -> { columnClave = "UPPER(v.modalidad_pago)"; columnIdentificacion = "v.modalidad_pago"; }
            case TIPO_CLASIFICACION -> { columnClave = "UPPER(v.renovado_reestructurado_normal)"; columnIdentificacion = "v.renovado_reestructurado_normal"; }
            default -> throw new IllegalArgumentException("Agrupacion invalida");
        }

        String baseQuery = """
            SELECT
                CAST(COALESCE(rl.id_limite, rlo.id_limite) AS VARCHAR) as "idLimite",
                COALESCE(rl.clave, 'OTROS') as "clave",
                COALESCE(rl.identificacion, 'Otros') as "identificacion",
                SUM(v.numero_creditos) as "numeroCreditos",
                SUM(v.cartera_vigente) as "carteraVigente",
                SUM(v.cartera_vencida) as "carteraVencida",
                SUM(v.cartera_total) as "carteraTotal",
                COALESCE(rlv.tipo_limite, rlo_v.tipo_limite) as "tipoLimite",
                COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje) as "limiteEstablecidoPorcentaje"
            FROM view_riesgo_cartera_mensual v
            LEFT JOIN riesgo_limite rl ON rl.agrupacion = ? AND rl.clave = %s
            LEFT JOIN riesgo_limite_version rlv ON rlv.id_limite = rl.id_limite
                AND rlv.vigente_desde <= v.fecha_corte AND (rlv.vigente_hasta IS NULL OR rlv.vigente_hasta > v.fecha_corte)
            LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = ? AND rlo.clave = 'OTROS' AND rl.id_limite IS NULL
            LEFT JOIN riesgo_limite_version rlo_v ON rlo_v.id_limite = rlo.id_limite
                AND rlo_v.vigente_desde <= v.fecha_corte AND (rlo_v.vigente_hasta IS NULL OR rlo_v.vigente_hasta > v.fecha_corte)
            WHERE v.mes_corte = ?
            GROUP BY
                COALESCE(rl.id_limite, rlo.id_limite),
                COALESCE(rl.clave, 'OTROS'),
                COALESCE(rl.identificacion, 'Otros'),
                COALESCE(rlv.tipo_limite, rlo_v.tipo_limite),
                COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje)
        """.formatted(columnClave);

        String countQuery = """
            SELECT COUNT(DISTINCT COALESCE(rl.clave, 'OTROS'))
            FROM view_riesgo_cartera_mensual v
            LEFT JOIN riesgo_limite rl ON rl.agrupacion = ? AND rl.clave = %s
            WHERE v.mes_corte = ?
        """.formatted(columnClave);

        String sortClause = "";
        if (pageable.getSort().isSorted()) {
            sortClause = " ORDER BY " + pageable.getSort().toString().replace(":", "");
        }
        String pagedQuery = baseQuery + sortClause + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<RiesgoSegmentoProjection> content = jdbcTemplate.query(
            pagedQuery,
            (rs, rowNum) -> new RiesgoSegmentoProjectionImpl(
                rs.getString("idLimite"),
                rs.getString("clave"),
                rs.getString("identificacion"),
                rs.getLong("numeroCreditos"),
                rs.getBigDecimal("carteraVigente"),
                rs.getBigDecimal("carteraVencida"),
                rs.getBigDecimal("carteraTotal"),
                rs.getString("tipoLimite"),
                rs.getBigDecimal("limiteEstablecidoPorcentaje")
            ),
            agrupacion.name(), agrupacion.name(), java.sql.Date.valueOf(mesCorte)
        );

        Long total = jdbcTemplate.queryForObject(
            countQuery,
            Long.class,
            agrupacion.name(), java.sql.Date.valueOf(mesCorte)
        );

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public List<OpcionLimiteDto> obtenerOpcionesDisponiblesPorAgrupacion(AgrupacionRiesgo agrupacion) {
        String columnClave;
        String columnIdentificacion;

        switch (agrupacion) {
            case PRODUCTO -> { columnClave = "CAST(v.numero_producto AS VARCHAR)"; columnIdentificacion = "CAST(v.producto_credito AS VARCHAR)"; }
            case MUNICIPIO -> { columnClave = "UPPER(v.municipio)"; columnIdentificacion = "CAST(v.municipio AS VARCHAR)"; }
            case ESTADO -> { columnClave = "UPPER(v.estado)"; columnIdentificacion = "CAST(v.estado AS VARCHAR)"; }
            case OCUPACION -> { columnClave = "UPPER(v.ocupacion_agrupada)"; columnIdentificacion = "CAST(v.ocupacion_agrupada AS VARCHAR)"; }
            case EDAD -> { columnClave = "CAST(v.intervalo_edad AS VARCHAR)"; columnIdentificacion = "CAST(v.intervalo_edad AS VARCHAR)"; }
            case GENERO -> { columnClave = "UPPER(v.genero)"; columnIdentificacion = "CAST(v.genero AS VARCHAR)"; }
            case SUCURSAL -> { columnClave = "UPPER(v.sucursal)"; columnIdentificacion = "CAST(v.sucursal AS VARCHAR)"; }
            case ACREDITADO -> { columnClave = "UPPER(v.cargo_acreditado_parte_relacionada)"; columnIdentificacion = "CAST(v.cargo_acreditado_parte_relacionada AS VARCHAR)"; }
            case MODALIDAD -> { columnClave = "UPPER(v.modalidad_pago)"; columnIdentificacion = "CAST(v.modalidad_pago AS VARCHAR)"; }
            case TIPO_CLASIFICACION -> { columnClave = "UPPER(v.renovado_reestructurado_normal)"; columnIdentificacion = "CAST(v.renovado_reestructurado_normal AS VARCHAR)"; }
            default -> throw new IllegalArgumentException("Agrupacion invalida");
        }

        String sql = """
            SELECT DISTINCT %s as clave, %s as identificacion
            FROM view_riesgo_cartera_mensual v
            WHERE v.mes_corte = (SELECT MAX(mes_corte) FROM view_riesgo_cartera_mensual)
            AND %s NOT IN (SELECT clave FROM riesgo_limite WHERE agrupacion = ?)
        """.formatted(columnClave, columnIdentificacion, columnClave);

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> new OpcionLimiteDto(rs.getString("clave"), rs.getString("identificacion")),
            agrupacion.name()
        );
    }

    private static class RiesgoSegmentoProjectionImpl implements RiesgoSegmentoProjection {
        private final String idLimite;
        private final String clave;
        private final String identificacion;
        private final Long numeroCreditos;
        private final BigDecimal carteraVigente;
        private final BigDecimal carteraVencida;
        private final BigDecimal carteraTotal;
        private final String tipoLimite;
        private final BigDecimal limiteEstablecidoPorcentaje;

        public RiesgoSegmentoProjectionImpl(String idLimite, String clave, String identificacion, Long numeroCreditos, BigDecimal carteraVigente, BigDecimal carteraVencida, BigDecimal carteraTotal, String tipoLimite, BigDecimal limiteEstablecidoPorcentaje) {
            this.idLimite = idLimite;
            this.clave = clave;
            this.identificacion = identificacion;
            this.numeroCreditos = numeroCreditos;
            this.carteraVigente = carteraVigente;
            this.carteraVencida = carteraVencida;
            this.carteraTotal = carteraTotal;
            this.tipoLimite = tipoLimite;
            this.limiteEstablecidoPorcentaje = limiteEstablecidoPorcentaje;
        }

        @Override public String getIdLimite() { return idLimite; }
        @Override public String getClave() { return clave; }
        @Override public String getIdentificacion() { return identificacion; }
        @Override public Long getNumeroCreditos() { return numeroCreditos; }
        @Override public BigDecimal getCarteraVigente() { return carteraVigente; }
        @Override public BigDecimal getCarteraVencida() { return carteraVencida; }
        @Override public BigDecimal getCarteraTotal() { return carteraTotal; }
        @Override public String getTipoLimite() { return tipoLimite; }
        @Override public BigDecimal getLimiteEstablecidoPorcentaje() { return limiteEstablecidoPorcentaje; }
    }
}
