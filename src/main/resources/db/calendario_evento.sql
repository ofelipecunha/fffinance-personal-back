-- Execute no PostgreSQL antes de subir o back com ddl-auto=validate.

CREATE TABLE IF NOT EXISTS calendario_evento (
    id SERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    data_evento DATE NOT NULL,
    tipo VARCHAR(20) NOT NULL DEFAULT 'NOTACAO',
    concluido BOOLEAN DEFAULT FALSE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
