package app.apaf.backend.domain.cartera.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cartera_datos_calculados")
@Getter
@Setter
@NoArgsConstructor
public class CarteraDatosCalculados {
    @Id
    @Column(name = "id_analisis_mensual")
    private UUID idAnalisisMensual;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_analisis_mensual")
    private CarteraDatos carteraDatos;

    // AW-CQ fields
    @Column(name = "tipo_y_estatus")
    private String tipoYEstatus;

    @Column(name = "cartera_tipo")
    private Short carteraTipo;

    @Column(name = "producto_tipo_cartera_estatus")
    private String productoTipoCarteraEstatus;

    @Column(name = "intervalo_dias_morosidad_y_tipo")
    private String intervaloDiasMorosidadYTipo;

    @Column(name = "intervalo_morosidad_y_tipo_cartera")
    private String intervaloMorosidadYTipoCartera;
    
    @Column(name = "intervalo_morosidad")
    private Short intervaloMorosidad;
    
    @Column(name = "contador")
    private Short contador;
    
    @Column(name = "producto_generado")
    private String productoGenerado;

    @Column(name = "cartera_total")
    private BigDecimal carteraTotal;

    @Column(name = "recuperacion_en_el_mes_capital")
    private BigDecimal recuperacionEnElMesCapital;

    @Column(name = "recuperacion_en_el_mes_intereses")
    private BigDecimal recuperacionEnElMesIntereses;

    @Column(name = "conv_abonos_a_dias")
    private Short convAbonosADias;

    @Column(name = "abonos_restantes_mes_1")
    private Integer abonosRestantesMes1;

    @Column(name = "importe_capital_proyectado_mes_1")
    private BigDecimal importeCapitalProyectadoMes1;

    @Column(name = "interes_devengado_proyectado_mes_1")
    private BigDecimal interesDevengadoProyectadoMes1;

    @Column(name = "abonos_restantes_mes_2")
    private Integer abonosRestantesMes2;

    @Column(name = "importe_capital_proyectado_mes_2")
    private BigDecimal importeCapitalProyectadoMes2;

    @Column(name = "interes_devengado_proyectado_mes_2")
    private BigDecimal interesDevengadoProyectadoMes2;

    @Column(name = "abonos_restantes_mes_3")
    private Integer abonosRestantesMes3;

    @Column(name = "importe_capital_proyectado_mes_3")
    private BigDecimal importeCapitalProyectadoMes3;

    @Column(name = "interes_devengado_proyectado_mes_3")
    private BigDecimal interesDevengadoProyectadoMes3;

    @Column(name = "dias_por_vencer")
    private Short diasPorVencer;

    @Column(name = "intervalo_edad")
    private Short intervaloEdad;

    @Column(name = "numero_producto")
    private String numeroProducto;

    @Column(name = "numero_creditos")
    private Short numeroCreditos;

    @Column(name = "ocupacion_agrupada")
    private String ocupacionAgrupada;

    @Column(name = "estado_municipio")
    private String estadoMunicipio;

    @Column(name = "suc_prod_tasa")
    private String sucProdTasa;

    @Column(name = "sucursal_credito_vigente_vencido")
    private String sucursalCreditoVigenteVencido;

    @Column(name = "origen_socio")
    private String origenSocio;

    @Column(name = "origen_auxiliar")
    private String origenAuxiliar;

    @Column(name = "otorgado_mes_realizo_mov")
    private String otorgadoMesRealizoMov;

    @Column(name = "accion_seguimiento")
    private String accionSeguimiento;

    @Column(name = "cart_riesgo_traspaso_a_vencida")
    private Short cartRiesgoTraspasoAVencida;

    @Column(name = "otorgado_mes_mov_riesgo_cartera_vencida")
    private String otorgadoMesMovRiesgoCarteraVencida;

    @Column(name = "numero_creditos_cartera_vencida")
    private Short numeroCreditosCarteraVencida;

    @Column(name = "otorgado_mes_realizo_mov_sucursal")
    private String otorgadoMesRealizoMovSucursal;

    @Column(name = "cart_riesgo_traspaso_vencida_sucursal")
    private String cartRiesgoTraspasoVencidaSucursal;

    @Column(name = "otorgado_mes_mov_riesgo_vencida_sucursal")
    private String otorgadoMesMovRiesgoVencidaSucursal;

    @Column(name = "nivel_de_riesgo_sic")
    private String nivelDeRiesgoSic;

    @Column(name = "nivel_de_riesgo_sic_vencida")
    private String nivelDeRiesgoSicVencida;

    @Column(name = "nivel_de_riesgo_sic_gestionada")
    private String nivelDeRiesgoSicGestionada;

    @Column(name = "plazo_remanente")
    private Short plazoRemanente;

    @Column(name = "plazo_remanente_sucursal_vigente_vencido")
    private String plazoRemanenteSucursalVigenteVencido;

    @Column(name = "numero_estado_municipio")
    private String numeroEstadoMunicipio;

    @Column(name = "credito_premier_requiere_verificacion_domiciliaria", nullable = false)
    private Boolean creditoPremierRequiereVerificacionDomiciliaria;

    @Column(name = "sucursal_tipo_cartera_estatus")
    private String sucursalTipoCarteraEstatus;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion;
}
