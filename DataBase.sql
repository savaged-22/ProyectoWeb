-- DROP SCHEMA public;

CREATE SCHEMA public AUTHORIZATION pg_database_owner;

COMMENT ON SCHEMA public IS 'standard public schema';

-- DROP SEQUENCE public.arco_id_seq;

CREATE SEQUENCE public.arco_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.audit_log_id_seq;

CREATE SEQUENCE public.audit_log_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.empresa_id_seq;

CREATE SEQUENCE public.empresa_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.invitacion_usuario_id_seq;

CREATE SEQUENCE public.invitacion_usuario_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.lane_id_seq;

CREATE SEQUENCE public.lane_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.nodo_id_seq;

CREATE SEQUENCE public.nodo_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.permiso_id_seq;

CREATE SEQUENCE public.permiso_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.pool_id_seq;

CREATE SEQUENCE public.pool_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.proceso_compartido_id_seq;

CREATE SEQUENCE public.proceso_compartido_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.proceso_id_seq;

CREATE SEQUENCE public.proceso_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.rol_pool_id_seq;

CREATE SEQUENCE public.rol_pool_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.rol_proceso_id_seq;

CREATE SEQUENCE public.rol_proceso_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.usuario_id_seq;

CREATE SEQUENCE public.usuario_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;-- public.empresa definition

-- Drop table

-- DROP TABLE public.empresa;

CREATE TABLE public.empresa (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	nombre varchar NOT NULL,
	nit varchar NOT NULL,
	email_contacto varchar NOT NULL,
	created_at timestamp NOT NULL,
	CONSTRAINT empresa_pk PRIMARY KEY (id)
);


-- public.permiso definition

-- Drop table

-- DROP TABLE public.permiso;

CREATE TABLE public.permiso (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	codigo varchar NOT NULL,
	descripcion varchar NOT NULL,
	CONSTRAINT permiso_pk PRIMARY KEY (id),
	CONSTRAINT permiso_unique UNIQUE (codigo)
);


-- public.pool definition

-- Drop table

-- DROP TABLE public.pool;

CREATE TABLE public.pool (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	empresa_id int4 NOT NULL,
	nombre varchar NOT NULL,
	config_json varchar NOT NULL,
	created_at varchar NOT NULL,
	CONSTRAINT pool_pk PRIMARY KEY (id),
	CONSTRAINT pool_empresa_fk FOREIGN KEY (empresa_id) REFERENCES public.empresa(id)
);


-- public.rol_pool definition

-- Drop table

-- DROP TABLE public.rol_pool;

CREATE TABLE public.rol_pool (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	pool_id int4 NOT NULL,
	nombre varchar NOT NULL,
	descripcion varchar NOT NULL,
	activo bool NOT NULL,
	es_propietario bool NOT NULL,
	created_at varchar NOT NULL,
	CONSTRAINT rol_pool_pk PRIMARY KEY (id),
	CONSTRAINT rol_pool_pool_fk FOREIGN KEY (pool_id) REFERENCES public.pool(id)
);


-- public.rol_pool_permiso definition

-- Drop table

-- DROP TABLE public.rol_pool_permiso;

CREATE TABLE public.rol_pool_permiso (
	rol_pool_id int4 NOT NULL,
	permiso_id int4 NOT NULL,
	CONSTRAINT rol_pool_permiso_pk PRIMARY KEY (rol_pool_id, permiso_id),
	CONSTRAINT rol_pool_permiso_permiso_fk FOREIGN KEY (permiso_id) REFERENCES public.permiso(id),
	CONSTRAINT rol_pool_permiso_rol_pool_fk FOREIGN KEY (rol_pool_id) REFERENCES public.rol_pool(id)
);


-- public.rol_proceso definition

-- Drop table

-- DROP TABLE public.rol_proceso;

CREATE TABLE public.rol_proceso (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	empresa_id int4 NOT NULL,
	nombre varchar NOT NULL,
	descripcion varchar NOT NULL,
	activo bool NOT NULL,
	created_at varchar NOT NULL,
	CONSTRAINT rol_proceso_pk PRIMARY KEY (id),
	CONSTRAINT rol_proceso_empresa_fk FOREIGN KEY (empresa_id) REFERENCES public.empresa(id)
);


-- public.usuario definition

-- Drop table

-- DROP TABLE public.usuario;

CREATE TABLE public.usuario (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	empresa_id int4 NOT NULL,
	email varchar NOT NULL,
	password_hash varchar NOT NULL,
	estado varchar NOT NULL,
	created_at varchar NOT NULL,
	CONSTRAINT usuario_pk PRIMARY KEY (id),
	CONSTRAINT usuario_unique UNIQUE (email),
	CONSTRAINT usuario_empresa_fk FOREIGN KEY (empresa_id) REFERENCES public.empresa(id)
);


-- public.usuario_rol_pool definition

-- Drop table

-- DROP TABLE public.usuario_rol_pool;

CREATE TABLE public.usuario_rol_pool (
	usuario_id int4 NOT NULL,
	rol_pool_id int4 NOT NULL,
	created_at varchar NOT NULL,
	CONSTRAINT usuario_rol_pool_pk PRIMARY KEY (usuario_id, rol_pool_id),
	CONSTRAINT usuario_rol_pool_rol_pool_fk FOREIGN KEY (rol_pool_id) REFERENCES public.rol_pool(id),
	CONSTRAINT usuario_rol_pool_usuario_fk FOREIGN KEY (usuario_id) REFERENCES public.usuario(id)
);


-- public.audit_log definition

-- Drop table

-- DROP TABLE public.audit_log;

CREATE TABLE public.audit_log (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	empresa_id int4 NOT NULL,
	usuario_id int4 NOT NULL,
	entidad varchar NOT NULL,
	entidad_id int4 NOT NULL,
	accion varchar NOT NULL,
	diff_json varchar NOT NULL,
	created_at varchar NOT NULL,
	CONSTRAINT audit_log_pk PRIMARY KEY (id),
	CONSTRAINT audit_log_empresa_fk FOREIGN KEY (empresa_id) REFERENCES public.empresa(id),
	CONSTRAINT audit_log_usuario_fk FOREIGN KEY (usuario_id) REFERENCES public.usuario(id)
);


-- public.invitacion_usuario definition

-- Drop table

-- DROP TABLE public.invitacion_usuario;

CREATE TABLE public.invitacion_usuario (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	empresa_id int4 NOT NULL,
	rol_pool_id int4 NOT NULL,
	created_by_user_id int4 NOT NULL,
	email varchar NOT NULL,
	token_hash varchar NOT NULL,
	estado varchar NOT NULL,
	expires_at varchar NOT NULL,
	created_at varchar NOT NULL,
	CONSTRAINT invitacion_usuario_pk PRIMARY KEY (id),
	CONSTRAINT invitacion_usuario_empresa_fk FOREIGN KEY (empresa_id) REFERENCES public.empresa(id),
	CONSTRAINT invitacion_usuario_rol_pool_fk FOREIGN KEY (rol_pool_id) REFERENCES public.rol_pool(id),
	CONSTRAINT invitacion_usuario_usuario_fk FOREIGN KEY (created_by_user_id) REFERENCES public.usuario(id)
);


-- public.proceso definition

-- Drop table

-- DROP TABLE public.proceso;

CREATE TABLE public.proceso (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	empresa_id int4 NOT NULL,
	pool_id int4 NOT NULL,
	created_by_user_id int4 NOT NULL,
	nombre varchar NOT NULL,
	descripcion varchar NOT NULL,
	categoria varchar NOT NULL,
	estado varchar NOT NULL,
	activo bool NOT NULL,
	created_at varchar NOT NULL,
	updated_at varchar NOT NULL,
	CONSTRAINT proceso_pk PRIMARY KEY (id),
	CONSTRAINT proceso_empresa_fk FOREIGN KEY (empresa_id) REFERENCES public.empresa(id),
	CONSTRAINT proceso_pool_fk FOREIGN KEY (pool_id) REFERENCES public.pool(id),
	CONSTRAINT proceso_usuario_fk FOREIGN KEY (created_by_user_id) REFERENCES public.usuario(id)
);


-- public.proceso_compartido definition

-- Drop table

-- DROP TABLE public.proceso_compartido;

CREATE TABLE public.proceso_compartido (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	proceso_id int4 NOT NULL,
	pool_destino_id int4 NOT NULL,
	created_by_user_id int4 NOT NULL,
	permiso varchar NOT NULL,
	created_at varchar NOT NULL,
	CONSTRAINT proceso_compartido_pk PRIMARY KEY (id),
	CONSTRAINT proceso_compartido_pool_fk FOREIGN KEY (pool_destino_id) REFERENCES public.pool(id),
	CONSTRAINT proceso_compartido_proceso_fk FOREIGN KEY (proceso_id) REFERENCES public.proceso(id),
	CONSTRAINT proceso_compartido_usuario_fk FOREIGN KEY (created_by_user_id) REFERENCES public.usuario(id)
);


-- public.lane definition

-- Drop table

-- DROP TABLE public.lane;

CREATE TABLE public.lane (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	proceso_id int4 NOT NULL,
	rol_proceso_id int4 NOT NULL,
	nombre varchar NOT NULL,
	orden int4 NOT NULL,
	created_at varchar NOT NULL,
	CONSTRAINT lane_pk PRIMARY KEY (id),
	CONSTRAINT lane_proceso_fk FOREIGN KEY (proceso_id) REFERENCES public.proceso(id),
	CONSTRAINT lane_rol_proceso_fk FOREIGN KEY (rol_proceso_id) REFERENCES public.rol_proceso(id)
);


-- public.nodo definition

-- Drop table

-- DROP TABLE public.nodo;

CREATE TABLE public.nodo (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	proceso_id int4 NOT NULL,
	lane_id int4 NOT NULL,
	tipo varchar NOT NULL,
	"label" varchar NOT NULL,
	pos_x float8 NOT NULL,
	pos_y float8 NOT NULL,
	created_at varchar NOT NULL,
	CONSTRAINT nodo_pk PRIMARY KEY (id),
	CONSTRAINT nodo_lane_fk FOREIGN KEY (lane_id) REFERENCES public.lane(id),
	CONSTRAINT nodo_proceso_fk FOREIGN KEY (proceso_id) REFERENCES public.proceso(id)
);


-- public.actividad definition

-- Drop table

-- DROP TABLE public.actividad;

CREATE TABLE public.actividad (
	nodo_id int4 NOT NULL,
	tipo_actividad varchar NOT NULL,
	props_json varchar NOT NULL,
	CONSTRAINT actividad_pk PRIMARY KEY (nodo_id),
	CONSTRAINT actividad_nodo_fk FOREIGN KEY (nodo_id) REFERENCES public.nodo(id)
);


-- public.arco definition

-- Drop table

-- DROP TABLE public.arco;

CREATE TABLE public.arco (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	proceso_id int4 NOT NULL,
	from_nodo_id int4 NOT NULL,
	to_nodo_id int4 NOT NULL,
	condicion_expr varchar NOT NULL,
	props_json varchar NOT NULL,
	activo bool NOT NULL,
	created_at varchar NOT NULL,
	CONSTRAINT arco_pk PRIMARY KEY (id),
	CONSTRAINT arco_nodo_fk FOREIGN KEY (from_nodo_id) REFERENCES public.nodo(id),
	CONSTRAINT arco_nodo_fk_1 FOREIGN KEY (to_nodo_id) REFERENCES public.nodo(id),
	CONSTRAINT arco_proceso_fk FOREIGN KEY (proceso_id) REFERENCES public.proceso(id)
);


-- public.gateway definition

-- Drop table

-- DROP TABLE public.gateway;

CREATE TABLE public.gateway (
	nodo_id int4 NOT NULL,
	tipo_gateway varchar NOT NULL,
	config_json varchar NOT NULL,
	CONSTRAINT gateway_pk PRIMARY KEY (nodo_id),
	CONSTRAINT gateway_nodo_fk FOREIGN KEY (nodo_id) REFERENCES public.nodo(id)
);