CREATE EXTENSION IF NOT EXISTS pgcrypto;

ALTER TABLE empresa
    ADD COLUMN id_uuid UUID DEFAULT gen_random_uuid();

UPDATE empresa
SET id_uuid = gen_random_uuid()
WHERE id_uuid IS NULL;

ALTER TABLE empresa
    ALTER COLUMN id_uuid SET NOT NULL;

ALTER TABLE usuario
    ADD COLUMN empresa_id_uuid UUID;
UPDATE usuario u
SET empresa_id_uuid = e.id_uuid
FROM empresa e
WHERE u.empresa_id = e.id;
ALTER TABLE usuario
    ALTER COLUMN empresa_id_uuid SET NOT NULL;

ALTER TABLE pool
    ADD COLUMN empresa_id_uuid UUID;
UPDATE pool p
SET empresa_id_uuid = e.id_uuid
FROM empresa e
WHERE p.empresa_id = e.id;
ALTER TABLE pool
    ALTER COLUMN empresa_id_uuid SET NOT NULL;

ALTER TABLE invitacion_usuario
    ADD COLUMN empresa_id_uuid UUID;
UPDATE invitacion_usuario i
SET empresa_id_uuid = e.id_uuid
FROM empresa e
WHERE i.empresa_id = e.id;
ALTER TABLE invitacion_usuario
    ALTER COLUMN empresa_id_uuid SET NOT NULL;

ALTER TABLE rol_proceso
    ADD COLUMN empresa_id_uuid UUID;
UPDATE rol_proceso r
SET empresa_id_uuid = e.id_uuid
FROM empresa e
WHERE r.empresa_id = e.id;
ALTER TABLE rol_proceso
    ALTER COLUMN empresa_id_uuid SET NOT NULL;

ALTER TABLE proceso
    ADD COLUMN empresa_id_uuid UUID;
UPDATE proceso p
SET empresa_id_uuid = e.id_uuid
FROM empresa e
WHERE p.empresa_id = e.id;
ALTER TABLE proceso
    ALTER COLUMN empresa_id_uuid SET NOT NULL;

ALTER TABLE audit_log
    ADD COLUMN empresa_id_uuid UUID;
UPDATE audit_log a
SET empresa_id_uuid = e.id_uuid
FROM empresa e
WHERE a.empresa_id = e.id;
ALTER TABLE audit_log
    ALTER COLUMN empresa_id_uuid SET NOT NULL;

ALTER TABLE mensaje_proceso
    ADD COLUMN empresa_id_uuid UUID;
UPDATE mensaje_proceso m
SET empresa_id_uuid = e.id_uuid
FROM empresa e
WHERE m.empresa_id = e.id;
ALTER TABLE mensaje_proceso
    ALTER COLUMN empresa_id_uuid SET NOT NULL;

ALTER TABLE suscripcion_mensaje
    ADD COLUMN empresa_id_uuid UUID;
UPDATE suscripcion_mensaje s
SET empresa_id_uuid = e.id_uuid
FROM empresa e
WHERE s.empresa_id = e.id;
ALTER TABLE suscripcion_mensaje
    ALTER COLUMN empresa_id_uuid SET NOT NULL;

ALTER TABLE notificacion_externa
    ADD COLUMN empresa_id_uuid UUID;
UPDATE notificacion_externa n
SET empresa_id_uuid = e.id_uuid
FROM empresa e
WHERE n.empresa_id = e.id;
ALTER TABLE notificacion_externa
    ALTER COLUMN empresa_id_uuid SET NOT NULL;

DO $$
DECLARE
    constraint_record RECORD;
BEGIN
    FOR constraint_record IN
        SELECT conrelid::regclass AS table_name, conname
        FROM pg_constraint
        WHERE contype = 'f'
          AND confrelid = 'empresa'::regclass
    LOOP
        EXECUTE format(
            'ALTER TABLE %s DROP CONSTRAINT %I',
            constraint_record.table_name,
            constraint_record.conname
        );
    END LOOP;
END $$;

DO $$
DECLARE
    pk_name TEXT;
BEGIN
    SELECT conname
    INTO pk_name
    FROM pg_constraint
    WHERE conrelid = 'empresa'::regclass
      AND contype = 'p';

    IF pk_name IS NOT NULL THEN
        EXECUTE format('ALTER TABLE empresa DROP CONSTRAINT %I', pk_name);
    END IF;
END $$;

ALTER TABLE usuario DROP COLUMN empresa_id;
ALTER TABLE pool DROP COLUMN empresa_id;
ALTER TABLE invitacion_usuario DROP COLUMN empresa_id;
ALTER TABLE rol_proceso DROP COLUMN empresa_id;
ALTER TABLE proceso DROP COLUMN empresa_id;
ALTER TABLE audit_log DROP COLUMN empresa_id;
ALTER TABLE mensaje_proceso DROP COLUMN empresa_id;
ALTER TABLE suscripcion_mensaje DROP COLUMN empresa_id;
ALTER TABLE notificacion_externa DROP COLUMN empresa_id;
ALTER TABLE empresa DROP COLUMN id;

ALTER TABLE usuario RENAME COLUMN empresa_id_uuid TO empresa_id;
ALTER TABLE pool RENAME COLUMN empresa_id_uuid TO empresa_id;
ALTER TABLE invitacion_usuario RENAME COLUMN empresa_id_uuid TO empresa_id;
ALTER TABLE rol_proceso RENAME COLUMN empresa_id_uuid TO empresa_id;
ALTER TABLE proceso RENAME COLUMN empresa_id_uuid TO empresa_id;
ALTER TABLE audit_log RENAME COLUMN empresa_id_uuid TO empresa_id;
ALTER TABLE mensaje_proceso RENAME COLUMN empresa_id_uuid TO empresa_id;
ALTER TABLE suscripcion_mensaje RENAME COLUMN empresa_id_uuid TO empresa_id;
ALTER TABLE notificacion_externa RENAME COLUMN empresa_id_uuid TO empresa_id;
ALTER TABLE empresa RENAME COLUMN id_uuid TO id;

ALTER TABLE empresa
    ADD CONSTRAINT empresa_pkey PRIMARY KEY (id);

ALTER TABLE usuario
    ADD CONSTRAINT usuario_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES empresa(id);
ALTER TABLE pool
    ADD CONSTRAINT pool_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES empresa(id);
ALTER TABLE invitacion_usuario
    ADD CONSTRAINT invitacion_usuario_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES empresa(id);
ALTER TABLE rol_proceso
    ADD CONSTRAINT rol_proceso_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES empresa(id);
ALTER TABLE proceso
    ADD CONSTRAINT proceso_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES empresa(id);
ALTER TABLE audit_log
    ADD CONSTRAINT audit_log_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES empresa(id);
ALTER TABLE mensaje_proceso
    ADD CONSTRAINT mensaje_proceso_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES empresa(id);
ALTER TABLE suscripcion_mensaje
    ADD CONSTRAINT suscripcion_mensaje_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES empresa(id);
ALTER TABLE notificacion_externa
    ADD CONSTRAINT notificacion_externa_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES empresa(id);

CREATE INDEX idx_usuario_empresa ON usuario(empresa_id);
CREATE INDEX idx_pool_empresa ON pool(empresa_id);
CREATE INDEX idx_invitacion_empresa ON invitacion_usuario(empresa_id);
CREATE INDEX idx_rol_proceso_empresa ON rol_proceso(empresa_id);
CREATE INDEX idx_proceso_empresa ON proceso(empresa_id);
CREATE INDEX idx_proceso_estado_activo ON proceso(empresa_id, estado, activo);
CREATE INDEX idx_audit_empresa ON audit_log(empresa_id);
CREATE INDEX idx_audit_entidad ON audit_log(empresa_id, entidad, entidad_id);
CREATE INDEX idx_mensaje_empresa ON mensaje_proceso(empresa_id);
CREATE INDEX idx_suscripcion_empresa ON suscripcion_mensaje(empresa_id);
CREATE INDEX idx_notif_empresa ON notificacion_externa(empresa_id);
