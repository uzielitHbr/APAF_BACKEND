CREATE TABLE eprc_ejecucion (
    id_ejecucion UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mes_corte DATE UNIQUE NOT NULL,
    fecha_ejecucion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_registros BIGINT NOT NULL DEFAULT 0,
    estado VARCHAR(20) NOT NULL CHECK (estado = 'COMPLETADA')
);

CREATE TABLE eprc_estratificacion_detalle (
    id_detalle BIGSERIAL PRIMARY KEY,
    id_ejecucion UUID NOT NULL,
    tipo_cartera VARCHAR(20) NOT NULL CHECK (tipo_cartera IN ('CONSUMO', 'COMERCIAL', 'VIVIENDA')),
    codigo_intervalo VARCHAR(10) NOT NULL,
    intervalo_vencimiento VARCHAR(100) NOT NULL,
    numero_creditos BIGINT NOT NULL DEFAULT 0,
    saldo_capital DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    saldo_interes_vigente DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    saldo_interes_vencido DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    saldo_cartera_total DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    garantia_liquida DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    garantia_hipotecaria DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    eprc_parte_cubierta DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    eprc_parte_expuesta DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    est_prev_intereses_vencidos DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    importe_estimacion_preventiva DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_eprc_detalle_ejecucion FOREIGN KEY (id_ejecucion) REFERENCES eprc_ejecucion (id_ejecucion) ON DELETE CASCADE
);

CREATE TABLE eprc_resumen_global (
    id_resumen UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_ejecucion UUID NOT NULL UNIQUE,
    reservas_requeridas DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    cartera_total_cuadro DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_eprc_resumen_ejecucion FOREIGN KEY (id_ejecucion) REFERENCES eprc_ejecucion (id_ejecucion) ON DELETE CASCADE
);
