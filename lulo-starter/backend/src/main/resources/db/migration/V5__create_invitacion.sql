CREATE TABLE invitacion_usuario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID      NOT NULL REFERENCES empresa(id),
    rol_pool_id UUID      NOT NULL REFERENCES rol_pool(id),
    created_by_user_id UUID      NOT NULL REFERENCES usuario(id),
    email              VARCHAR(255) NOT NULL,
    token_hash         VARCHAR(255) NOT NULL,
    estado             VARCHAR(20)  NOT NULL DEFAULT 'pendiente',
    expires_at         TIMESTAMP    NOT NULL,
    created_at         TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_invitacion_empresa ON invitacion_usuario(empresa_id);
CREATE INDEX idx_invitacion_token   ON invitacion_usuario(token_hash);
