-- ─── Lane ────────────────────────────────────────────────────────────────────
-- Carril del diagrama, asociado a un rol funcional de la empresa
CREATE TABLE lane (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    proceso_id UUID      NOT NULL REFERENCES proceso(id),
    rol_proceso_id UUID      REFERENCES rol_proceso(id),
    nombre         VARCHAR(100) NOT NULL,
    orden          INTEGER      NOT NULL DEFAULT 0,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_lane_proceso ON lane(proceso_id);

-- ─── Nodo (tabla base — herencia JOINED) ─────────────────────────────────────
-- tipo: actividad | gateway | inicio | fin
CREATE TABLE nodo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    proceso_id UUID     NOT NULL REFERENCES proceso(id),
    lane_id    UUID     REFERENCES lane(id),
    tipo       VARCHAR(30) NOT NULL,
    label      VARCHAR(255),
    pos_x      FLOAT,
    pos_y      FLOAT,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_nodo_proceso ON nodo(proceso_id);
CREATE INDEX idx_nodo_lane    ON nodo(lane_id);

-- ─── Actividad (tabla hija de Nodo) ──────────────────────────────────────────
-- tipo_actividad: tarea | subproceso | manual | servicio | script
CREATE TABLE actividad (
    nodo_id        UUID     PRIMARY KEY REFERENCES nodo(id),
    tipo_actividad VARCHAR(50),
    props_json     JSONB
);

-- ─── Gateway (tabla hija de Nodo) ────────────────────────────────────────────
-- tipo_gateway: exclusivo | paralelo | inclusivo
CREATE TABLE gateway (
    nodo_id      UUID     PRIMARY KEY REFERENCES nodo(id),
    tipo_gateway VARCHAR(30) NOT NULL,
    config_json  JSONB
);

-- ─── Arco ────────────────────────────────────────────────────────────────────
-- Conexión dirigida entre dos nodos cualesquiera
CREATE TABLE arco (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    proceso_id UUID   NOT NULL REFERENCES proceso(id),
    from_nodo_id   UUID   NOT NULL REFERENCES nodo(id),
    to_nodo_id     UUID   NOT NULL REFERENCES nodo(id),
    condicion_expr TEXT,
    props_json     JSONB,
    activo         BOOLEAN   NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_arco_proceso  ON arco(proceso_id);
CREATE INDEX idx_arco_from     ON arco(from_nodo_id);
CREATE INDEX idx_arco_to       ON arco(to_nodo_id);
