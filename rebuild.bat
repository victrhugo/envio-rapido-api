@echo off
echo Parando containers...
docker compose down

echo.
echo Reconstruindo containers com as correcoes de CORS...
docker compose up --build --force-recreate

pause


