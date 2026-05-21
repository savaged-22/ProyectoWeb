-- 1. Insertar Empresa Demo
INSERT INTO empresa (id, nombre, nit, email_contacto)
VALUES ('123e4567-e89b-12d3-a456-426614174000', 'Empresa Demo', '123456789-0', 'contacto@empresa.com')
ON CONFLICT (id) DO NOTHING;

-- 2. Insertar Usuario Administrador
INSERT INTO usuario (id, empresa_id, email, password_hash, estado)
VALUES ('51f2d282-08c4-42df-a2a3-a79ae10f528b', '123e4567-e89b-12d3-a456-426614174000', 'admin@empresa.com', '$2b$12$DTmM5aH4j9KLw6qmoBFG0OgxTYVWj0G4LSzonu6KnRBGon9Mw6/Fa', 'activo')
ON CONFLICT (id) DO NOTHING;

-- 3. Insertar Pool Principal
INSERT INTO pool (id, empresa_id, nombre, config_json)
VALUES ('251cea45-5aa2-46c1-9d2b-ecd73782e539', '123e4567-e89b-12d3-a456-426614174000', 'Principal', '{}')
ON CONFLICT (id) DO NOTHING;

-- 4. Insertar Rol de Administrador para el Pool
INSERT INTO rol_pool (id, pool_id, nombre, descripcion, activo, es_propietario)
VALUES ('81f5c6e8-29bf-45bc-9e5c-7b6c5f7e7a8a', '251cea45-5aa2-46c1-9d2b-ecd73782e539', 'Administrador', 'Rol de administrador con acceso completo al pool', true, true)
ON CONFLICT (id) DO NOTHING;

-- 5. Asociar todos los permisos al Rol de Administrador
INSERT INTO rol_pool_permiso (rol_pool_id, permiso_id)
SELECT '81f5c6e8-29bf-45bc-9e5c-7b6c5f7e7a8a', id FROM permiso
ON CONFLICT (rol_pool_id, permiso_id) DO NOTHING;

-- 6. Asignar el Rol de Administrador al Usuario en el Pool
INSERT INTO usuario_rol_pool (usuario_id, rol_pool_id)
VALUES ('51f2d282-08c4-42df-a2a3-a79ae10f528b', '81f5c6e8-29bf-45bc-9e5c-7b6c5f7e7a8a')
ON CONFLICT (usuario_id, rol_pool_id) DO NOTHING;
