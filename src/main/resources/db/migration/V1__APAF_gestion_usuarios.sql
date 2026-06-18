-- 1. Tabla de Roles
CREATE TABLE rol (
id_rol BIGSERIAL PRIMARY KEY,
codigo_rol VARCHAR(50) NOT NULL UNIQUE
);

-- 2. Tabla de Usuarios
CREATE TABLE usuarios (
id_usuario BIGSERIAL PRIMARY KEY,
nombre_completo VARCHAR(150) NOT NULL,
correo VARCHAR(100) NOT NULL UNIQUE,
contrasenia VARCHAR(255),
estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                              CHECK (estado IN ('PENDIENTE', 'ACTIVO', 'INACTIVO')),
rol VARCHAR(30) NOT NULL
                      CHECK ( rol IN ('ADMIN','RIESGOS','ANALISTA') ),
intentos_fallidos INT DEFAULT 0,
cuenta_bloqueada BOOLEAN DEFAULT FALSE,
tiempo_bloqueado TIMESTAMP,
fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
fecha_baja TIMESTAMP,
token_recuperacion VARCHAR(255) UNIQUE,
expiracion_token TIMESTAMP,
creado_por BIGINT,
CONSTRAINT fk_usuario_creado_por FOREIGN KEY (creado_por) REFERENCES usuarios(id_usuario) ON DELETE SET NULL
);

-- 3. Inserción de Roles
INSERT INTO rol (codigo_rol) VALUES
('ADMIN'),
('RIESGOS'),
('ANALISTA')
    ON CONFLICT (codigo_rol) DO NOTHING;