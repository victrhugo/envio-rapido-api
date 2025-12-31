# 🚨 URGENTE: Reconstruir Docker para Corrigir CORS

## ⚠️ O problema é CORS - você PRECISA reconstruir o Docker!

O erro que você está vendo é porque o Docker está usando código antigo que não tem as correções de CORS.

## 📋 Passos OBRIGATÓRIOS:

### 1. Pare o Docker atual:
```bash
cd envioApi
docker compose down
```

### 2. Reconstrua COMPLETAMENTE:
```bash
docker compose up --build --force-recreate
```

### 3. Aguarde até ver:
```
Started EnvioApiApplication
```

### 4. Verifique se o usuário admin foi criado:
Procure nos logs por:
```
✅ Usuário admin criado com sucesso!
```

## ✅ O que foi corrigido:

1. ✅ Filtro CORS adicional criado (`CorsFilter.java`)
2. ✅ Requisições OPTIONS permitidas
3. ✅ JwtAuthFilter não bloqueia mais OPTIONS
4. ✅ Headers CORS configurados corretamente

## 🔍 Após reconstruir:

1. **Recarregue a página do front-end** (Ctrl+F5)
2. **Tente fazer login** com:
   - Login: `admin`
   - Senha: `admin123`
3. **Se o admin não existir**, clique no botão "Criar Usuário Admin" primeiro

## ⚡ Comando Rápido (copie e cole tudo):

```bash
cd envioApi
docker compose down
docker compose up --build --force-recreate
```

**IMPORTANTE:** Aguarde até a aplicação iniciar completamente antes de testar!


