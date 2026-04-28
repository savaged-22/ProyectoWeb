-- Roles funcionales de la empresa (asignables a lanes del diagrama)
-- Ejemplos: "Analista", "Gerente de Area", "Auditor"
CREATE TABLE rol_proceso (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID      NOT NULL REFERENCES empresa(id),
    nombre      VARCHAR(100) NOT NULL,
    descripcion TEXT,
    activo      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_rol_proceso_empresa ON rol_proceso(empresa_id);
