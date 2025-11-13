# 📦 Envio Rápido API
### Sistema de gestão de envios com cálculo automático de frete • Spring Boot + MelhorEnvio + ViaCEP

![Java](https://img.shields.io/badge/Java-21+-red)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-brightgreen)
![JUnit](https://img.shields.io/badge/Tests-JUnit%205-blue)
![Coverage](https://img.shields.io/badge/Coverage-80%25-green)
![Status](https://img.shields.io/badge/Project-Completed-success)

---

## 🚀 Sobre o Projeto
A **Envio Rápido API** é uma aplicação backend completa em **Java + Spring Boot** focada em:
- Cálculo automático de fretes usando **MelhorEnvio**
- Validação real de CEP com **ViaCEP**
- Autenticação e autorização com **JWT**
- CRUD completo de envios
- Testes unitários com MockMvc + Mockito
- Tratamento global de exceções
- Estrutura sólida seguindo boas práticas de arquitetura

---

## 🧱 Arquitetura da Aplicação

A API segue uma arquitetura em camadas clara e organizada:

```
src/main/java/com/gft/envioapi
│
├── controller        → Controle das requisições HTTP
├── service           → Regras de negócio e integrações externas
├── repository        → Persistência JPA
├── dto               → Estruturas de requisição/response
├── client            → Interfaces Feign (ViaCEP / MelhorEnvio)
├── security          → JWT, filtros e autenticação
└── exception         → Tratamento global de erros
```

---

## ⚙️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3**
- **Spring Security + JWT**
- **Spring Data JPA**
- **OpenFeign**
- **Mockito + JUnit 5 + MockMvc**
- **Maven**

---

## ▶️ Como Rodar o Projeto

### ✅ Pré-requisitos
- JDK 21+
- Maven 3.9+
- Conta no **MelhorEnvio** para gerar token

### 📌 1. Clone o repositório
```
git clone <seu-repo>.git
cd envioApi
```

### 📌 2. Configure o `application.properties`

```
server.port=8080

jwt.secret=5H8pQ2vN9kL3mR7jX4cB6wT1yF0dG8sA2vN5kL9mR3jX7cB4wT6yF1dG0sA8pH5Q

melhorenvio.base-url=https://melhorenvio.com.br
melhorenvio.services=1, 2
melhorenvio.token=Bearer {SEU_TOKEN}
```

### 📌 3. Execute
```
mvn spring-boot:run
```

API disponível em:  
👉 **http://localhost:8080**

---

## 🔐 Autenticação JWT

Antes de acessar `/api/envios`, gere o token:

### 📌 Registro  
```
POST /auth/register
```

### 📌 Login  
```
POST /auth/login
```

Header obrigatório:
```
Authorization: Bearer <token>
```

---

## 🧠 Perfis e Permissões

| Role  | Permissões |
|-------|------------|
| **USER** | Listar / Consultar envios |
| **ADMIN** | Criar / Atualizar / Deletar |

---

## 📚 Endpoints Principais

| Método | Endpoint | Descrição | Permissão |
|--------|----------|------------|-----------|
| POST | `/auth/register` | Registrar usuário | Público |
| POST | `/auth/login` | Gerar token JWT | Público |
| GET | `/api/envios` | Listar envios | USER / ADMIN |
| GET | `/api/envios/{id}` | Buscar por ID | USER / ADMIN |
| POST | `/api/envios` | Criar envio | ADMIN |
| PATCH | `/api/envios/{id}` | Atualização parcial | ADMIN |
| DELETE | `/api/envios/{id}` | Excluir | ADMIN |
| HEAD | `/api/envios/{id}` | Metadados | USER / ADMIN |
| OPTIONS | `/api/envios` | Métodos suportados | Público |

---

## 🧾 Exemplo — Criação de Envio

### Request  
```
POST /api/envios
```

### Body  
```json
{
  "nomeRemetente": "Victor Hugo",
  "endereco": "Rua Gisele Martins, 1191",
  "cepOrigem": "69907650",
  "cepDestino": "98502140",
  "larguraCaixa": 10.0,
  "alturaCaixa": 12.0,
  "comprimentoCaixa": 20.0,
  "peso": 5.0
}
```

### Response (201)  
```json
{
  "nomeRemetente": "Victor Hugo",
  "cepOrigem": "69907650",
  "cepDestino": "98502140",
  "frete": {
    "valorPAC": "57.20",
    "prazoPAC": "5",
    "valorSEDEX": "148.64",
    "prazoSEDEX": "2"
  },
  "mensagem": "Cálculo de frete realizado com sucesso"
}
```

---

## ❗ Erros Comuns

| Código | Quando ocorre |
|--------|----------------|
| **400** | CEP inválido / dados errados |
| **403** | Usuário sem permissão |
| **404** | Envio não encontrado |
| **406** | Accept inválido |
| **415** | Content-Type incorreto |
| **500** | Erro inesperado |

---

## 🧪 Testes

- Testes com **MockMvc**
- Mock de serviços
- Testes do filtro JWT
- Cobertura desejada: **80%**

Para rodar:

```
mvn test
```

Relatório:
```
target/site/jacoco/index.html
```

---

## 📦 Documentação Postman

Coleção completa com:
- Scripts automáticos
- Variáveis (`base_url`)
- Exemplos de requests/responses

👉 **Link:** *adicione aqui o link da sua coleção*

---

## 👨‍💻 Autor

**Victor Hugo Santos**  
Desenvolvedor Backend — GFT Technologies  
📍 São José dos Campos, SP

---

Se quiser, posso gerar também:
- Uma **versão em inglês**
- Uma **versão resumida**
- Uma **versão com emojis reduzidos**
- Banner visual para colocar no topo
