CREATE TABLE proceso (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID      NOT NULL REFERENCES empresa(id),
    pool_id UUID      NOT NULL REFERENCES pool(id),
    created_by_user_id UUID      NOT NULL REFERENCES usuario(id),
    nombre             VARCHAR(255) NOT NULL,
    descripcion        TEXT,
    categoria          VARCHAR(100),
    estado             VARCHAR(20)  NOT NULL DEFAULT 'borrador',
    activo             BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP
);

CREATE INDEX idx_proceso_empresa      ON proceso(empresa_id);
CREATE INDEX idx_proceso_pool         ON proceso(pool_id);
CREATE INDEX idx_proceso_estado_activo ON proceso(empresa_id, estado, activo);
