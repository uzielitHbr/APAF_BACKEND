CREATE TABLE catalogo_sucursal (
    id_sucursal UUID PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO catalogo_sucursal (id_sucursal, codigo, nombre) VALUES
(gen_random_uuid(), '10401', 'Matriz'),
(gen_random_uuid(), '10402', 'Entabladero'),
(gen_random_uuid(), '10403', 'Coxquihui'),
(gen_random_uuid(), '10404', 'Huehuetla'),
(gen_random_uuid(), '10405', 'La Uno');

CREATE TABLE catalogo_banda_morosidad (
    rango_id VARCHAR(20) PRIMARY KEY,
    tipo_cartera VARCHAR(20) NOT NULL,
    etiqueta VARCHAR(100) NOT NULL,
    orden INTEGER NOT NULL
);

INSERT INTO catalogo_banda_morosidad (rango_id, tipo_cartera, etiqueta, orden) VALUES
('c-0', 'CONSUMO', '0 dias', 1),
('c-1', 'CONSUMO', '1-7 dias', 2),
('c-2', 'CONSUMO', '8-30 dias', 3),
('c-3', 'CONSUMO', '31-60 dias', 4),
('c-4', 'CONSUMO', '61-90 dias', 5),
('c-5', 'CONSUMO', '91-120 dias', 6),
('c-6', 'CONSUMO', '121-180 dias', 7),
('c-7', 'CONSUMO', '181 dias o mas', 8),

('com-0', 'COMERCIAL', '0 dias', 1),
('com-1', 'COMERCIAL', '1-30 dias', 2),
('com-2', 'COMERCIAL', '31-60 dias', 3),
('com-3', 'COMERCIAL', '61-90 dias', 4),
('com-4', 'COMERCIAL', '91-120 dias', 5),
('com-5', 'COMERCIAL', '121-150 dias', 6),
('com-6', 'COMERCIAL', '151-180 dias', 7),
('com-7', 'COMERCIAL', '181-210 dias', 8),
('com-8', 'COMERCIAL', '211-240 dias', 9),
('com-9', 'COMERCIAL', '241 dias o mas', 10),

('v-0', 'VIVIENDA', '0 dias', 1),
('v-1', 'VIVIENDA', '1-30 dias', 2),
('v-2', 'VIVIENDA', '31-60 dias', 3),
('v-3', 'VIVIENDA', '61-90 dias', 4),
('v-4', 'VIVIENDA', '91-120 dias', 5),
('v-5', 'VIVIENDA', '121-150 dias', 6),
('v-6', 'VIVIENDA', '151-180 dias', 7),
('v-7', 'VIVIENDA', '181-1460 dias', 8),
('v-8', 'VIVIENDA', '1461 dias o mas', 9);

CREATE TABLE analisis_trimestral_ejecucion (
    id_ejecucion UUID PRIMARY KEY,
    mes_corte DATE NOT NULL,
    fecha_corte DATE NOT NULL,
    numero_version INTEGER NOT NULL,
    estado VARCHAR(20) NOT NULL,
    version_formula VARCHAR(50) NOT NULL,
    total_registros BIGINT NOT NULL,
    generado_por BIGINT NULL,
    actor VARCHAR(150) NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NULL,
    codigo_error VARCHAR(80) NULL,
    mensaje_error TEXT NULL,
    version_lock BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_analisis_mes_version UNIQUE (mes_corte, numero_version)
);

CREATE TABLE analisis_trimestral_sucursal_detalle (
    id_detalle BIGSERIAL PRIMARY KEY,
    id_ejecucion UUID NOT NULL,
    sucursal_codigo VARCHAR(20) NULL,
    tipo_cartera VARCHAR(20) NOT NULL,
    creditos_vigentes BIGINT NOT NULL,
    capital_vigente DECIMAL(19,4) NOT NULL,
    intereses_vigentes DECIMAL(19,4) NOT NULL,
    cartera_vigente DECIMAL(19,4) NOT NULL,
    creditos_vencidos BIGINT NOT NULL,
    capital_vencido DECIMAL(19,4) NOT NULL,
    intereses_vencidos DECIMAL(19,4) NOT NULL,
    cartera_vencida DECIMAL(19,4) NOT NULL,
    creditos_total BIGINT NOT NULL,
    cartera_total DECIMAL(19,4) NOT NULL,
    proporcion_global_porcentaje DECIMAL(7,4) NULL,
    proporcion_dentro_sucursal_porcentaje DECIMAL(7,4) NULL,
    CONSTRAINT fk_sucursal_detalle_ejecucion FOREIGN KEY (id_ejecucion) REFERENCES analisis_trimestral_ejecucion(id_ejecucion)
);
CREATE UNIQUE INDEX uq_analisis_sucursal_detalle ON analisis_trimestral_sucursal_detalle 
    (id_ejecucion, tipo_cartera, (COALESCE(sucursal_codigo, 'CONSOLIDADO')));

CREATE TABLE analisis_trimestral_sucursal_resumen (
    id_resumen BIGSERIAL PRIMARY KEY,
    id_ejecucion UUID NOT NULL,
    sucursal_codigo VARCHAR(20) NULL,
    imor_porcentaje DECIMAL(7,4) NOT NULL,
    cartera_total DECIMAL(19,4) NOT NULL,
    cartera_vencida DECIMAL(19,4) NOT NULL,
    proporcion_global DECIMAL(7,4) NOT NULL,
    CONSTRAINT fk_sucursal_resumen_ejecucion FOREIGN KEY (id_ejecucion) REFERENCES analisis_trimestral_ejecucion(id_ejecucion)
);
CREATE UNIQUE INDEX uq_analisis_sucursal_resumen ON analisis_trimestral_sucursal_resumen 
    (id_ejecucion, (COALESCE(sucursal_codigo, 'CONSOLIDADO')));

CREATE TABLE analisis_trimestral_producto_resultado (
    id_ejecucion UUID NOT NULL,
    producto_codigo VARCHAR(50) NOT NULL,
    producto_nombre VARCHAR(150) NOT NULL,
    creditos_vencidos BIGINT NOT NULL,
    importe_vencido DECIMAL(19,4) NOT NULL,
    proporcion_porcentaje DECIMAL(7,4) NOT NULL,
    PRIMARY KEY (id_ejecucion, producto_codigo),
    CONSTRAINT fk_producto_resultado_ejecucion FOREIGN KEY (id_ejecucion) REFERENCES analisis_trimestral_ejecucion(id_ejecucion)
);

CREATE TABLE analisis_trimestral_banda_resultado (
    id_ejecucion UUID NOT NULL,
    clasificacion VARCHAR(20) NOT NULL,
    tipo_cartera VARCHAR(20) NOT NULL,
    rango_id VARCHAR(20) NOT NULL,
    rango_etiqueta VARCHAR(100) NOT NULL,
    orden INTEGER NOT NULL,
    numero_creditos BIGINT NOT NULL,
    importe_total DECIMAL(19,4) NOT NULL,
    PRIMARY KEY (id_ejecucion, clasificacion, tipo_cartera, rango_id),
    CONSTRAINT fk_banda_resultado_ejecucion FOREIGN KEY (id_ejecucion) REFERENCES analisis_trimestral_ejecucion(id_ejecucion)
);

CREATE TABLE analisis_trimestral_inconsistencia (
    id_inconsistencia BIGSERIAL PRIMARY KEY,
    id_ejecucion UUID NOT NULL,
    codigo VARCHAR(80) NOT NULL,
    severidad VARCHAR(20) NOT NULL,
    modulo VARCHAR(50) NOT NULL,
    referencia VARCHAR(200) NULL,
    valor_esperado TEXT NULL,
    valor_obtenido TEXT NULL,
    mensaje TEXT NOT NULL,
    bloqueante BOOLEAN NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inconsistencia_ejecucion FOREIGN KEY (id_ejecucion) REFERENCES analisis_trimestral_ejecucion(id_ejecucion)
);