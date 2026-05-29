-- Categorias de receita para a tela Continhas (combo não ficar vazio em RECEITA).

INSERT INTO categoria (nome, tipo)
SELECT v.nome, v.tipo
FROM (
    VALUES
        ('Salário', 'RECEITA'),
        ('Freelance', 'RECEITA'),
        ('Investimentos', 'RECEITA'),
        ('Outras receitas', 'RECEITA')
) AS v(nome, tipo)
WHERE NOT EXISTS (SELECT 1 FROM categoria c WHERE c.nome = v.nome);
