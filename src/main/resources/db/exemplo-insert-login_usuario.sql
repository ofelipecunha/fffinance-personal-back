-- 1) Gere senha_hash com BCrypt (obrigatório: a API compara com BCryptPasswordEncoder.matches).
--    No IDE: execute a classe de teste com main com.example.portal.BcryptSenhaUtil
--    ou no código: new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("123")
-- 2) Ajuste nome/e-mail e cole o hash na linha abaixo.

INSERT INTO login_usuario (nome, email, senha_hash, ativo)
VALUES (
    'Usuário teste',
    'teste@portal.com',
    'COLE_AQUI_O_HASH_BCRYPT',
    TRUE
);
