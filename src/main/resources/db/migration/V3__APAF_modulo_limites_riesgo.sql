CREATE TABLE riesgo_limite (
 id_limite UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 agrupacion VARCHAR(40) NOT NULL,
 clave VARCHAR(160) NOT NULL,
 identificacion VARCHAR(255) NOT NULL,
 tipo_limite VARCHAR(10) NOT NULL,
 porcentaje_actual NUMERIC(7,4) NULL, 
 activo BOOLEAN NOT NULL DEFAULT TRUE,
 fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 version_lock BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT chk_riesgo_limite_agrupacion CHECK (agrupacion IN (
 'PRODUCTO','MUNICIPIO','ESTADO','OCUPACION','EDAD','GENERO',
 'SUCURSAL','ACREDITADO','MODALIDAD','TIPO_CLASIFICACION'
 )),
 CONSTRAINT uk_riesgo_limite_agrupacion_clave UNIQUE (agrupacion, clave),
 CONSTRAINT chk_riesgo_limite_clave_no_vacia CHECK (BTRIM(clave) <> ''),
 CONSTRAINT chk_riesgo_limite_identificacion_no_vacia CHECK (BTRIM(identificacion) <> ''),
 CONSTRAINT chk_riesgo_limite_tipo CHECK (tipo_limite IN ('MAXIMO','MINIMO')),
 CONSTRAINT chk_riesgo_limite_porcentaje CHECK (porcentaje_actual >= 0 AND porcentaje_actual <= 100)
);

CREATE TABLE riesgo_limite_historial (
 id_historial UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 id_limite UUID NOT NULL,
 accion VARCHAR(20) NOT NULL,
 porcentaje_anterior NUMERIC(7,4) NULL,
 porcentaje_nuevo NUMERIC(7,4) NULL,
 motivo VARCHAR(255) NULL,
 realizado_por BIGINT NULL,
 actor VARCHAR(150) NOT NULL,
 fecha_movimiento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_riesgo_limite_historial_limite FOREIGN KEY (id_limite) REFERENCES riesgo_limite(id_limite) ON DELETE CASCADE,
 CONSTRAINT fk_riesgo_limite_historial_usuario FOREIGN KEY (realizado_por) REFERENCES usuarios(id_usuario) ON DELETE SET NULL,
 CONSTRAINT chk_riesgo_limite_historial_accion CHECK (accion IN ('CREACION','ACTUALIZACION','DESACTIVACION','REACTIVACION'))
);

CREATE INDEX index_riesgo_limite_agrupacion ON riesgo_limite(agrupacion, identificacion);
CREATE INDEX index_riesgo_limite_historial_fecha ON riesgo_limite_historial(id_limite, fecha_movimiento DESC);
CREATE INDEX index_riesgo_limite_historial_usuario ON riesgo_limite_historial(realizado_por, fecha_movimiento DESC);

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
JOIN cartera_datos_calculados cdc ON cdc.id_analisis_mensual = cd.id_analisis_mensual;

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
    
    -- MODALIDAD (Incluye la vacía)
    ('MODALIDAD', 'PAGO UNICO DE PRINCIPAL E INTERES AL VENCIMIENTO', 'Pago unico de principal e interes al vencimiento', 'MAXIMO', 2.0000),
    ('MODALIDAD', 'PAGO UNICO DE PRINCIPAL AL VENCIMIENTO Y PAGOS PERIODICOS DE INTERES', 'Pago unico de principal al vencimiento y pagos periodicos de interes', 'MAXIMO', NULL),
    ('MODALIDAD', 'PAGOS PERIODICOS DE PRINCIPAL E INTERES', 'Pagos periodicos de principal e interes', 'MINIMO', 98.0000),
    
    -- ESTADOS (Incluye todos los vacíos)
    ('ESTADO', 'BAJA CALIFORNIA', 'Baja California', 'MAXIMO', NULL),
    ('ESTADO', 'CIUDAD DE MEXICO', 'Ciudad de Mexico', 'MAXIMO', NULL),
    ('ESTADO', 'GUERRERO', 'Guerrero', 'MAXIMO', NULL),
    ('ESTADO', 'HIDALGO', 'Hidalgo', 'MAXIMO', NULL),
    ('ESTADO', 'JALISCO', 'Jalisco', 'MAXIMO', NULL),
    ('ESTADO', 'MEXICO', 'Mexico', 'MAXIMO', NULL),
    ('ESTADO', 'MORELOS', 'Morelos', 'MAXIMO', NULL),
    ('ESTADO', 'NUEVO LEON', 'Nuevo Leon', 'MAXIMO', NULL),
    ('ESTADO', 'PUEBLA', 'Puebla', 'MAXIMO', 20.0000),
    ('ESTADO', 'QUERETARO', 'Queretaro', 'MAXIMO', NULL),
    ('ESTADO', 'SONORA', 'Sonora', 'MAXIMO', NULL),
    ('ESTADO', 'TAMAULIPAS', 'Tamaulipas', 'MAXIMO', NULL),
    ('ESTADO', 'TLAXCALA', 'Tlaxcala', 'MAXIMO', NULL),
    ('ESTADO', 'VERACRUZ DE IGNACIO DE LA LLAVE', 'Veracruz de Ignacio de la Llave', 'MAXIMO', 85.0000),
    ('ESTADO', 'ZACATECAS', 'Zacatecas', 'MAXIMO', NULL),
    ('ESTADO', 'CAROLINA DEL NORTE', 'Carolina del Norte', 'MAXIMO', NULL),
    ('ESTADO', 'OTROS', 'Otros', 'MAXIMO', 5.0000),
    
    -- EDAD (Todos vacíos)
    ('EDAD', '1', '18 A 25', 'MAXIMO', NULL),
    ('EDAD', '2', '26 A 30', 'MAXIMO', NULL),
    ('EDAD', '3', '31 A 35', 'MAXIMO', NULL),
    ('EDAD', '4', '36 A 40', 'MAXIMO', NULL),
    ('EDAD', '5', '41 A 45', 'MAXIMO', NULL),
    ('EDAD', '6', '46 A 50', 'MAXIMO', NULL),
    ('EDAD', '7', '51 A 55', 'MAXIMO', NULL),
    ('EDAD', '8', '56 A 60', 'MAXIMO', NULL),
    ('EDAD', '9', '61 A 65', 'MAXIMO', NULL),
    ('EDAD', '10', '66 A 70', 'MAXIMO', NULL),
    ('EDAD', '11', '71 A 75', 'MAXIMO', NULL),
    ('EDAD', '12', '76 A 80', 'MAXIMO', NULL),
    ('EDAD', '13', '81 A 85', 'MAXIMO', NULL),
    ('EDAD', '14', '86 A 90', 'MAXIMO', NULL),

    -- GENERO (Todos vacíos)
    ('GENERO', 'MASCULINO', 'Masculino', 'MAXIMO', NULL),
    ('GENERO', 'FEMENINO', 'Femenino', 'MINIMO', NULL),

    -- TIPO DE CARTERA / CLASIFICACION (Todos vacíos)
    ('TIPO_CLASIFICACION', 'NUEVO', 'Nuevo', 'MAXIMO', NULL),
    ('TIPO_CLASIFICACION', 'RENOVADO', 'Renovado', 'MAXIMO', NULL),
    ('TIPO_CLASIFICACION', 'REESTRUCTURADO', 'Reestructurado', 'MAXIMO', NULL),
    
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
    INSERT INTO riesgo_limite (agrupacion, clave, identificacion, tipo_limite, porcentaje_actual, activo)
    SELECT agrupacion, clave, identificacion, tipo_limite, porcentaje, TRUE FROM limites
    RETURNING id_limite, porcentaje_actual
)
INSERT INTO riesgo_limite_historial (
    id_limite, accion, porcentaje_anterior, porcentaje_nuevo, motivo, actor
)
SELECT 
    id_limite, 'CREACION', NULL, porcentaje_actual, 'Inicialización por migración', 'MIGRACION_FLYWAY'
FROM inserted_limites;