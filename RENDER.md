# Render — guia rápido (2 serviços)

Você precisa de **dois serviços** no mesmo repositório, cada um com **Dockerfile diferente**.

---

## Serviço A — Banco (`fffinance-personal-back` ou `fffinance-postgres`)

**Tipo:** Private Service  

| Campo | Valor |
|--------|--------|
| Dockerfile Path | `docker/postgres/Dockerfile` |
| Root Directory | *(vazio)* |

**Não use** `./Dockerfile` aqui — esse é da API Spring.

**Environment (só estas):**

| Key | Value |
|-----|--------|
| `POSTGRES_USER` | `portal` |
| `POSTGRES_PASSWORD` | *(senha forte)* |
| `POSTGRES_DB` | `portal` |

**Disk:** `/var/lib/postgresql/data`

Quando estiver certo, o serviço roda **Postgres na porta 5432**, não Spring Boot.

---

## Serviço B — API (`fffinance-api`)

**Tipo:** Web Service  

| Campo | Valor |
|--------|--------|
| Dockerfile Path | `./Dockerfile` |
| Health Check | `/api/health` |

**Environment:**

| Key | Value |
|-----|--------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://NOME-DO-SERVICO-POSTGRES:5432/portal` |
| `SPRING_DATASOURCE_USERNAME` | `portal` |
| `SPRING_DATASOURCE_PASSWORD` | *(igual ao POSTGRES_PASSWORD)* |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:4200` |
| `APP_UPLOAD_AVATARS_DIR` | `/tmp/fffinance-avatars` *(upload de foto de perfil)* |

Substitua `NOME-DO-SERVICO-POSTGRES` pelo **Name** do Private Service (ex.: `fffinance-personal-back`).

**Foto de perfil:** sem `APP_UPLOAD_AVATARS_DIR`, a API pode falhar com `Erro ao preparar armazenamento` (pasta `data/avatars` sem permissão no Docker). No plano free do Render os ficheiros em `/tmp` somem após redeploy.

---

## Erros que você viu

| Log | Causa |
|-----|--------|
| `jdbcUrl, ${SPRING_DATASOURCE_URL}` | Spring rodando no serviço do **banco** (Dockerfile errado) sem variáveis `SPRING_*` |
| `UnknownHostException: fffinance-postgres` | Host na URL não existe — use o **nome real** do Private Service |
| `Port scan timeout` / `:10000` no Postgres | Private Service subiu a **API** em vez do Postgres |

---

## Ordem

1. Corrigir Private Service (Postgres) → **Live**  
2. Ajustar `SPRING_DATASOURCE_URL` na **fffinance-api**  
3. Manual Deploy da API  
4. `https://fffinance-api.onrender.com/api/health`

---

## Criar usuário de login (banco novo)

Flyway cria as tabelas, mas **não** insere usuário. No **Shell** do Postgres ou via cliente SQL:

1. Gere hash BCrypt da senha (local: classe `BcryptSenhaUtil` nos testes).
2. Execute (ajuste e-mail/senha):

```sql
INSERT INTO login_usuario (nome, email, senha_hash, ativo)
VALUES ('Admin', 'seu@email.com', 'HASH_BCRYPT_AQUI', TRUE);
```
