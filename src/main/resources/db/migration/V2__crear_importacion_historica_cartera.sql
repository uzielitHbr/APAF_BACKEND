CREATE TABLE cartera_importacion_historica (
    id_importacion UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mes_corte DATE NOT NULL,
    fecha_corte DATE NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    hash_sha256 CHAR(64) NOT NULL,
    estado VARCHAR(20) NOT NULL
        CHECK (estado IN ('INICIADA','VALIDADA','COMPLETADA','FALLIDA')),
    total_filas INTEGER NOT NULL DEFAULT 0,
    filas_validas INTEGER NOT NULL DEFAULT 0,
    filas_insertadas INTEGER NOT NULL DEFAULT 0,
    filas_calculadas INTEGER NOT NULL DEFAULT 0,
    filas_rechazadas INTEGER NOT NULL DEFAULT 0,
    version_importador VARCHAR(50),
    ejecutado_por VARCHAR(100) NOT NULL DEFAULT 'EQUIPO_DESARROLLO',
    fecha_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_fin TIMESTAMP,
    codigo_error VARCHAR(80),
    mensaje_error TEXT,
    CONSTRAINT chk_importacion_mes_inicio
        CHECK (mes_corte = DATE_TRUNC('month', mes_corte)::DATE),
    CONSTRAINT chk_importacion_fecha_fin_mes
        CHECK (fecha_corte =
        (mes_corte + INTERVAL '1 month' - INTERVAL '1 day')::DATE)
);

CREATE UNIQUE INDEX uk_importacion_mes_completada
ON cartera_importacion_historica(mes_corte)
WHERE estado = 'COMPLETADA';

CREATE UNIQUE INDEX uk_importacion_hash_completada
ON cartera_importacion_historica(hash_sha256)
WHERE estado = 'COMPLETADA';

ALTER TABLE cartera_datos
ADD COLUMN IF NOT EXISTS id_importacion UUID NULL;

ALTER TABLE cartera_datos
ADD CONSTRAINT fk_cartera_importacion_historica
FOREIGN KEY (id_importacion)
REFERENCES cartera_importacion_historica(id_importacion);

CREATE INDEX IF NOT EXISTS index_cartera_id_importacion
ON cartera_datos(id_importacion);
