



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



-- Índices para optimizar el login y seguridad
CREATE INDEX IF NOT EXISTS index_usuarios_id_rol ON usuarios(id_rol);
CREATE INDEX IF NOT EXISTS index_usuarios_estado ON usuarios(estado);




CREATE TABLE cartera_datos (
id_analisis_mensual UUID PRIMARY KEY DEFAULT gen_random_uuid(),

mes_corte DATE NOT NULL,
fecha_corte DATE NOT NULL,

nombre_acreditado VARCHAR(255),
numero_socio VARCHAR(50) NOT NULL,
numero_contrato VARCHAR(50) NOT NULL,
sucursal VARCHAR(100) NOT NULL,
clasificacion_credito VARCHAR(100),
producto_credito VARCHAR(100),
modalidad_pago VARCHAR(150),
fecha_otorgamiento DATE,
monto_original NUMERIC(18, 2),
fecha_vencimiento DATE,
tasa_ordinaria_nominal_anual NUMERIC(10, 6),
tasa_moratoria_nominal_anual NUMERIC(10, 6),
plazo_credito_meses INTEGER,
frecuencia_pago_capital VARCHAR(50),
frecuencia_pago_intereses VARCHAR(50),

dias_mora INTEGER NOT NULL DEFAULT 0,
capital_vigente NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
capital_vencido NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
int_dev_no_cobrados_vigentes NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
int_dev_no_cobrados_vencidos NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
int_dev_no_cobrados_ctas_orden NUMERIC(18, 2) NOT NULL DEFAULT 0.00,

fecha_ultimo_pago_capital DATE,
monto_ultimo_pago_capital NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
fecha_ultimo_pago_intereses DATE,
monto_ultimo_pago_intereses NUMERIC(18, 2) NOT NULL DEFAULT 0.00,

renovado_reestructurado_normal VARCHAR(50),
emproblemado BOOLEAN NOT NULL DEFAULT FALSE,
vigente_o_vencido VARCHAR(50),
cargo_acreditado_parte_relacionada VARCHAR(100),

monto_garantia_liquida NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
cuenta_garantia_liquida VARCHAR(100),
monto_garantia_prendaria NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
monto_garantia_hipotecaria NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
eprc_contable_parte_cubierta NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
eprc_contable_parte_expuesta NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
eprc_contable_x_intereses_cee NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
importe_estimacion_adicional NUMERIC(18, 2) NOT NULL DEFAULT 0.00,

localidad VARCHAR(150),
estado VARCHAR(100),
ocupacion VARCHAR(150),
municipio VARCHAR(150),
genero VARCHAR(50),
fecha_nacimiento DATE,
edad SMALLINT,
tipo_cartera_calificacion VARCHAR(100),
finalidad_credito VARCHAR(255),
cce VARCHAR(100),

fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

 CONSTRAINT uk_cartera_mes_contrato
     UNIQUE (mes_corte, numero_contrato),

CONSTRAINT chk_mes_corte_inicio_mes
    CHECK ( mes_corte = DATE_TRUNC('month', mes_corte)::DATE
                                       ),

CONSTRAINT chk_fecha_corte_fin_mes
    CHECK (fecha_corte =(
mes_corte + INTERVAL '1 month' - INTERVAL '1 day' )::DATE ),

 CONSTRAINT chk_cartera_dias_mora
     CHECK (dias_mora >= 0),

CONSTRAINT chk_cartera_edad
    CHECK (edad IS NULL  OR edad BETWEEN 0 AND 130
                                       ),

CONSTRAINT chk_cartera_plazo_credito
    CHECK ( plazo_credito_meses IS NULL OR plazo_credito_meses >= 0 )
);

/*
 Trigger para actualizar fechas
 */
CREATE OR REPLACE FUNCTION fn_actualizar_fecha_actualizacion()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


DROP TRIGGER IF EXISTS
    trigger_actualizar_fecha_actualizacion_cartera
    ON cartera_datos;

CREATE TRIGGER trigger_actualizar_fecha_actualizacion_cartera
    BEFORE UPDATE ON cartera_datos
    FOR EACH ROW
EXECUTE FUNCTION fn_actualizar_fecha_actualizacion();


DROP TRIGGER IF EXISTS
    trigger_actualizar_fecha_actualizacion_calculados
    ON cartera_datos_calculados;

CREATE TRIGGER trigger_actualizar_fecha_actualizacion_calculados
    BEFORE UPDATE ON cartera_datos_calculados
    FOR EACH ROW
EXECUTE FUNCTION fn_actualizar_fecha_actualizacion();





CREATE TABLE cartera_datos_calculados (
id_analisis_mensual UUID PRIMARY KEY,

tipo_y_estatus VARCHAR(150),
cartera_tipo SMALLINT,
producto_tipo_cartera_estatus VARCHAR(150),
intervalo_dias_morosidad_y_tipo VARCHAR(100),
intervalo_morosidad_y_tipo_cartera VARCHAR(20),

cartera_total NUMERIC(18, 2),
recuperacion_en_el_mes_capital NUMERIC(18, 2),
recuperacion_en_el_mes_intereses NUMERIC(18, 2),
conv_abonos_a_dias SMALLINT,

abonos_restantes_mes_1 INTEGER,
importe_capital_proyectado_mes_1 NUMERIC(18, 2),
interes_devengado_proyectado_mes_1 NUMERIC(18, 2),
abonos_restantes_mes_2 INTEGER,
importe_capital_proyectado_mes_2 NUMERIC(18, 2),
interes_devengado_proyectado_mes_2 NUMERIC(18, 2),

abonos_restantes_mes_3 INTEGER,
importe_capital_proyectado_mes_3 NUMERIC(18, 2),
interes_devengado_proyectado_mes_3 NUMERIC(18, 2),

dias_por_vencer SMALLINT,
intervalo_edad SMALLINT,
numero_producto VARCHAR(50),
numero_creditos SMALLINT,
ocupacion_agrupada VARCHAR(150),
estado_municipio VARCHAR(300),

suc_prod_tasa VARCHAR(200),
sucursal_credito_vigente_vencido VARCHAR(250),
origen_socio VARCHAR(100),
origen_auxiliar VARCHAR(100),
otorgado_mes_realizo_mov VARCHAR(50),
accion_seguimiento VARCHAR(250),

cart_riesgo_traspaso_a_vencida SMALLINT,
otorgado_mes_mov_riesgo_cartera_vencida VARCHAR(100),
numero_creditos_cartera_vencida SMALLINT,
otorgado_mes_realizo_mov_sucursal VARCHAR(200),
cart_riesgo_traspaso_vencida_sucursal VARCHAR(200),
otorgado_mes_mov_riesgo_vencida_sucursal VARCHAR(250),

nivel_de_riesgo_sic VARCHAR(100),
nivel_de_riesgo_sic_vencida VARCHAR(100),
nivel_de_riesgo_sic_gestionada VARCHAR(100),

plazo_remanente SMALLINT,
plazo_remanente_sucursal_vigente_vencido VARCHAR(200),
numero_estado_municipio VARCHAR(150),
credito_premier_requiere_verificacion_domiciliaria BOOLEAN NOT NULL DEFAULT FALSE,
sucursal_tipo_cartera_estatus VARCHAR(200),

fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

 CONSTRAINT fk_calculados_cartera_base
     FOREIGN KEY (id_analisis_mensual) REFERENCES cartera_datos(id_analisis_mensual) ON DELETE CASCADE,

CONSTRAINT chk_calculados_cartera_tipo CHECK (
    cartera_tipo IS NULL
        OR cartera_tipo BETWEEN 0 AND 2 ),

CONSTRAINT chk_calculados_dias_por_vencer
   CHECK ( dias_por_vencer IS NULL OR dias_por_vencer BETWEEN 1 AND 3  ),

CONSTRAINT chk_calculados_intervalo_edad
 CHECK ( intervalo_edad IS NULL  OR intervalo_edad BETWEEN 1 AND 14
                                                  ),
CONSTRAINT chk_calculados_riesgo_traspaso CHECK (
  cart_riesgo_traspaso_a_vencida IS NULL OR cart_riesgo_traspaso_a_vencida BETWEEN 0 AND 3 )


);

/* Vista para el Dropdown de selección de mes*/
CREATE OR REPLACE VIEW view_meses_cartera_disponibles AS
SELECT DISTINCT
    mes_corte,
    fecha_corte
FROM cartera_datos
ORDER BY mes_corte DESC;

CREATE INDEX IF NOT EXISTS index_preview_cartera
    ON cartera_datos (
                      mes_corte DESC,
                      sucursal,
                      numero_contrato
        )
    INCLUDE (
        id_analisis_mensual,
        nombre_acreditado,
        numero_socio,
        producto_credito,
        capital_vigente
        );


CREATE INDEX IF NOT EXISTS index_cartera_mes_estatus
    ON cartera_datos (
   mes_corte,
   vigente_o_vencido
        );


CREATE INDEX IF NOT EXISTS index_cartera_mes_producto
    ON cartera_datos (
 mes_corte,
 producto_credito
        );


CREATE INDEX IF NOT EXISTS index_cartera_mes_clasificacion
    ON cartera_datos (
mes_corte,
clasificacion_credito
        );


CREATE INDEX IF NOT EXISTS index_cartera_numero_socio
    ON cartera_datos(numero_socio);


CREATE INDEX IF NOT EXISTS index_cartera_numero_contrato
    ON cartera_datos(numero_contrato);
