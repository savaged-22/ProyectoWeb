CREATE TABLE proceso_compartido (
    id                 SERIAL PRIMARY KEY,
    proceso_id         INTEGER     NOT NULL REFERENCES proceso(id),
    pool_destino_id    INTEGER     NOT NULL REFERENCES pool(id),
    created_by_user_id INTEGER     NOT NULL REFERENCES usuario(id),
    permiso            VARCHAR(50) NOT NULL DEFAULT 'lectura',
    created_at         TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE (proceso_id, pool_destino_id)
);
