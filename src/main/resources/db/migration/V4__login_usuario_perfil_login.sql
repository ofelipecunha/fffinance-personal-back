ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS perfil VARCHAR(20) NOT NULL DEFAULT 'ADMIN';
ALTER TABLE login_usuario ADD COLUMN IF NOT EXISTS login VARCHAR(150);

UPDATE login_usuario SET login = email WHERE login IS NULL OR TRIM(login) = '';
UPDATE login_usuario SET perfil = 'ADMIN' WHERE perfil IS NULL OR TRIM(perfil) = '';
