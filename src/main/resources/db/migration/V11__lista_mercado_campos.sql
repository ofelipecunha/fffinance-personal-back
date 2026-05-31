ALTER TABLE lista ADD COLUMN IF NOT EXISTS id_login BIGINT;
ALTER TABLE lista ADD COLUMN IF NOT EXISTS data_compra DATE DEFAULT CURRENT_DATE;
ALTER TABLE lista ADD COLUMN IF NOT EXISTS finalizada BOOLEAN DEFAULT FALSE;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_lista_login_usuario') THEN
    ALTER TABLE lista
      ADD CONSTRAINT fk_lista_login_usuario
      FOREIGN KEY (id_login) REFERENCES login_usuario (id_login);
  END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_lista_login_data ON lista (id_login, data_compra);

UPDATE lista SET data_compra = CURRENT_DATE WHERE data_compra IS NULL;
UPDATE lista SET finalizada = FALSE WHERE finalizada IS NULL;
