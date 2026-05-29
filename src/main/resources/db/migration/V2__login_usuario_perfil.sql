-- Tabela de login (banco novo no Render não tinha CREATE — só ALTER em versões antigas).

CREATE TABLE IF NOT EXISTS login_usuario (
    id_login BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    token VARCHAR(500),
    data_criacao TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    imagem VARCHAR(2048),
    sobrenome VARCHAR(150),
    telefone VARCHAR(40),
    bio VARCHAR(500),
    cargo VARCHAR(150),
    localizacao VARCHAR(255),
    pais VARCHAR(120),
    cidade_estado VARCHAR(255),
    cep VARCHAR(30),
    tax_id VARCHAR(80),
    url_facebook VARCHAR(500),
    url_twitter VARCHAR(500),
    url_linkedin VARCHAR(500),
    url_instagram VARCHAR(500)
);

-- Bases já existentes (só colunas antigas): adiciona perfil sem quebrar.
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS imagem VARCHAR(2048);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS sobrenome VARCHAR(150);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS telefone VARCHAR(40);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS bio VARCHAR(500);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS cargo VARCHAR(150);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS localizacao VARCHAR(255);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS pais VARCHAR(120);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS cidade_estado VARCHAR(255);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS cep VARCHAR(30);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS tax_id VARCHAR(80);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS url_facebook VARCHAR(500);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS url_twitter VARCHAR(500);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS url_linkedin VARCHAR(500);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS url_instagram VARCHAR(500);
