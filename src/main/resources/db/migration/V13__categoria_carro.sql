INSERT INTO categoria (nome, tipo)
SELECT v.nome, v.tipo
FROM (
    VALUES ('CARRO', 'DESPESA')
) AS v(nome, tipo)
WHERE NOT EXISTS (SELECT 1 FROM categoria c WHERE UPPER(TRIM(c.nome)) = 'CARRO');
