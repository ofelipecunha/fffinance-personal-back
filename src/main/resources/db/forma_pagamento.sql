-- Execute no PostgreSQL (banco configurado em application.properties) antes de subir o back com ddl-auto=validate.
-- SERIAL = INTEGER no PostgreSQL; no JPA use java.lang.Integer para o @Id (Long mapeia como BIGINT e falha na validação).

CREATE TABLE IF NOT EXISTS forma_pagamento (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    tipo VARCHAR(30),
    ativo BOOLEAN DEFAULT TRUE NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

INSERT INTO forma_pagamento (nome, tipo)
SELECT v.nome, v.tipo
FROM (
    VALUES
        ('Dinheiro', 'DINHEIRO'),
        ('PIX', 'DIGITAL'),
        ('Cartão de Crédito', 'CREDITO'),
        ('Cartão de Débito', 'DEBITO'),
        ('Boleto', 'DIGITAL')
) AS v(nome, tipo)
WHERE NOT EXISTS (SELECT 1 FROM forma_pagamento fp WHERE fp.nome = v.nome);
