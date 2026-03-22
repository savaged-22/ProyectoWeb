INSERT INTO permiso (codigo, descripcion) VALUES
    -- Procesos
    ('PROCESO_VER',       'Ver procesos del pool'),
    ('PROCESO_CREAR',     'Crear nuevos procesos'),
    ('PROCESO_EDITAR',    'Editar procesos existentes'),
    ('PROCESO_ELIMINAR',  'Eliminar procesos (soft delete)'),
    ('PROCESO_PUBLICAR',  'Publicar procesos en estado borrador'),
    ('PROCESO_COMPARTIR', 'Compartir procesos con otros pools'),
    -- Diagrama
    ('DIAGRAMA_VER',      'Ver diagrama del proceso'),
    ('DIAGRAMA_EDITAR',   'Editar nodos, arcos y lanes del diagrama'),
    -- Roles del pool
    ('ROL_VER',           'Ver roles del pool'),
    ('ROL_CREAR',         'Crear roles en el pool'),
    ('ROL_EDITAR',        'Editar roles del pool'),
    ('ROL_ELIMINAR',      'Eliminar roles del pool'),
    -- Usuarios
    ('USUARIO_VER',       'Ver usuarios del pool'),
    ('USUARIO_INVITAR',   'Invitar usuarios al pool'),
    ('USUARIO_REVOCAR',   'Revocar acceso de usuarios'),
    -- Administración
    ('POOL_ADMINISTRAR',  'Administrar configuración del pool'),
    ('AUDIT_VER',         'Ver log de auditoría');
