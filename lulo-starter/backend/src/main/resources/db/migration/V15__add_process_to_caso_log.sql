ALTER TABLE caso_log ADD COLUMN proceso_id UUID;
ALTER TABLE caso_log ADD CONSTRAINT fk_caso_log_proceso FOREIGN KEY (proceso_id) REFERENCES proceso(id);
ALTER TABLE caso_log ADD COLUMN estado VARCHAR(50);
ALTER TABLE caso_log ALTER COLUMN caso_id DROP NOT NULL;
