-- HU-25: Enviar mensaje entre procesos (Message Throw)
-- HU-26: Envío de notificaciones externas (webhook, email, queue)
-- HU-27: Recibir mensaje y activar proceso (Message Catch)
-- HU-28: Correlación de mensajes con instancias de proceso

-- Mensajes lanzados entre procesos
CREATE TABLE mensaje_proceso (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID      NOT NULL REFERENCES empresa(id),
    proceso_origen_id UUID      NOT NULL REFERENCES proceso(id),
    nombre_mensaje    VARCHAR(100) NOT NULL,
    payload_json      JSONB,
    correlation_key   VARCHAR(200),
    estado            VARCHAR(20)  NOT NULL DEFAULT 'pendiente', -- pendiente, entregado, fallido
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    delivered_at      TIMESTAMP
);

CREATE INDEX idx_mensaje_empresa         ON mensaje_proceso(empresa_id);
CREATE INDEX idx_mensaje_origen          ON mensaje_proceso(proceso_origen_id);
CREATE INDEX idx_mensaje_nombre_estado   ON mensaje_proceso(nombre_mensaje, estado);
CREATE INDEX idx_mensaje_correlation     ON mensaje_proceso(correlation_key) WHERE correlation_key IS NOT NULL;

-- Suscripciones de procesos a mensajes (Message Catch - HU-27)
CREATE TABLE suscripcion_mensaje (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID      NOT NULL REFERENCES empresa(id),
    proceso_id UUID      NOT NULL REFERENCES proceso(id),
    nombre_mensaje  VARCHAR(100) NOT NULL,
    correlation_key VARCHAR(200),
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_suscripcion_empresa        ON suscripcion_mensaje(empresa_id);
CREATE INDEX idx_suscripcion_proceso        ON suscripcion_mensaje(proceso_id);
CREATE INDEX idx_suscripcion_nombre_activo  ON suscripcion_mensaje(nombre_mensaje, activo);

-- Destinos de notificación externa (HU-26)
CREATE TABLE notificacion_externa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID      NOT NULL REFERENCES empresa(id),
    proceso_id UUID      NOT NULL REFERENCES proceso(id),
    nombre_mensaje  VARCHAR(100) NOT NULL,
    tipo            VARCHAR(20)  NOT NULL CHECK (tipo IN ('webhook', 'email', 'queue')),
    destino         VARCHAR(500) NOT NULL,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notif_empresa        ON notificacion_externa(empresa_id);
CREATE INDEX idx_notif_proceso        ON notificacion_externa(proceso_id);
CREATE INDEX idx_notif_nombre_activo  ON notificacion_externa(nombre_mensaje, activo);

-- Entregas de mensajes a suscripciones (correlación HU-28)
CREATE TABLE entrega_mensaje (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mensaje_id UUID     NOT NULL REFERENCES mensaje_proceso(id),
    suscripcion_id UUID     NOT NULL REFERENCES suscripcion_mensaje(id),
    estado         VARCHAR(20) NOT NULL DEFAULT 'pendiente', -- pendiente, confirmado
    created_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    confirmado_at  TIMESTAMP,
    UNIQUE (mensaje_id, suscripcion_id)
);

CREATE INDEX idx_entrega_mensaje      ON entrega_mensaje(mensaje_id);
CREATE INDEX idx_entrega_suscripcion  ON entrega_mensaje(suscripcion_id);
CREATE INDEX idx_entrega_estado       ON entrega_mensaje(estado);
