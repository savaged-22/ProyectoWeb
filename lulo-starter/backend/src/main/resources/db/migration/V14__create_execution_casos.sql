CREATE TABLE caso (
    id UUID PRIMARY KEY,
    proceso_id UUID NOT NULL REFERENCES proceso(id),
    iniciado_por_id UUID NOT NULL REFERENCES usuario(id),
    estado VARCHAR(255) NOT NULL DEFAULT 'PENDIENTE',
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP
);

CREATE TABLE caso_actividad (
    id UUID PRIMARY KEY,
    caso_id UUID NOT NULL REFERENCES caso(id) ON DELETE CASCADE,
    nodo_id UUID NOT NULL REFERENCES nodo(id),
    estado VARCHAR(255) NOT NULL DEFAULT 'PENDIENTE',
    asignado_a_id UUID REFERENCES usuario(id),
    variables_json JSONB,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP
);

CREATE TABLE caso_log (
    id UUID PRIMARY KEY,
    caso_id UUID NOT NULL REFERENCES caso(id) ON DELETE CASCADE,
    nivel VARCHAR(255) NOT NULL,
    mensaje VARCHAR(1000) NOT NULL,
    fecha TIMESTAMP NOT NULL
);
