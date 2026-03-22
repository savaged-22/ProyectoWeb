CREATE TABLE pool (
    id          SERIAL PRIMARY KEY,
    empresa_id  INTEGER      NOT NULL REFERENCES empresa(id),
    nombre      VARCHAR(255) NOT NULL,
    config_json JSONB,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pool_empresa ON pool(empresa_id);
