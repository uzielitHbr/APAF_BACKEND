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
                COALESCE(rl.tipo_limite, rlo.tipo_limite) as "tipoLimite",
                COALESCE(rl.porcentaje_actual, rlo.porcentaje_actual) as "limiteEstablecidoPorcentaje"
            FROM view_riesgo_cartera_mensual v
            LEFT JOIN riesgo_limite rl ON rl.agrupacion = ? AND rl.clave = %s AND rl.activo = true
            LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = ? AND rlo.clave = 'OTROS' AND rl.id_limite IS NULL AND rlo.activo = true
            WHERE v.mes_corte = ?
            GROUP BY
                COALESCE(rl.id_limite, rlo.id_limite),
                COALESCE(rl.clave, 'OTROS'),
                COALESCE(rl.identificacion, 'Otros'),
                COALESCE(rl.tipo_limite, rlo.tipo_limite),
                COALESCE(rl.porcentaje_actual, rlo.porcentaje_actual)
        """.formatted(columnClave);

        String countQuery = """
            SELECT COUNT(DISTINCT COALESCE(rl.clave, 'OTROS'))
            FROM view_riesgo_cartera_mensual v
            LEFT JOIN riesgo_limite rl ON rl.agrupacion = ? AND rl.clave = %s AND rl.activo = true
            WHERE v.mes_corte = ?
        """.formatted(columnClave);

        String sortClause = "";
        if (pageable.getSort().isSorted()) {
            sortClause = " ORDER BY " + pageable.getSort().toString().replace(":", "");
        }
        
        String pagedQuery = baseQuery;
        if (pageable.isPaged()) {
            pagedQuery += sortClause + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        }

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
        List<OpcionLimiteDto> opcionesEstaticas = null;

        switch (agrupacion) {
            case EDAD -> {
                opcionesEstaticas = List.of(
                    new OpcionLimiteDto("1", "18 A 25"),
                    new OpcionLimiteDto("2", "26 A 30"),
                    new OpcionLimiteDto("3", "31 A 35"),
                    new OpcionLimiteDto("4", "36 A 40"),
                    new OpcionLimiteDto("5", "41 A 45"),
                    new OpcionLimiteDto("6", "46 A 50"),
                    new OpcionLimiteDto("7", "51 A 55"),
                    new OpcionLimiteDto("8", "56 A 60"),
                    new OpcionLimiteDto("9", "61 A 65"),
                    new OpcionLimiteDto("10", "66 A 70"),
                    new OpcionLimiteDto("11", "71 A 75"),
                    new OpcionLimiteDto("12", "76 A 80"),
                    new OpcionLimiteDto("13", "81 A 85"),
                    new OpcionLimiteDto("14", "86 A 90")
                );
            }
            case GENERO -> {
                opcionesEstaticas = List.of(
                    new OpcionLimiteDto("MASCULINO", "Masculino"),
                    new OpcionLimiteDto("FEMENINO", "Femenino")
                );
            }
            case TIPO_CLASIFICACION -> {
                opcionesEstaticas = List.of(
                    new OpcionLimiteDto("NUEVO", "Nuevo"),
                    new OpcionLimiteDto("RENOVADO", "Renovado"),
                    new OpcionLimiteDto("REESTRUCTURADO", "Reestructurado")
                );
            }
            case MODALIDAD -> {
                opcionesEstaticas = List.of(
                    new OpcionLimiteDto("PAGOS PERIODICOS DE PRINCIPAL E INTERES", "Pagos periodicos de principal e interes"),
                    new OpcionLimiteDto("PAGO UNICO DE PRINCIPAL E INTERES AL VENCIMIENTO", "Pago unico de principal e interes al vencimiento")
                );
            }
            case PRODUCTO -> {
                opcionesEstaticas = List.of(
                    new OpcionLimiteDto("3101", "Credito Ordinario"),
                    new OpcionLimiteDto("3102", "Credito Automatico"),
                    new OpcionLimiteDto("3103", "Auto-credito"),
                    new OpcionLimiteDto("3104", "Credi Hogar"),
                    new OpcionLimiteDto("3105", "Creditazo"),
                    new OpcionLimiteDto("3107", "Vivienda segura"),
                    new OpcionLimiteDto("3109", "Credito Premier"),
                    new OpcionLimiteDto("3110", "Credito de Confianza"),
                    new OpcionLimiteDto("3129", "Semilla"),
                    new OpcionLimiteDto("3130", "Credito Agropecuario"),
                    new OpcionLimiteDto("3134", "Multi-Credito")
                );
            }
            default -> {
                // Dinámicos
            }
        }

        if (opcionesEstaticas != null) {
            List<OpcionLimiteDto> estaticasConOtros = new java.util.ArrayList<>(opcionesEstaticas);
            estaticasConOtros.add(new OpcionLimiteDto("OTROS", "Otros"));
            
            List<String> clavesOcupadas = jdbcTemplate.query(
                "SELECT clave FROM riesgo_limite WHERE agrupacion = ? AND activo = true",
                (rs, rowNum) -> rs.getString("clave"),
                agrupacion.name()
            );
            return estaticasConOtros.stream()
                    .filter(opt -> !clavesOcupadas.contains(opt.getClave()))
                    .toList();
        }

        // Catálogos dinámicos
        String columnClave;
        String columnIdentificacion;

        switch (agrupacion) {
            case MUNICIPIO -> { columnClave = "UPPER(v.municipio)"; columnIdentificacion = "CAST(v.municipio AS VARCHAR)"; }
            case ESTADO -> { columnClave = "UPPER(v.estado)"; columnIdentificacion = "CAST(v.estado AS VARCHAR)"; }
            case OCUPACION -> { columnClave = "UPPER(v.ocupacion_agrupada)"; columnIdentificacion = "CAST(v.ocupacion_agrupada AS VARCHAR)"; }
            case SUCURSAL -> { columnClave = "UPPER(v.sucursal)"; columnIdentificacion = "CAST(v.sucursal AS VARCHAR)"; }
            case ACREDITADO -> { columnClave = "UPPER(v.cargo_acreditado_parte_relacionada)"; columnIdentificacion = "CAST(v.cargo_acreditado_parte_relacionada AS VARCHAR)"; }
            default -> throw new IllegalArgumentException("Agrupacion invalida para catalogo dinamico");
        }

        String sql = """
            SELECT DISTINCT %s as clave, %s as identificacion
            FROM view_riesgo_cartera_mensual v
            WHERE %s IS NOT NULL AND BTRIM(%s) <> ''
        """.formatted(columnClave, columnIdentificacion, columnClave, columnClave);

        List<OpcionLimiteDto> dinamicas = jdbcTemplate.query(
            sql,
            (rs, rowNum) -> new OpcionLimiteDto(rs.getString("clave"), rs.getString("identificacion"))
        );
        
        // Ensure "OTROS" is available for dynamic catalogs if they want to limit the catch-all bucket
        boolean hasOtros = dinamicas.stream().anyMatch(opt -> "OTROS".equals(opt.getClave()));
        if (!hasOtros) {
            dinamicas.add(new OpcionLimiteDto("OTROS", "Otros"));
        }

        List<String> clavesOcupadas = jdbcTemplate.query(
            "SELECT clave FROM riesgo_limite WHERE agrupacion = ? AND activo = true",
            (rs, rowNum) -> rs.getString("clave"),
            agrupacion.name()
        );

        return dinamicas.stream()
                .filter(opt -> !clavesOcupadas.contains(opt.getClave()))
                .toList();
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
