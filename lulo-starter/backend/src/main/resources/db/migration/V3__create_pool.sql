CREATE TABLE pool (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID      NOT NULL REFERENCES empresa(id),
    nombre      VARCHAR(255) NOT NULL,
    config_json JSONB,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pool_empresa ON pool(empresa_id);
