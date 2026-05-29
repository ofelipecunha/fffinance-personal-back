-- Campos de perfil alinhados às telas de perfil (resumo, dados pessoais, endereço e redes).
-- imagem: URL ou caminho relativo servido estaticamente (ex.: /uploads/avatar-1.jpg)

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
