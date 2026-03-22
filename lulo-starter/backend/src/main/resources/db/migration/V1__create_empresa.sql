CREATE TABLE empresa (
    id             SERIAL PRIMARY KEY,
    nombre         VARCHAR(255) NOT NULL,
    nit            VARCHAR(50)  NOT NULL UNIQUE,
    email_contacto VARCHAR(255),
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);
