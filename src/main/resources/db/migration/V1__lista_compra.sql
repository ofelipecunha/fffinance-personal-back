-- Tabelas `lista` e `item_lista` (total = qtd × valor unitário é calculado no Hibernate com @Formula).
-- `tipo_lista` pode já existir na base; CREATE IF NOT EXISTS + colunas opcionais.

CREATE TABLE IF NOT EXISTS tipo_lista (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(32) NOT NULL UNIQUE,
    nome VARCHAR(100) NOT NULL,
    icone VARCHAR(64),
    cor VARCHAR(32),
    ordem INTEGER NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

ALTER TABLE tipo_lista ADD COLUMN IF NOT EXISTS codigo VARCHAR(32);
ALTER TABLE tipo_lista ADD COLUMN IF NOT EXISTS icone VARCHAR(64);
ALTER TABLE tipo_lista ADD COLUMN IF NOT EXISTS cor VARCHAR(32);
ALTER TABLE tipo_lista ADD COLUMN IF NOT EXISTS ordem INTEGER DEFAULT 0;
ALTER TABLE tipo_lista ADD COLUMN IF NOT EXISTS ativo BOOLEAN DEFAULT TRUE;

CREATE TABLE IF NOT EXISTS lista (
    id SERIAL PRIMARY KEY,
    tipo_id INTEGER NOT NULL REFERENCES tipo_lista (id),
    nome VARCHAR(150) NOT NULL,
    data_criacao TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_lista_tipo ON lista (tipo_id);
CREATE INDEX IF NOT EXISTS idx_lista_data ON lista (data_criacao DESC);

CREATE TABLE IF NOT EXISTS item_lista (
    id SERIAL PRIMARY KEY,
    lista_id INTEGER NOT NULL REFERENCES lista (id) ON DELETE CASCADE,
    nome VARCHAR(150) NOT NULL,
    quantidade INTEGER NOT NULL DEFAULT 1 CHECK (quantidade > 0),
    valor_unitario NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (valor_unitario >= 0)
);

CREATE INDEX IF NOT EXISTS idx_item_lista ON item_lista (lista_id);

INSERT INTO tipo_lista (codigo, nome, icone, cor, ordem, ativo)
SELECT v.codigo, v.nome, v.icone, v.cor, v.ordem, v.ativo
FROM (
    VALUES
        ('MERCADO', 'Mercado', 'shopping_cart', '#ea580c', 10, TRUE),
        ('ESCOLINHA', 'Escolinha', 'school', '#6366f1', 20, TRUE),
        ('CASA', 'Casa', 'home', '#4680ff', 30, TRUE),
        ('TRANSPORTE', 'Transporte', 'directions_car', '#64748b', 40, TRUE),
        ('SAUDE', 'Saúde', 'medical_services', '#2ca87f', 50, TRUE),
        ('LAZER', 'Lazer', 'celebration', '#e58a00', 60, TRUE)
) AS v (codigo, nome, icone, cor, ordem, ativo)
WHERE NOT EXISTS (SELECT 1 FROM tipo_lista t WHERE t.codigo = v.codigo);
