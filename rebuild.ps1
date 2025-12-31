Write-Host "Parando containers..." -ForegroundColor Yellow
docker compose down

Write-Host ""
Write-Host "Reconstruindo containers com as correcoes de CORS..." -ForegroundColor Green
docker compose up --build --force-recreate


