CREATE TABLE IF NOT EXISTS anotacao_mercado (
    id SERIAL PRIMARY KEY,
    id_login BIGINT REFERENCES login_usuario (id_login),
    titulo VARCHAR(120) NOT NULL,
    data_criacao TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    convertida BOOLEAN NOT NULL DEFAULT FALSE,
    lista_id INTEGER REFERENCES lista (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_anotacao_login_aberta ON anotacao_mercado (id_login, convertida, data_criacao DESC);

CREATE TABLE IF NOT EXISTS item_anotacao (
    id SERIAL PRIMARY KEY,
    anotacao_id INTEGER NOT NULL REFERENCES anotacao_mercado (id) ON DELETE CASCADE,
    nome VARCHAR(150) NOT NULL,
    quantidade INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_item_anotacao ON item_anotacao (anotacao_id);
