-- Continhas: lançamentos por utilizador + data de pagamento

ALTER TABLE lancamento ADD COLUMN IF NOT EXISTS id_login BIGINT;
ALTER TABLE lancamento ADD COLUMN IF NOT EXISTS data_pagamento DATE;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'fk_lancamento_login_usuario'
  ) THEN
    ALTER TABLE lancamento
      ADD CONSTRAINT fk_lancamento_login_usuario
      FOREIGN KEY (id_login) REFERENCES login_usuario (id_login);
  END IF;
END $$;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'chk_lancamento_tipo'
  ) THEN
    ALTER TABLE lancamento
      ADD CONSTRAINT chk_lancamento_tipo
      CHECK (tipo IN ('RECEITA', 'DESPESA'));
  END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_lancamento_usuario_data
  ON lancamento (id_login, data_lancamento);

CREATE INDEX IF NOT EXISTS idx_lancamento_categoria
  ON lancamento (categoria_id);

CREATE INDEX IF NOT EXISTS idx_lancamento_forma_pagamento
  ON lancamento (forma_pagamento_id);
