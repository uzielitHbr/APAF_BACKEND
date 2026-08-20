package app.apaf.backend.features.risk_management.analysis;

import app.apaf.backend.features.risk_management.domain.AgrupacionRiesgo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import app.apaf.backend.features.risk_management.domain.entity.RiesgoLimiteEntity;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface RiesgoAnalisisReadRepository extends JpaRepository<RiesgoLimiteEntity, UUID> {

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

    @Query(value = """
                SELECT
                    CAST(COALESCE(rl.id_limite, rlo.id_limite) AS VARCHAR) as idLimite,
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS PRODUCTOS' ELSE v.numero_producto END) as clave,
                    COALESCE(rl.identificacion, rlo.identificacion, v.producto_credito) as identificacion,
                    SUM(v.numero_creditos) as numeroCreditos,
                    SUM(v.cartera_vigente) as carteraVigente,
                    SUM(v.cartera_vencida) as carteraVencida,
                    SUM(v.cartera_total) as carteraTotal,
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite) as tipoLimite,
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje) as limiteEstablecidoPorcentaje
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'PRODUCTO' AND rl.clave = v.numero_producto
                LEFT JOIN riesgo_limite_version rlv ON rlv.id_limite = rl.id_limite
                    AND rlv.vigente_desde <= v.fecha_corte AND (rlv.vigente_hasta IS NULL OR rlv.vigente_hasta > v.fecha_corte)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'PRODUCTO' AND rlo.clave = 'OTROS PRODUCTOS' AND rl.id_limite IS NULL
                LEFT JOIN riesgo_limite_version rlo_v ON rlo_v.id_limite = rlo.id_limite
                    AND rlo_v.vigente_desde <= v.fecha_corte AND (rlo_v.vigente_hasta IS NULL OR rlo_v.vigente_hasta > v.fecha_corte)
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
                GROUP BY
                    COALESCE(rl.id_limite, rlo.id_limite),
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS PRODUCTOS' ELSE v.numero_producto END),
                    COALESCE(rl.identificacion, rlo.identificacion, v.producto_credito),
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite),
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje)
            """, countQuery = """
                SELECT COUNT(DISTINCT COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS PRODUCTOS' ELSE v.numero_producto END))
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'PRODUCTO' AND rl.clave = v.numero_producto
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'PRODUCTO' AND rlo.clave = 'OTROS PRODUCTOS' AND rl.id_limite IS NULL
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
            """, nativeQuery = true)
    Page<RiesgoSegmentoProjection> agruparPorProducto(@Param("mesCorte") LocalDate mesCorte, Pageable pageable);

    @Query(value = """
                SELECT
                    CAST(COALESCE(rl.id_limite, rlo.id_limite) AS VARCHAR) as idLimite,
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS MUNICIPIOS' ELSE UPPER(v.municipio) END) as clave,
                    COALESCE(rl.identificacion, rlo.identificacion, v.municipio) as identificacion,
                    SUM(v.numero_creditos) as numeroCreditos,
                    SUM(v.cartera_vigente) as carteraVigente,
                    SUM(v.cartera_vencida) as carteraVencida,
                    SUM(v.cartera_total) as carteraTotal,
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite) as tipoLimite,
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje) as limiteEstablecidoPorcentaje
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'MUNICIPIO' AND rl.clave = UPPER(v.municipio)
                LEFT JOIN riesgo_limite_version rlv ON rlv.id_limite = rl.id_limite
                    AND rlv.vigente_desde <= v.fecha_corte AND (rlv.vigente_hasta IS NULL OR rlv.vigente_hasta > v.fecha_corte)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'MUNICIPIO' AND rlo.clave = 'OTROS MUNICIPIOS' AND rl.id_limite IS NULL
                LEFT JOIN riesgo_limite_version rlo_v ON rlo_v.id_limite = rlo.id_limite
                    AND rlo_v.vigente_desde <= v.fecha_corte AND (rlo_v.vigente_hasta IS NULL OR rlo_v.vigente_hasta > v.fecha_corte)
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
                GROUP BY
                    COALESCE(rl.id_limite, rlo.id_limite),
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS MUNICIPIOS' ELSE UPPER(v.municipio) END),
                    COALESCE(rl.identificacion, rlo.identificacion, v.municipio),
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite),
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje)
            """, countQuery = """
                SELECT COUNT(DISTINCT COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS MUNICIPIOS' ELSE UPPER(v.municipio) END))
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'MUNICIPIO' AND rl.clave = UPPER(v.municipio)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'MUNICIPIO' AND rlo.clave = 'OTROS MUNICIPIOS' AND rl.id_limite IS NULL
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
            """, nativeQuery = true)
    Page<RiesgoSegmentoProjection> agruparPorMunicipio(@Param("mesCorte") LocalDate mesCorte, Pageable pageable);

    @Query(value = """
                SELECT
                    CAST(COALESCE(rl.id_limite, rlo.id_limite) AS VARCHAR) as idLimite,
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS ESTADOS' ELSE UPPER(v.estado) END) as clave,
                    COALESCE(rl.identificacion, rlo.identificacion, v.estado) as identificacion,
                    SUM(v.numero_creditos) as numeroCreditos,
                    SUM(v.cartera_vigente) as carteraVigente,
                    SUM(v.cartera_vencida) as carteraVencida,
                    SUM(v.cartera_total) as carteraTotal,
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite) as tipoLimite,
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje) as limiteEstablecidoPorcentaje
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'ESTADO' AND rl.clave = UPPER(v.estado)
                LEFT JOIN riesgo_limite_version rlv ON rlv.id_limite = rl.id_limite
                    AND rlv.vigente_desde <= v.fecha_corte AND (rlv.vigente_hasta IS NULL OR rlv.vigente_hasta > v.fecha_corte)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'ESTADO' AND rlo.clave = 'OTROS ESTADOS' AND rl.id_limite IS NULL
                LEFT JOIN riesgo_limite_version rlo_v ON rlo_v.id_limite = rlo.id_limite
                    AND rlo_v.vigente_desde <= v.fecha_corte AND (rlo_v.vigente_hasta IS NULL OR rlo_v.vigente_hasta > v.fecha_corte)
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
                GROUP BY
                    COALESCE(rl.id_limite, rlo.id_limite),
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS ESTADOS' ELSE UPPER(v.estado) END),
                    COALESCE(rl.identificacion, rlo.identificacion, v.estado),
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite),
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje)
            """, countQuery = """
                SELECT COUNT(DISTINCT COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS ESTADOS' ELSE UPPER(v.estado) END))
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'ESTADO' AND rl.clave = UPPER(v.estado)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'ESTADO' AND rlo.clave = 'OTROS ESTADOS' AND rl.id_limite IS NULL
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
            """, nativeQuery = true)
    Page<RiesgoSegmentoProjection> agruparPorEstado(@Param("mesCorte") LocalDate mesCorte, Pageable pageable);

    @Query(value = """
                SELECT
                    CAST(COALESCE(rl.id_limite, rlo.id_limite) AS VARCHAR) as idLimite,
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTRAS OCUPACIONES' ELSE UPPER(v.ocupacion_agrupada) END) as clave,
                    COALESCE(rl.identificacion, rlo.identificacion, v.ocupacion_agrupada) as identificacion,
                    SUM(v.numero_creditos) as numeroCreditos,
                    SUM(v.cartera_vigente) as carteraVigente,
                    SUM(v.cartera_vencida) as carteraVencida,
                    SUM(v.cartera_total) as carteraTotal,
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite) as tipoLimite,
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje) as limiteEstablecidoPorcentaje
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'OCUPACION' AND rl.clave = UPPER(v.ocupacion_agrupada)
                LEFT JOIN riesgo_limite_version rlv ON rlv.id_limite = rl.id_limite
                    AND rlv.vigente_desde <= v.fecha_corte AND (rlv.vigente_hasta IS NULL OR rlv.vigente_hasta > v.fecha_corte)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'OCUPACION' AND rlo.clave = 'OTRAS OCUPACIONES' AND rl.id_limite IS NULL
                LEFT JOIN riesgo_limite_version rlo_v ON rlo_v.id_limite = rlo.id_limite
                    AND rlo_v.vigente_desde <= v.fecha_corte AND (rlo_v.vigente_hasta IS NULL OR rlo_v.vigente_hasta > v.fecha_corte)
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
                GROUP BY
                    COALESCE(rl.id_limite, rlo.id_limite),
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTRAS OCUPACIONES' ELSE UPPER(v.ocupacion_agrupada) END),
                    COALESCE(rl.identificacion, rlo.identificacion, v.ocupacion_agrupada),
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite),
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje)
            """, countQuery = """
                SELECT COUNT(DISTINCT COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTRAS OCUPACIONES' ELSE UPPER(v.ocupacion_agrupada) END))
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'OCUPACION' AND rl.clave = UPPER(v.ocupacion_agrupada)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'OCUPACION' AND rlo.clave = 'OTRAS OCUPACIONES' AND rl.id_limite IS NULL
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
            """, nativeQuery = true)
    Page<RiesgoSegmentoProjection> agruparPorOcupacion(@Param("mesCorte") LocalDate mesCorte, Pageable pageable);

    @Query(value = """
                SELECT
                    CAST(COALESCE(rl.id_limite, rlo.id_limite) AS VARCHAR) as idLimite,
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS RANGOS' ELSE CAST(v.intervalo_edad AS VARCHAR) END) as clave,
                    COALESCE(rl.identificacion, rlo.identificacion, CAST(v.intervalo_edad AS VARCHAR)) as identificacion,
                    SUM(v.numero_creditos) as numeroCreditos,
                    SUM(v.cartera_vigente) as carteraVigente,
                    SUM(v.cartera_vencida) as carteraVencida,
                    SUM(v.cartera_total) as carteraTotal,
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite) as tipoLimite,
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje) as limiteEstablecidoPorcentaje
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'EDAD' AND rl.clave = CAST(v.intervalo_edad AS VARCHAR)
                LEFT JOIN riesgo_limite_version rlv ON rlv.id_limite = rl.id_limite
                    AND rlv.vigente_desde <= v.fecha_corte AND (rlv.vigente_hasta IS NULL OR rlv.vigente_hasta > v.fecha_corte)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'EDAD' AND rlo.clave = 'OTROS RANGOS' AND rl.id_limite IS NULL
                LEFT JOIN riesgo_limite_version rlo_v ON rlo_v.id_limite = rlo.id_limite
                    AND rlo_v.vigente_desde <= v.fecha_corte AND (rlo_v.vigente_hasta IS NULL OR rlo_v.vigente_hasta > v.fecha_corte)
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
                GROUP BY
                    COALESCE(rl.id_limite, rlo.id_limite),
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS RANGOS' ELSE CAST(v.intervalo_edad AS VARCHAR) END),
                    COALESCE(rl.identificacion, rlo.identificacion, CAST(v.intervalo_edad AS VARCHAR)),
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite),
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje)
            """, countQuery = """
                SELECT COUNT(DISTINCT COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS RANGOS' ELSE CAST(v.intervalo_edad AS VARCHAR) END))
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'EDAD' AND rl.clave = CAST(v.intervalo_edad AS VARCHAR)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'EDAD' AND rlo.clave = 'OTROS RANGOS' AND rl.id_limite IS NULL
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
            """, nativeQuery = true)
    Page<RiesgoSegmentoProjection> agruparPorEdad(@Param("mesCorte") LocalDate mesCorte, Pageable pageable);

    @Query(value = """
                SELECT
                    CAST(COALESCE(rl.id_limite, rlo.id_limite) AS VARCHAR) as idLimite,
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS GENEROS' ELSE UPPER(v.genero) END) as clave,
                    COALESCE(rl.identificacion, rlo.identificacion, v.genero) as identificacion,
                    SUM(v.numero_creditos) as numeroCreditos,
                    SUM(v.cartera_vigente) as carteraVigente,
                    SUM(v.cartera_vencida) as carteraVencida,
                    SUM(v.cartera_total) as carteraTotal,
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite) as tipoLimite,
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje) as limiteEstablecidoPorcentaje
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'GENERO' AND rl.clave = UPPER(v.genero)
                LEFT JOIN riesgo_limite_version rlv ON rlv.id_limite = rl.id_limite
                    AND rlv.vigente_desde <= v.fecha_corte AND (rlv.vigente_hasta IS NULL OR rlv.vigente_hasta > v.fecha_corte)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'GENERO' AND rlo.clave = 'OTROS GENEROS' AND rl.id_limite IS NULL
                LEFT JOIN riesgo_limite_version rlo_v ON rlo_v.id_limite = rlo.id_limite
                    AND rlo_v.vigente_desde <= v.fecha_corte AND (rlo_v.vigente_hasta IS NULL OR rlo_v.vigente_hasta > v.fecha_corte)
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
                GROUP BY
                    COALESCE(rl.id_limite, rlo.id_limite),
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS GENEROS' ELSE UPPER(v.genero) END),
                    COALESCE(rl.identificacion, rlo.identificacion, v.genero),
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite),
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje)
            """, countQuery = """
                SELECT COUNT(DISTINCT COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS GENEROS' ELSE UPPER(v.genero) END))
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'GENERO' AND rl.clave = UPPER(v.genero)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'GENERO' AND rlo.clave = 'OTROS GENEROS' AND rl.id_limite IS NULL
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
            """, nativeQuery = true)
    Page<RiesgoSegmentoProjection> agruparPorGenero(@Param("mesCorte") LocalDate mesCorte, Pageable pageable);

    @Query(value = """
                SELECT
                    CAST(COALESCE(rl.id_limite, rlo.id_limite) AS VARCHAR) as idLimite,
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTRAS SUCURSALES' ELSE UPPER(v.sucursal) END) as clave,
                    COALESCE(rl.identificacion, rlo.identificacion, v.sucursal) as identificacion,
                    SUM(v.numero_creditos) as numeroCreditos,
                    SUM(v.cartera_vigente) as carteraVigente,
                    SUM(v.cartera_vencida) as carteraVencida,
                    SUM(v.cartera_total) as carteraTotal,
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite) as tipoLimite,
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje) as limiteEstablecidoPorcentaje
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'SUCURSAL' AND rl.clave = UPPER(v.sucursal)
                LEFT JOIN riesgo_limite_version rlv ON rlv.id_limite = rl.id_limite
                    AND rlv.vigente_desde <= v.fecha_corte AND (rlv.vigente_hasta IS NULL OR rlv.vigente_hasta > v.fecha_corte)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'SUCURSAL' AND rlo.clave = 'OTRAS SUCURSALES' AND rl.id_limite IS NULL
                LEFT JOIN riesgo_limite_version rlo_v ON rlo_v.id_limite = rlo.id_limite
                    AND rlo_v.vigente_desde <= v.fecha_corte AND (rlo_v.vigente_hasta IS NULL OR rlo_v.vigente_hasta > v.fecha_corte)
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
                GROUP BY
                    COALESCE(rl.id_limite, rlo.id_limite),
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTRAS SUCURSALES' ELSE UPPER(v.sucursal) END),
                    COALESCE(rl.identificacion, rlo.identificacion, v.sucursal),
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite),
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje)
            """, countQuery = """
                SELECT COUNT(DISTINCT COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTRAS SUCURSALES' ELSE UPPER(v.sucursal) END))
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'SUCURSAL' AND rl.clave = UPPER(v.sucursal)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'SUCURSAL' AND rlo.clave = 'OTRAS SUCURSALES' AND rl.id_limite IS NULL
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
            """, nativeQuery = true)
    Page<RiesgoSegmentoProjection> agruparPorSucursal(@Param("mesCorte") LocalDate mesCorte, Pageable pageable);

    @Query(value = """
                SELECT
                    CAST(COALESCE(rl.id_limite, rlo.id_limite) AS VARCHAR) as idLimite,
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS ACREDITADOS' ELSE UPPER(v.cargo_acreditado_parte_relacionada) END) as clave,
                    COALESCE(rl.identificacion, rlo.identificacion, v.cargo_acreditado_parte_relacionada) as identificacion,
                    SUM(v.numero_creditos) as numeroCreditos,
                    SUM(v.cartera_vigente) as carteraVigente,
                    SUM(v.cartera_vencida) as carteraVencida,
                    SUM(v.cartera_total) as carteraTotal,
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite) as tipoLimite,
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje) as limiteEstablecidoPorcentaje
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'ACREDITADO' AND rl.clave = UPPER(v.cargo_acreditado_parte_relacionada)
                LEFT JOIN riesgo_limite_version rlv ON rlv.id_limite = rl.id_limite
                    AND rlv.vigente_desde <= v.fecha_corte AND (rlv.vigente_hasta IS NULL OR rlv.vigente_hasta > v.fecha_corte)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'ACREDITADO' AND rlo.clave = 'OTROS ACREDITADOS' AND rl.id_limite IS NULL
                LEFT JOIN riesgo_limite_version rlo_v ON rlo_v.id_limite = rlo.id_limite
                    AND rlo_v.vigente_desde <= v.fecha_corte AND (rlo_v.vigente_hasta IS NULL OR rlo_v.vigente_hasta > v.fecha_corte)
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
                GROUP BY
                    COALESCE(rl.id_limite, rlo.id_limite),
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS ACREDITADOS' ELSE UPPER(v.cargo_acreditado_parte_relacionada) END),
                    COALESCE(rl.identificacion, rlo.identificacion, v.cargo_acreditado_parte_relacionada),
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite),
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje)
            """, countQuery = """
                SELECT COUNT(DISTINCT COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTROS ACREDITADOS' ELSE UPPER(v.cargo_acreditado_parte_relacionada) END))
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'ACREDITADO' AND rl.clave = UPPER(v.cargo_acreditado_parte_relacionada)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'ACREDITADO' AND rlo.clave = 'OTROS ACREDITADOS' AND rl.id_limite IS NULL
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
            """, nativeQuery = true)
    Page<RiesgoSegmentoProjection> agruparPorAcreditado(@Param("mesCorte") LocalDate mesCorte, Pageable pageable);

    @Query(value = """
                SELECT
                    CAST(COALESCE(rl.id_limite, rlo.id_limite) AS VARCHAR) as idLimite,
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTRAS MODALIDADES' ELSE UPPER(v.modalidad_pago) END) as clave,
                    COALESCE(rl.identificacion, rlo.identificacion, v.modalidad_pago) as identificacion,
                    SUM(v.numero_creditos) as numeroCreditos,
                    SUM(v.cartera_vigente) as carteraVigente,
                    SUM(v.cartera_vencida) as carteraVencida,
                    SUM(v.cartera_total) as carteraTotal,
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite) as tipoLimite,
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje) as limiteEstablecidoPorcentaje
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'MODALIDAD' AND rl.clave = UPPER(v.modalidad_pago)
                LEFT JOIN riesgo_limite_version rlv ON rlv.id_limite = rl.id_limite
                    AND rlv.vigente_desde <= v.fecha_corte AND (rlv.vigente_hasta IS NULL OR rlv.vigente_hasta > v.fecha_corte)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'MODALIDAD' AND rlo.clave = 'OTRAS MODALIDADES' AND rl.id_limite IS NULL
                LEFT JOIN riesgo_limite_version rlo_v ON rlo_v.id_limite = rlo.id_limite
                    AND rlo_v.vigente_desde <= v.fecha_corte AND (rlo_v.vigente_hasta IS NULL OR rlo_v.vigente_hasta > v.fecha_corte)
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
                GROUP BY
                    COALESCE(rl.id_limite, rlo.id_limite),
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTRAS MODALIDADES' ELSE UPPER(v.modalidad_pago) END),
                    COALESCE(rl.identificacion, rlo.identificacion, v.modalidad_pago),
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite),
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje)
            """, countQuery = """
                SELECT COUNT(DISTINCT COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTRAS MODALIDADES' ELSE UPPER(v.modalidad_pago) END))
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'MODALIDAD' AND rl.clave = UPPER(v.modalidad_pago)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'MODALIDAD' AND rlo.clave = 'OTRAS MODALIDADES' AND rl.id_limite IS NULL
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
            """, nativeQuery = true)
    Page<RiesgoSegmentoProjection> agruparPorModalidad(@Param("mesCorte") LocalDate mesCorte, Pageable pageable);

    @Query(value = """
                SELECT
                    CAST(COALESCE(rl.id_limite, rlo.id_limite) AS VARCHAR) as idLimite,
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTRAS CLASIFICACIONES' ELSE UPPER(v.renovado_reestructurado_normal) END) as clave,
                    COALESCE(rl.identificacion, rlo.identificacion, v.renovado_reestructurado_normal) as identificacion,
                    SUM(v.numero_creditos) as numeroCreditos,
                    SUM(v.cartera_vigente) as carteraVigente,
                    SUM(v.cartera_vencida) as carteraVencida,
                    SUM(v.cartera_total) as carteraTotal,
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite) as tipoLimite,
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje) as limiteEstablecidoPorcentaje
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'TIPO_CLASIFICACION' AND rl.clave = UPPER(v.renovado_reestructurado_normal)
                LEFT JOIN riesgo_limite_version rlv ON rlv.id_limite = rl.id_limite
                    AND rlv.vigente_desde <= v.fecha_corte AND (rlv.vigente_hasta IS NULL OR rlv.vigente_hasta > v.fecha_corte)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'TIPO_CLASIFICACION' AND rlo.clave = 'OTRAS CLASIFICACIONES' AND rl.id_limite IS NULL
                LEFT JOIN riesgo_limite_version rlo_v ON rlo_v.id_limite = rlo.id_limite
                    AND rlo_v.vigente_desde <= v.fecha_corte AND (rlo_v.vigente_hasta IS NULL OR rlo_v.vigente_hasta > v.fecha_corte)
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
                GROUP BY
                    COALESCE(rl.id_limite, rlo.id_limite),
                    COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTRAS CLASIFICACIONES' ELSE UPPER(v.renovado_reestructurado_normal) END),
                    COALESCE(rl.identificacion, rlo.identificacion, v.renovado_reestructurado_normal),
                    COALESCE(rlv.tipo_limite, rlo_v.tipo_limite),
                    COALESCE(rlv.limite_porcentaje, rlo_v.limite_porcentaje)
            """, countQuery = """
                SELECT COUNT(DISTINCT COALESCE(rl.clave, CASE WHEN rlo.id_limite IS NOT NULL THEN 'OTRAS CLASIFICACIONES' ELSE UPPER(v.renovado_reestructurado_normal) END))
                FROM view_riesgo_cartera_mensual v
                LEFT JOIN riesgo_limite rl ON rl.agrupacion = 'TIPO_CLASIFICACION' AND rl.clave = UPPER(v.renovado_reestructurado_normal)
                LEFT JOIN riesgo_limite rlo ON rlo.agrupacion = 'TIPO_CLASIFICACION' AND rlo.clave = 'OTRAS CLASIFICACIONES' AND rl.id_limite IS NULL
                WHERE v.mes_corte = CAST(:mesCorte AS DATE)
            """, nativeQuery = true)
    Page<RiesgoSegmentoProjection> agruparPorTipoClasificacion(@Param("mesCorte") LocalDate mesCorte,
            Pageable pageable);

    default Page<RiesgoSegmentoProjection> obtenerAnalisisPorAgrupacion(AgrupacionRiesgo agrupacion, LocalDate mesCorte,
            Pageable pageable) {
        return switch (agrupacion) {
            case PRODUCTO -> agruparPorProducto(mesCorte, pageable);
            case MUNICIPIO -> agruparPorMunicipio(mesCorte, pageable);
            case ESTADO -> agruparPorEstado(mesCorte, pageable);
            case OCUPACION -> agruparPorOcupacion(mesCorte, pageable);
            case EDAD -> agruparPorEdad(mesCorte, pageable);
            case GENERO -> agruparPorGenero(mesCorte, pageable);
            case SUCURSAL -> agruparPorSucursal(mesCorte, pageable);
            case ACREDITADO -> agruparPorAcreditado(mesCorte, pageable);
            case MODALIDAD -> agruparPorModalidad(mesCorte, pageable);
            case TIPO_CLASIFICACION -> agruparPorTipoClasificacion(mesCorte, pageable);
        };
    }
}
