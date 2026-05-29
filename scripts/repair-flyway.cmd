@echo off
REM Corrige checksum do Flyway apos alterar arquivos V*.sql ja aplicados no banco local.
cd /d "%~dp0.."
call mvnw.cmd -q flyway:repair
echo.
echo Concluido. Suba a aplicacao de novo no IntelliJ.
