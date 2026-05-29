-- Tabelas que antes eram scripts manuais em db/*.sql (necessárias com ddl-auto=validate).

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

CREATE TABLE IF NOT EXISTS lancamento (
    id SERIAL PRIMARY KEY,
    descricao VARCHAR(255),
    valor NUMERIC(10, 2) NOT NULL,
    data_lancamento DATE NOT NULL,
    categoria_id INTEGER NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    forma_pagamento VARCHAR(50),
    pago BOOLEAN DEFAULT FALSE NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    forma_pagamento_id INTEGER,
    CONSTRAINT fk_lancamento_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT fk_lancamento_forma_pagamento FOREIGN KEY (forma_pagamento_id) REFERENCES forma_pagamento (id)
);

CREATE TABLE IF NOT EXISTS calendario_evento (
    id SERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    data_evento DATE NOT NULL,
    tipo VARCHAR(20) NOT NULL DEFAULT 'NOTACAO',
    concluido BOOLEAN DEFAULT FALSE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pessoa (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS documento (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(255),
    nome_arquivo VARCHAR(255) NOT NULL,
    tipo_arquivo VARCHAR(50),
    tamanho BIGINT,
    caminho_arquivo TEXT,
    pessoa_id INTEGER NOT NULL,
    data_upload TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_documento_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa (id)
);
