-- Execute no PostgreSQL antes de subir o back com ddl-auto=validate.
-- SERIAL = INTEGER no PostgreSQL; use java.lang.Integer no @Id.

CREATE TABLE IF NOT EXISTS categoria (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE NOT NULL
);

INSERT INTO categoria (nome, tipo)
SELECT v.nome, v.tipo
FROM (
    VALUES
        ('Energia', 'DESPESA'),
        ('Água', 'DESPESA'),
        ('Mercado', 'DESPESA'),
        ('Aluguel', 'DESPESA'),
        ('Restaurante', 'DESPESA'),
        ('Uber', 'DESPESA'),
        ('Streaming', 'DESPESA')
) AS v(nome, tipo)
WHERE NOT EXISTS (SELECT 1 FROM categoria c WHERE c.nome = v.nome);
