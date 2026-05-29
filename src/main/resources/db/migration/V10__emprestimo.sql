CREATE TABLE IF NOT EXISTS emprestimo (
    id SERIAL PRIMARY KEY,
    id_login BIGINT NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    banco VARCHAR(40) NOT NULL,
    valor_emprestimo NUMERIC(14, 2) NOT NULL,
    quantidade_parcelas INTEGER NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_emprestimo_login FOREIGN KEY (id_login) REFERENCES login_usuario (id_login)
);

CREATE INDEX IF NOT EXISTS idx_emprestimo_login ON emprestimo (id_login);
CREATE INDEX IF NOT EXISTS idx_emprestimo_descricao ON emprestimo (LOWER(descricao));
