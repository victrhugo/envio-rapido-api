# 🔧 Solução de Problemas de Login

## Problema: Login não funciona com admin/admin123

### Solução 1: Verificar se o usuário foi criado

1. **Verifique os logs da aplicação ao iniciar:**
   - Procure por mensagens como:
     - `✅ Usuário admin criado com sucesso!`
     - `ℹ️  Usuário admin já existe no banco de dados.`

2. **Se não aparecer nenhuma mensagem:**
   - O `DataInitializer` pode não ter executado
   - Verifique se a aplicação iniciou completamente

### Solução 2: Criar usuário admin manualmente via API

**Opção A - Usando o endpoint de criação:**
```bash
curl -X POST http://localhost:8080/auth/create-admin
```

**Opção B - Usando o endpoint de registro:**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "login": "admin",
    "senha": "admin123",
    "role": "ADMIN"
  }'
```

### Solução 3: Verificar o banco de dados diretamente

Se você tem acesso ao MySQL:

```sql
-- Conectar ao banco
mysql -u root -p -h localhost -P 3307

-- Usar o banco
USE enviorapidoapi;

-- Verificar usuários
SELECT * FROM users;

-- Criar usuário manualmente (se necessário)
-- A senha precisa ser criptografada com BCrypt
-- Use o endpoint da API para criar corretamente
```

### Solução 4: Verificar credenciais

Certifique-se de usar exatamente:
- **Login:** `admin` (minúsculo, sem espaços)
- **Senha:** `admin123` (minúsculo, sem espaços)

### Solução 5: Verificar se a API está rodando

1. **Teste se a API está respondendo:**
```bash
curl http://localhost:8080/auth/login
# Deve retornar erro 400 (Bad Request) - isso é normal
```

2. **Teste o login via curl:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "admin",
    "senha": "admin123"
  }'
```

**Resposta esperada (sucesso):**
```json
{
  "token": "eyJhbGc...",
  "login": "admin"
}
```

**Resposta de erro:**
- `Usuário não encontrado` → Usuário não existe
- `Senha inválida` → Senha está errada
- `Usuário sem role definida` → Problema na criação do usuário

### Solução 6: Recriar o banco de dados

Se nada funcionar, você pode recriar o banco:

1. **Parar a aplicação**

2. **Remover o volume do MySQL:**
```bash
docker compose down -v
```

3. **Iniciar novamente:**
```bash
docker compose up --build
```

Isso vai criar um banco novo e o usuário admin será criado automaticamente.

---

## 🔍 Debug Avançado

### Verificar logs detalhados

Adicione no `application.properties`:
```properties
logging.level.com.gft.envioapi.configuration.DataInitializer=DEBUG
logging.level.com.gft.envioapi.security=DEBUG
```

### Verificar se o DataInitializer está sendo executado

Procure nos logs por:
- `Criando usuário admin padrão...`
- `✅ Usuário admin criado com sucesso!`

Se não aparecer, o componente pode não estar sendo carregado.

---

## ✅ Checklist de Verificação

- [ ] Aplicação Spring Boot está rodando na porta 8080
- [ ] MySQL está rodando e acessível
- [ ] Logs mostram que o usuário foi criado
- [ ] Credenciais estão corretas (admin/admin123)
- [ ] Front-end está conectando em http://localhost:8080
- [ ] CORS está configurado corretamente

---

## 📞 Se ainda não funcionar

1. Verifique os logs completos da aplicação
2. Teste o login via curl/Postman primeiro
3. Verifique se há erros no console do navegador (F12)
4. Certifique-se de que está usando as credenciais corretas


