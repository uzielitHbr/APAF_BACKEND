CREATE TABLE riesgo_limite (
 id_limite UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 agrupacion VARCHAR(40) NOT NULL,
 clave VARCHAR(160) NOT NULL,
 identificacion VARCHAR(255) NOT NULL,
 fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 version_lock BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT chk_riesgo_limite_agrupacion CHECK (agrupacion IN (
 'PRODUCTO','MUNICIPIO','ESTADO','OCUPACION','EDAD','GENERO',
 'SUCURSAL','ACREDITADO','MODALIDAD','TIPO_CLASIFICACION'
 )),
 CONSTRAINT uk_riesgo_limite_agrupacion_clave UNIQUE (agrupacion, clave),
 CONSTRAINT chk_riesgo_limite_clave_no_vacia CHECK (BTRIM(clave) <> ''),
 CONSTRAINT chk_riesgo_limite_identificacion_no_vacia
 CHECK (BTRIM(identificacion) <> '')
);

CREATE TABLE riesgo_limite_version (
 id_version UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 id_limite UUID NOT NULL,
 numero_version INTEGER NOT NULL,
 tipo_limite VARCHAR(10) NOT NULL,
 limite_porcentaje NUMERIC(7,4) NOT NULL,
 activo BOOLEAN NOT NULL,
 accion VARCHAR(20) NOT NULL,
 vigente_desde TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 vigente_hasta TIMESTAMP NULL,
 realizado_por BIGINT NULL,
 actor VARCHAR(150) NOT NULL,
 origen VARCHAR(30) NOT NULL DEFAULT 'USUARIO',
 fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_riesgo_limite_version_limite
 FOREIGN KEY (id_limite) REFERENCES riesgo_limite(id_limite) ON DELETE CASCADE,
 CONSTRAINT fk_riesgo_limite_version_usuario
 FOREIGN KEY (realizado_por) REFERENCES usuarios(id_usuario) ON DELETE SET NULL,
 CONSTRAINT uk_riesgo_limite_numero_version
 UNIQUE (id_limite, numero_version),
 CONSTRAINT chk_riesgo_limite_tipo
 CHECK (tipo_limite IN ('MAXIMO','MINIMO')),
 CONSTRAINT chk_riesgo_limite_porcentaje
 CHECK (limite_porcentaje >= 0 AND limite_porcentaje <= 100),
 CONSTRAINT chk_riesgo_limite_accion CHECK (accion IN (
 'CREACION','ACTUALIZACION','DESACTIVACION','REACTIVACION'
 )),
 CONSTRAINT chk_riesgo_limite_origen
 CHECK (origen IN ('USUARIO','MIGRACION_EXCEL_V2')),
 CONSTRAINT chk_riesgo_limite_vigencia
 CHECK (vigente_hasta IS NULL OR vigente_hasta > vigente_desde),
 CONSTRAINT chk_riesgo_limite_actor CHECK (BTRIM(actor) <> '')
);

-- Índices de optimización
CREATE UNIQUE INDEX uk_riesgo_limite_version_actual
 ON riesgo_limite_version(id_limite)
 WHERE vigente_hasta IS NULL;

CREATE INDEX index_riesgo_limite_agrupacion
 ON riesgo_limite(agrupacion, identificacion);

CREATE INDEX index_riesgo_limite_version_vigencia
 ON riesgo_limite_version(id_limite, vigente_desde DESC, vigente_hasta);

CREATE INDEX index_riesgo_limite_version_usuario_fecha
 ON riesgo_limite_version(realizado_por, fecha_registro DESC);

-- 3. Vista Mensual de Riesgos
CREATE OR REPLACE VIEW view_riesgo_cartera_mensual AS
SELECT
 cd.id_analisis_mensual,
 cd.mes_corte,
 cd.fecha_corte,
 cd.producto_credito,
 cdc.numero_producto,
 cd.municipio,
 cd.estado,
 cdc.ocupacion_agrupada,
 cdc.intervalo_edad,
 cd.genero,
 cd.sucursal,
 cd.cargo_acreditado_parte_relacionada,
 cd.modalidad_pago,
 cd.renovado_reestructurado_normal,
 COALESCE(cdc.numero_creditos, 0) AS numero_creditos,
 COALESCE(cd.capital_vigente, 0) + COALESCE(cd.int_dev_no_cobrados_vigentes, 0) AS cartera_vigente,
 COALESCE(cd.capital_vencido, 0) + COALESCE(cd.int_dev_no_cobrados_vencidos, 0) AS cartera_vencida,
 COALESCE(cdc.cartera_total, 0) AS cartera_total
FROM cartera_datos cd
JOIN cartera_datos_calculados cdc
 ON cdc.id_analisis_mensual = cd.id_analisis_mensual;


WITH limites (agrupacion, clave, identificacion, tipo_limite, porcentaje) AS (
    VALUES
    -- PRODUCTOS
    ('PRODUCTO', '3101', 'Credito Ordinario', 'MAXIMO', 35.0000),
    ('PRODUCTO', '3102', 'Credito Automatico', 'MAXIMO', 10.0000),
    ('PRODUCTO', '3103', 'Auto-credito', 'MAXIMO', 40.0000),
    ('PRODUCTO', '3104', 'Credi Hogar', 'MAXIMO', 10.0000),
    ('PRODUCTO', '3105', 'Creditazo', 'MAXIMO', 12.0000),
    ('PRODUCTO', '3107', 'Vivienda segura', 'MAXIMO', 35.0000),
    ('PRODUCTO', '3109', 'Credito Premier', 'MAXIMO', 15.0000),
    ('PRODUCTO', '3110', 'Credito de Confianza', 'MAXIMO', 20.0000),
    ('PRODUCTO', '3129', 'Semilla', 'MAXIMO', 10.0000),
    ('PRODUCTO', '3130', 'Credito Agropecuario', 'MAXIMO', 10.0000),
    ('PRODUCTO', '3134', 'Multi-Credito', 'MAXIMO', 10.0000),
    
    -- MODALIDAD
    ('MODALIDAD', 'PAGO UNICO DE PRINCIPAL E INTERES AL VENCIMIENTO', 'Pago unico de principal e interes al vencimiento', 'MAXIMO', 2.0000),
    ('MODALIDAD', 'PAGOS PERIODICOS DE PRINCIPAL E INTERES', 'Pagos periodicos de principal e interes', 'MINIMO', 98.0000),
    
    -- ESTADOS
    ('ESTADO', 'PUEBLA', 'Puebla', 'MAXIMO', 20.0000),
    ('ESTADO', 'VERACRUZ DE IGNACIO DE LA LLAVE', 'Veracruz de Ignacio de la Llave', 'MAXIMO', 85.0000),
    ('ESTADO', 'OTROS', 'Otros', 'MAXIMO', 5.0000),
    
    -- SUCURSALES
    ('SUCURSAL', 'COYUTLA', 'Coyutla', 'MAXIMO', 100.0000),
    ('SUCURSAL', 'ENTABLEDERO', 'Entabledero', 'MAXIMO', 100.0000),
    ('SUCURSAL', 'COXQUIHUI', 'Coxquihui', 'MAXIMO', 100.0000),
    ('SUCURSAL', 'HUEHUETLA', 'Huehuetla', 'MAXIMO', 100.0000),
    ('SUCURSAL', 'LA UNO', 'La Uno', 'MAXIMO', 100.0000),
    
    -- OCUPACIONES
    ('OCUPACION', 'AGRICULTOR', 'Agricultor', 'MAXIMO', 15.0000),
    ('OCUPACION', 'ALBAÑIL', 'Albañil', 'MAXIMO', 5.0000),
    ('OCUPACION', 'AMA DE CASA', 'Ama De Casa', 'MAXIMO', 10.0000),
    ('OCUPACION', 'AVICULTOR', 'Avicultor', 'MAXIMO', 5.0000),
    ('OCUPACION', 'CHOFER', 'Chofer', 'MAXIMO', 10.0000),
    ('OCUPACION', 'COMERCIANTE', 'Comerciante', 'MAXIMO', 35.0000),
    ('OCUPACION', 'EMPLEADO', 'Empleado', 'MAXIMO', 20.0000),
    ('OCUPACION', 'GANADERO', 'Ganadero', 'MAXIMO', 10.0000),
    ('OCUPACION', 'JUBILADO', 'Jubilado', 'MAXIMO', 10.0000),
    ('OCUPACION', 'PORCICULTOR', 'Porcicultor', 'MAXIMO', 5.0000),
    ('OCUPACION', 'PROFESOR', 'Profesor', 'MAXIMO', 18.0000),
    ('OCUPACION', 'TAXISTA', 'Taxista', 'MAXIMO', 5.0000),
    ('OCUPACION', 'VENDEDOR', 'Vendedor', 'MAXIMO', 5.0000),
    ('OCUPACION', 'OTROS', 'Otros', 'MAXIMO', 30.0000),
    
    -- MUNICIPIOS
    ('MUNICIPIO', 'CHUMATLAN', 'Chumatlan', 'MAXIMO', 5.0000),
    ('MUNICIPIO', 'COAHUITLAN', 'Coahuitlan', 'MAXIMO', 13.0000),
    ('MUNICIPIO', 'COATZINTLA', 'Coatzintla', 'MAXIMO', 3.0000),
    ('MUNICIPIO', 'COXQUIHUI', 'Coxquihui', 'MAXIMO', 13.0000),
    ('MUNICIPIO', 'COYUTLA', 'Coyutla', 'MAXIMO', 40.0000),
    ('MUNICIPIO', 'CUETZALAN DEL PROGRESO', 'Cuetzalan Del Progreso', 'MAXIMO', 3.0000),
    ('MUNICIPIO', 'ESPINAL', 'Espinal', 'MAXIMO', 25.0000),
    ('MUNICIPIO', 'FILOMENO MATA', 'Filomeno Mata', 'MAXIMO', 12.0000),
    ('MUNICIPIO', 'HUEHUETLA', 'Huehuetla', 'MAXIMO', 10.0000),
    ('MUNICIPIO', 'MECATLAN', 'Mecatlan', 'MAXIMO', 12.0000),
    ('MUNICIPIO', 'OLINTLA', 'Olintla', 'MAXIMO', 3.0000),
    ('MUNICIPIO', 'PAPANTLA', 'Papantla', 'MAXIMO', 8.0000),
    ('MUNICIPIO', 'POZA RICA DE HIDALGO', 'Poza Rica De Hidalgo', 'MAXIMO', 5.0000),
    ('MUNICIPIO', 'PUEBLA', 'Puebla', 'MAXIMO', 5.0000),
    ('MUNICIPIO', 'TUZAMAPAN DE GALEANA', 'Tuzamapan De Galeana', 'MAXIMO', 3.0000),
    ('MUNICIPIO', 'ZOZOCOLCO DE HIDALGO', 'Zozocolco De Hidalgo', 'MAXIMO', 10.0000),
    ('MUNICIPIO', 'OTROS MUNICIPIOS', 'Otros Municipios', 'MAXIMO', 10.0000)
),
inserted_limites AS (
    INSERT INTO riesgo_limite (agrupacion, clave, identificacion)
    SELECT agrupacion, clave, identificacion FROM limites
    RETURNING id_limite, agrupacion, clave
)
INSERT INTO riesgo_limite_version (
    id_limite, numero_version, tipo_limite, limite_porcentaje, 
    activo, accion, vigente_desde, vigente_hasta, 
    realizado_por, actor, origen
)
SELECT 
    il.id_limite, 
    1, 
    l.tipo_limite, 
    l.porcentaje, 
    TRUE, 
    'CREACION', 
    COALESCE((SELECT CAST(MIN(mes_corte) AS TIMESTAMP) FROM cartera_datos), CURRENT_TIMESTAMP), 
    NULL, 
    NULL, 
    'MIGRACION_FLYWAY', 
    'MIGRACION_EXCEL_V2'
FROM inserted_limites il
JOIN limites l ON il.agrupacion = l.agrupacion AND il.clave = l.clave;
