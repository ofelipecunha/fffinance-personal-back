-- Hibernate valida String como VARCHAR; converte CHAR/bpchar criado na V5 ou manualmente.

ALTER TABLE login_usuario
    ALTER COLUMN ativo TYPE VARCHAR(1) USING TRIM(ativo::text);

ALTER TABLE login_usuario
    ALTER COLUMN estado TYPE VARCHAR(2) USING NULLIF(TRIM(estado::text), '');
