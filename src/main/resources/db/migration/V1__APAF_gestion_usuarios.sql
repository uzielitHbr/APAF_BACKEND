-- 1. Tabla de Permisos
CREATE TABLE permisos (
id_permiso SERIAL PRIMARY KEY,
codigo_permiso VARCHAR(50) NOT NULL UNIQUE
);

-- 2. Tabla de Usuarios
CREATE TABLE usuarios (
id_usuario SERIAL PRIMARY KEY,
nombre_completo VARCHAR(150) NOT NULL,
correo VARCHAR(100) NOT NULL UNIQUE,
password VARCHAR(255),
estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                              CHECK (estado IN ('PENDIENTE', 'ACTIVO', 'INACTIVO')),

fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
fecha_baja TIMESTAMP,
token_recuperacion VARCHAR(255) UNIQUE,
expiracion_token TIMESTAMP,

creado_por INT,
      CONSTRAINT fk_usuario_creado_por
             FOREIGN KEY (creado_por) REFERENCES usuarios(id_usuario)
                                  ON DELETE SET NULL
);

-- 3. Tabla Unión de Usuarios y Permisos
CREATE TABLE usuarios_permisos (
id_usuario INT NOT NULL,
id_permiso INT NOT NULL,
    PRIMARY KEY (id_usuario, id_permiso),

         CONSTRAINT fk_usuarios_permisos_usuario  FOREIGN KEY (id_usuario)
                                        REFERENCES usuarios(id_usuario)
                                           ON DELETE CASCADE,

        CONSTRAINT fk_usuarios_permisos_permiso FOREIGN KEY (id_permiso)
                        REFERENCES permisos(id_permiso) ON DELETE CASCADE
);

-- 4. Inserción de permisos iniciales
INSERT INTO permisos (codigo_permiso) VALUES
('GESTIONAR_USUARIOS'),
('VER_CARTERA_COMPLETA'),
('VER_CARTERA_CALCULADA'),
('VER_LIMITES_RIESGO'),
('EDITAR_LIMITES_RIESGO'),
('VER_ANALISIS_TRIM_SUCURSAL'),
('VER_ANALISIS_TRIM_CARTERA'),
('VER_SEGUIMIENTO_CARTERA'),
('VER_EST_CARTERA_EPRC')
    ON CONFLICT (codigo_permiso) DO NOTHING;