# 🚀 Como Rodar o Projeto

## Opção 1: Docker Compose (Recomendado)

### Pré-requisitos:
- Docker e Docker Compose instalados

### Passos:

1. **Navegue até a pasta do projeto:**
```bash
cd envioApi
```

2. **Rode o Docker Compose:**
```bash
docker compose up --build
```

Isso vai:
- Criar e iniciar o MySQL na porta 3307
- Compilar e rodar a aplicação Spring Boot na porta 8080
- Criar automaticamente o usuário admin (login: `admin`, senha: `admin123`)

3. **Aguarde até ver a mensagem:**
```
Started EnvioApiApplication
```

4. **Acesse a API em:**
```
http://localhost:8080
```

---

## Opção 2: Maven Direto (Desenvolvimento)

### Pré-requisitos:
- Java 21 instalado
- Maven instalado
- MySQL rodando (ou use Docker apenas para MySQL)

### Passos:

1. **Inicie o MySQL (se não estiver rodando):**
```bash
cd envioApi
docker compose up mysql -d
```

2. **Configure o `application.properties`** (se necessário):
```properties
spring.datasource.url=jdbc:mysql://localhost:3307/enviorapidoapi
spring.datasource.username=root
spring.datasource.password=39166011
```

3. **Rode a aplicação:**
```bash
mvn spring-boot:run
```

---

## 🎯 Rodar o Front-end

### Em outro terminal:

1. **Navegue até a pasta do front-end:**
```bash
cd envio-frontend
```

2. **Instale as dependências (primeira vez):**
```bash
npm install
```

3. **Inicie o servidor de desenvolvimento:**
```bash
npm start
# ou
ng serve
```

4. **Acesse o front-end em:**
```
http://localhost:4200
```

---

## ✅ Verificar se está tudo funcionando

1. **Back-end rodando:**
   - Acesse: http://localhost:8080
   - Deve retornar erro 401 (normal, precisa de autenticação)

2. **Front-end rodando:**
   - Acesse: http://localhost:4200
   - Deve abrir a tela de login

3. **Fazer login:**
   - Login: `admin`
   - Senha: `admin123`

---

## 🐛 Problemas Comuns

### Porta 8080 já em uso:
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Porta 4200 já em uso:
```bash
# Altere a porta no angular.json ou use:
ng serve --port 4201
```

### MySQL não conecta:
- Verifique se o MySQL está rodando: `docker ps`
- Verifique as credenciais no `application.properties`
- Verifique se a porta 3307 está livre

---

## 📝 Notas Importantes

- O usuário admin é criado **automaticamente** na primeira inicialização
- A senha é criptografada automaticamente
- O front-end está configurado para se conectar em `http://localhost:8080`
- Certifique-se de que o CORS está configurado (já está no código)


