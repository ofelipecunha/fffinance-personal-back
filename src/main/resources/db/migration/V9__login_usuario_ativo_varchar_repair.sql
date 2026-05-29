-- Garante tipos compatíveis com Hibernate (String) em produção se V6 não tiver sido aplicada.

ALTER TABLE login_usuario
    ALTER COLUMN ativo TYPE VARCHAR(1) USING TRIM(ativo::text);

ALTER TABLE login_usuario
    ALTER COLUMN estado TYPE VARCHAR(2) USING NULLIF(TRIM(estado::text), '');
