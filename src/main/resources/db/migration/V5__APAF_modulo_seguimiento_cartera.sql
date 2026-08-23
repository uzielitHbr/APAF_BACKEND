CREATE TABLE seguimiento_ejecucion (
    id_ejecucion UUID PRIMARY KEY,
    mes_corte DATE UNIQUE NOT NULL,
    fecha_ejecucion TIMESTAMP NOT NULL
);

CREATE TABLE seguimiento_saldo (
    id_saldo UUID PRIMARY KEY,
    mes_corte DATE NOT NULL,
    sucursal VARCHAR(255) NOT NULL,
    numero_creditos INT NOT NULL,
    capital_vigente NUMERIC(19, 4) NOT NULL,
    interes_ord_vigente NUMERIC(19, 4) NOT NULL,
    capital_vencido NUMERIC(19, 4) NOT NULL,
    interes_ord_vencido NUMERIC(19, 4) NOT NULL,
    cuentas_orden NUMERIC(19, 4) NOT NULL,
    saldo_total NUMERIC(19, 4) NOT NULL,
    creditos_con_movimiento INT NOT NULL,
    creditos_sin_movimiento INT NOT NULL,
    creditos_otorgados_mes INT NOT NULL,
    imor_general NUMERIC(19, 4) NOT NULL,
    proporcion_cartera NUMERIC(19, 4) NOT NULL,
    imor_sucursal NUMERIC(19, 4) NOT NULL,
    imor_proyectado NUMERIC(19, 4) NOT NULL,
    es_total BOOLEAN NOT NULL
);

CREATE TABLE seguimiento_morosidad (
    id_morosidad UUID PRIMARY KEY,
    mes_corte DATE NOT NULL,
    sucursal VARCHAR(255) NOT NULL,
    rango_mora VARCHAR(20) NOT NULL,
    numero_creditos INT NOT NULL,
    capital_vigente NUMERIC(19, 4) NOT NULL,
    interes_ord_vigente NUMERIC(19, 4) NOT NULL,
    capital_vencido NUMERIC(19, 4) NOT NULL,
    interes_ord_vencido NUMERIC(19, 4) NOT NULL,
    cuentas_orden NUMERIC(19, 4) NOT NULL,
    saldo_total NUMERIC(19, 4) NOT NULL,
    creditos_con_movimiento INT NOT NULL,
    creditos_sin_movimiento INT NOT NULL,
    creditos_otorgados_mes INT NOT NULL
);

CREATE TABLE seguimiento_plazo (
    id_plazo UUID PRIMARY KEY,
    mes_corte DATE NOT NULL,
    sucursal VARCHAR(255),
    tipo_vista VARCHAR(20) NOT NULL,
    plazo_remanente VARCHAR(50) NOT NULL,
    numero_creditos INT NOT NULL,
    capital_vigente NUMERIC(19, 4) NOT NULL,
    interes_ord_vigente NUMERIC(19, 4) NOT NULL,
    capital_vencido NUMERIC(19, 4) NOT NULL,
    interes_ord_vencido NUMERIC(19, 4) NOT NULL,
    cuentas_orden NUMERIC(19, 4) NOT NULL,
    saldo_total NUMERIC(19, 4) NOT NULL,
    creditos_con_movimiento INT NOT NULL,
    creditos_sin_movimiento INT NOT NULL,
    creditos_otorgados_mes INT NOT NULL,
    imor NUMERIC(19, 4) NOT NULL,
    proporcion NUMERIC(19, 4) NOT NULL
);
