-- Catálogo global de permisos (compartido entre todas las empresas)
CREATE TABLE permiso (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo      VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT
);

-- Roles dentro de un pool (cada pool define sus propios roles)
CREATE TABLE rol_pool (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pool_id UUID      NOT NULL REFERENCES pool(id),
    nombre         VARCHAR(100) NOT NULL,
    descripcion    TEXT,
    activo         BOOLEAN      NOT NULL DEFAULT TRUE,
    es_propietario BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_rol_pool_pool ON rol_pool(pool_id);

-- Permisos asignados a cada rol del pool
CREATE TABLE rol_pool_permiso (
    rol_pool_id UUID NOT NULL REFERENCES rol_pool(id),
    permiso_id  INTEGER NOT NULL REFERENCES permiso(id),
    PRIMARY KEY (rol_pool_id, permiso_id)
);

-- Asignación de usuarios a roles dentro de un pool
CREATE TABLE usuario_rol_pool (
    usuario_id UUID   NOT NULL REFERENCES usuario(id),
    rol_pool_id UUID   NOT NULL REFERENCES rol_pool(id),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (usuario_id, rol_pool_id)
);
