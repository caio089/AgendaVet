@echo off
REM Inicia o servidor AgendaVet (API REST + frontend)
REM Requer Maven e Java 17+ instalados

cd /d "%~dp0"

mvn -q compile exec:java
