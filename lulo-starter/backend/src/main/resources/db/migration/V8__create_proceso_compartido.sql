CREATE TABLE proceso_compartido (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    proceso_id UUID     NOT NULL REFERENCES proceso(id),
    pool_destino_id    UUID     NOT NULL REFERENCES pool(id),
    created_by_user_id UUID     NOT NULL REFERENCES usuario(id),
    permiso            VARCHAR(50) NOT NULL DEFAULT 'lectura',
    created_at         TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE (proceso_id, pool_destino_id)
);
