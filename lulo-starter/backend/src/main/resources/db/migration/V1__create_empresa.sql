CREATE TABLE empresa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre         VARCHAR(255) NOT NULL,
    nit            VARCHAR(50)  NOT NULL UNIQUE,
    email_contacto VARCHAR(255),
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);
