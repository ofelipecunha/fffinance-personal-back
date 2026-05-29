-- Execute no PostgreSQL antes de subir o back com ddl-auto=validate.
-- Exige tabelas categoria e forma_pagamento já criadas.

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
