-- Campos de perfil/endereço (alinhado ao front).

ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS endereco VARCHAR(255);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS cidade VARCHAR(100);
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS estado CHAR(2);

ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS imagem_url VARCHAR(500);

-- Perfil padrão USUARIO para novos registos
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS perfil VARCHAR(30);
UPDATE login_usuario SET perfil = 'USUARIO' WHERE perfil IS NULL OR TRIM(perfil) = '';
ALTER TABLE login_usuario ALTER COLUMN perfil SET DEFAULT 'USUARIO';

-- Copia caminho da foto antiga
UPDATE login_usuario SET imagem_url = imagem WHERE imagem_url IS NULL AND imagem IS NOT NULL AND TRIM(imagem) <> '';

-- ativo em CHAR(1) S/N (converte de BOOLEAN se ainda for boolean)
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name = 'login_usuario' AND column_name = 'ativo'
      AND data_type = 'boolean'
  ) THEN
    ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS ativo_sn CHAR(1) DEFAULT 'S';
    UPDATE login_usuario SET ativo_sn = CASE WHEN ativo IS TRUE THEN 'S' ELSE 'N' END;
    ALTER TABLE login_usuario DROP COLUMN ativo;
    ALTER TABLE login_usuario RENAME COLUMN ativo_sn TO ativo;
    ALTER TABLE login_usuario ALTER COLUMN ativo SET DEFAULT 'S';
    ALTER TABLE login_usuario ALTER COLUMN ativo SET NOT NULL;
  ELSIF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name = 'login_usuario' AND column_name = 'ativo'
  ) THEN
    ALTER TABLE login_usuario ADD COLUMN ativo CHAR(1) NOT NULL DEFAULT 'S';
  END IF;
END $$;

UPDATE login_usuario SET ativo = 'S' WHERE ativo IS NULL OR TRIM(ativo::text) = '';
