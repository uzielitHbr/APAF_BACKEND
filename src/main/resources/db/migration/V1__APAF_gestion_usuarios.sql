
CREATE EXTENSION IF NOT EXISTS pgcrypto;

/*
 ROLES TABLA
 */
CREATE TABLE rol (
id_rol BIGSERIAL PRIMARY KEY,
codigo_rol VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO rol (codigo_rol) VALUES
('ADMIN'),
('RIESGOS'),
('ANALISTA')
ON CONFLICT (codigo_rol) DO NOTHING;


/*
 TABLA USUARIOS
 */
CREATE TABLE usuarios (
id_usuario BIGSERIAL PRIMARY KEY,
nombre_completo VARCHAR(150) NOT NULL,
correo VARCHAR(100) NOT NULL UNIQUE,
contrasenia VARCHAR(255),
telefono VARCHAR(15) NOT NULL,

estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
    CHECK (estado IN ('PENDIENTE', 'ACTIVO', 'INACTIVO')),

 id_rol BIGINT NOT NULL,
intentos_fallidos INT DEFAULT 0,
cuenta_bloqueada BOOLEAN DEFAULT FALSE,
tiempo_bloqueado TIMESTAMP,

fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
fecha_baja TIMESTAMP,

token_recuperacion VARCHAR(255) UNIQUE,
expiracion_token TIMESTAMP,
creado_por BIGINT,
CONSTRAINT fk_usuario_rol
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol),

CONSTRAINT fk_usuario_creado_por
    FOREIGN KEY (creado_por) REFERENCES usuarios(id_usuario) ON DELETE SET NULL
);


/*
 CARTERA
 Cada fila representa un contrato
 */
CREATE TABLE cartera_analisis_mensual (
id_analisis_mensual UUID PRIMARY KEY DEFAULT gen_random_uuid(),

 mes_corte DATE NOT NULL,
fecha_corte DATE NOT NULL,

/*
 Datos
 */
numero_socio VARCHAR(50) NOT NULL,
nombre_acreditado VARCHAR(255) NOT NULL,
genero VARCHAR(50),
fecha_nacimiento DATE,
edad INT,
ocupacion VARCHAR(150),
localidad VARCHAR(150),


municipio VARCHAR(150),
estado VARCHAR(100),

/*
 Credito
 */
no_contrato VARCHAR(50) NOT NULL,
sucursal VARCHAR(100) NOT NULL,
clasificacion_credito VARCHAR(100),
producto_credito VARCHAR(100),
modalidad_pago VARCHAR(50),
fecha_otorgamiento DATE,
fecha_vencimiento DATE,
monto_original NUMERIC(18, 2),
tasa_ordinaria_nominal_anual NUMERIC(10, 6),
tasa_moratoria_nominal_anual NUMERIC(10, 6),
plazo_credito_meses INT,
frecuencia_pago_capital VARCHAR(50),
frecuencia_pago_intereses VARCHAR(50),
tipo_cartera_calificacion VARCHAR(100),
finalidad_credito VARCHAR(255),
cce VARCHAR(100),

/*
 Saldo y morosidad
 */
dias_mora INT DEFAULT 0,
vigente_o_vencido VARCHAR(50),
capital_vigente NUMERIC(18, 2) DEFAULT 0.00,
capital_vencido NUMERIC(18, 2) DEFAULT 0.00,
int_dev_no_cobrados_vigentes NUMERIC(18, 2) DEFAULT 0.00,
int_dev_no_cobrados_vencidos NUMERIC(18, 2) DEFAULT 0.00,
int_dev_no_cobrados_ctas_orden NUMERIC(18, 2) DEFAULT 0.00,

/*
 Historiales por mes
 */
fecha_ultimo_pago_capital DATE,
monto_ultimo_pago_capital NUMERIC(18, 2) DEFAULT 0.00,
fecha_ultimo_pago_intereses DATE,
monto_ultimo_pago_intereses NUMERIC(18, 2) DEFAULT 0.00,

/*
 Estatus y riesgos
 */
renovado_reestructurado_normal VARCHAR(50),
emproblemado VARCHAR(50),
cargo_acreditado_parte_relacionada VARCHAR(100),

/*
 Garantias y EPRC
 */
monto_garantia_liquida NUMERIC(18, 2) DEFAULT 0.00,
cuenta_garantia_liquida VARCHAR(100),
monto_garantia_prendaria NUMERIC(18, 2) DEFAULT 0.00,
monto_garantia_hipotecaria NUMERIC(18, 2) DEFAULT 0.00,
eprc_contable_parte_cubierta NUMERIC(18, 2) DEFAULT 0.00,
eprc_contable_parte_expuesta NUMERIC(18, 2) DEFAULT 0.00,
eprc_contable_x_intereses_cee NUMERIC(18, 2) DEFAULT 0.00,
importe_estimacion_adicional NUMERIC(18, 2) DEFAULT 0.00,

/*
 DATOS GENERADOS POR EL SISTEMA
 */
tipo_y_estatus VARCHAR(150),
cartera_tipo INT,
cartera_vencida_por_producto VARCHAR(150),

 intervalo_dias_morosidad_y_tipo VARCHAR(100),
intervalo_o_morosidad INT,
intervalo_morosidad_y_tipo_cartera VARCHAR(150),

cartera_total NUMERIC(18, 2) DEFAULT 0.00,
recuperacion_en_el_mes_capital NUMERIC(18, 2) DEFAULT 0.00,
recuperacion_en_el_mes_intereses NUMERIC(18, 2) DEFAULT 0.00,

conv_abonos_a_dias INT,

    -- Proyección mes 1
abonos_restantes_mes_1 INT,
importe_capital_proyectado_mes_1 NUMERIC(18, 2) DEFAULT 0.00,
interes_devengado_proyectado_mes_1 NUMERIC(18, 2) DEFAULT 0.00,

    -- Proyección mes 2
abonos_restantes_mes_2 INT,
importe_capital_proyectado_mes_2 NUMERIC(18, 2) DEFAULT 0.00,
interes_devengado_proyectado_mes_2 NUMERIC(18, 2) DEFAULT 0.00,

    -- Proyección mes 3
abonos_restantes_mes_3 INT,
importe_capital_proyectado_mes_3 NUMERIC(18, 2) DEFAULT 0.00,
interes_devengado_proyectado_mes_3 NUMERIC(18, 2) DEFAULT 0.00,

intervalo_edad INT,
numero_producto VARCHAR(50),
numero_creditos INT,

ocupacion_concatenada VARCHAR(150),
estado_municipio VARCHAR(200),
contador INT,

suc_prod_tasa VARCHAR(200),
sucursal_credito_vencido VARCHAR(200),
origen_socio VARCHAR(100),
origen_auxiliar VARCHAR(100),
otorgado_mes_realizo_mov VARCHAR(150),
accion_seguimiento VARCHAR(250),

cart_riesgo_traspaso_a_vencida INT,
num_creditos_en_cartera_vencida INT,

otorgado_mes_realizo_mov_suc VARCHAR(200),
cart_riesgo_traspaso_a_vencida_suc VARCHAR(200),

nivel_de_riesgo_sic VARCHAR(100),
nivel_de_riesgo_sic_vencida VARCHAR(100),
nivel_de_riesgo_sic_gestionada VARCHAR(100),

plazo_remanente INT,
plazo_remanente_sucursal_vigente_vencido VARCHAR(200),

producto_generado VARCHAR(100),
estado_generado VARCHAR(100),

cred_premier_verif_domiciliaria BOOLEAN DEFAULT FALSE,
tipo_y_estatus_sucursal VARCHAR(200),


fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,


    -- REGLAS
    -- No se puede repetir el mismo contrato dentro del mismo mes.
CONSTRAINT uk_cartera_mes_contrato
    UNIQUE (mes_corte, no_contrato),

    -- mes_corte siempre debe ser el primer día del mes.
CONSTRAINT chk_mes_corte_inicio_mes
    CHECK (mes_corte = DATE_TRUNC('month', mes_corte)::DATE),

    -- fecha_corte debe ser el último día del mismo mes.
CONSTRAINT chk_fecha_corte_fin_mes
    CHECK (fecha_corte = (mes_corte + INTERVAL '1 month' - INTERVAL '1 day')::DATE )
);


-- TRIGGER PARA ACTUALIZAR fecha_actualizacion
CREATE OR REPLACE FUNCTION fn_actualizar_fecha_actualizacion()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_actualizar_fecha_actualizacion_usuario
    BEFORE UPDATE ON usuarios
    FOR EACH ROW
EXECUTE FUNCTION fn_actualizar_fecha_actualizacion();


CREATE TRIGGER trg_actualizar_fecha_actualizacion_cartera
    BEFORE UPDATE ON cartera_analisis_mensual
    FOR EACH ROW
EXECUTE FUNCTION fn_actualizar_fecha_actualizacion();


-- Indices de usuarios


CREATE INDEX idx_usuarios_id_rol
    ON usuarios(id_rol);

CREATE INDEX idx_usuarios_estado
    ON usuarios(estado);
/*
/*
Cartera
 */

-- Filtro principal por mes
CREATE INDEX idx_cartera_mes
    ON cartera_analisis_mensual(mes_corte);

-- Filtros comunes por mes
CREATE INDEX idx_cartera_mes_sucursal
    ON cartera_analisis_mensual(mes_corte, sucursal);

CREATE INDEX idx_cartera_mes_estatus
    ON cartera_analisis_mensual(mes_corte, vigente_o_vencido);

CREATE INDEX idx_cartera_mes_producto
    ON cartera_analisis_mensual(mes_corte, producto_credito);

CREATE INDEX idx_cartera_mes_ocupacion
    ON cartera_analisis_mensual(mes_corte, ocupacion);

CREATE INDEX idx_cartera_mes_riesgo
    ON cartera_analisis_mensual(mes_corte, nivel_de_riesgo_sic);

CREATE INDEX idx_cartera_mes_riesgo_vencida
    ON cartera_analisis_mensual(mes_corte, nivel_de_riesgo_sic_vencida);

CREATE INDEX idx_cartera_mes_riesgo_gestionada
    ON cartera_analisis_mensual(mes_corte, nivel_de_riesgo_sic_gestionada);

CREATE INDEX idx_cartera_mes_clasificacion
    ON cartera_analisis_mensual(mes_corte, clasificacion_credito);

CREATE INDEX idx_cartera_mes_municipio
    ON cartera_analisis_mensual(mes_corte, municipio);

CREATE INDEX idx_cartera_mes_estado
    ON cartera_analisis_mensual(mes_corte, estado);

 -- Búsquedas directas

 CREATE INDEX idx_cartera_numero_socio
    ON cartera_analisis_mensual(numero_socio);

CREATE INDEX idx_cartera_no_contrato
    ON cartera_analisis_mensual(no_contrato);

CREATE INDEX idx_cartera_nombre_acreditado
    ON cartera_analisis_mensual(nombre_acreditado);

-- Dashboard específico: vencidos por sucursal y ocupación
CREATE INDEX idx_cartera_mes_vencido_sucursal_ocupacion
    ON cartera_analisis_mensual(mes_corte, vigente_o_vencido, sucursal, ocupacion);
 */

/*
 Meses disponibles
 */
CREATE OR REPLACE VIEW vw_meses_cartera_disponibles AS
SELECT DISTINCT
    mes_corte,
    fecha_corte
FROM cartera_analisis_mensual
ORDER BY mes_corte DESC;


