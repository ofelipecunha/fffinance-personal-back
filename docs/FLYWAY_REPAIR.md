# Flyway: checksum mismatch (V2)

## O que aconteceu

O banco local já executou `V2__login_usuario_perfil.sql` com uma versão antiga do arquivo.
O arquivo foi alterado no projeto → o Flyway bloqueia a subida:

```
Migration checksum mismatch for migration version 2
```

Isso **não é erro de senha/criptografia** — é só o histórico do Flyway desatualizado.

## Correção no banco (você já fez)

```sql
UPDATE flyway_schema_history
SET checksum = -1534918783
WHERE version = '2';
```

Depois suba o `PortalApplication` de novo. O Flyway deve aplicar **V4** e **V5** se ainda não constarem no histórico:

```sql
SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;
```

## Alternativa: Maven

```cmd
cd portal\portal
scripts\repair-flyway.cmd
```

(Requer `JAVA_HOME` configurado.)

## Alternativa: SQL (DBeaver / pgAdmin)

```sql
UPDATE flyway_schema_history
SET checksum = -1534918783
WHERE version = '2';
```

Depois suba o Spring Boot de novo.

## Render (produção)

Se o mesmo erro aparecer no Render após deploy, no Shell do Postgres:

```sql
UPDATE flyway_schema_history
SET checksum = -1534918783
WHERE version = '2';
```

Ou use `flyway repair` no pipeline — **não** altere migrações já aplicadas em produção sem combinar com repair.

## Render (produção)

Se a API cair com `ativo` em `login_usuario` (`bpchar` vs `boolean`/`varchar`):

1. Confirme no log do deploy: `Migrating schema ... to version "8"` (ou `"9"`), não só até `"3"`.
2. Se o histórico parar em `3`, faça **Clear build cache** no Render e redeploy do serviço `fffinance-api`.
3. Após subir, o histórico deve listar V4–V9; a V6/V9 convertem `ativo` para `VARCHAR(1)` compatível com a entidade Java.
