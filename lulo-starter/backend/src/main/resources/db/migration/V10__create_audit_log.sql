-- Tabla append-only: nunca se actualiza ni elimina un registro
-- accion: CREAR | EDITAR | PUBLICAR | ARCHIVAR | COMPARTIR | ASIGNAR_ROL | REVOCAR_ROL
CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID      NOT NULL REFERENCES empresa(id),
    usuario_id UUID      NOT NULL REFERENCES usuario(id),
    entidad     VARCHAR(100) NOT NULL,
    entidad_id  UUID      NOT NULL,
    accion      VARCHAR(30)  NOT NULL,
    diff_json   JSONB,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_empresa        ON audit_log(empresa_id);
CREATE INDEX idx_audit_entidad        ON audit_log(empresa_id, entidad, entidad_id);
CREATE INDEX idx_audit_created_at     ON audit_log(created_at DESC);
