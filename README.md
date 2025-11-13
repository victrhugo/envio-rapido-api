# 📦 Envio Rápido API

API REST desenvolvida em **Java + Spring Boot**, responsável por criar e consultar **envios** com **cálculo automático de frete** via **MelhorEnvio**, além de **validação real de CEP** com a API **ViaCEP**.

---

## 🚀 Principais Recursos
- Autenticação JWT (Bearer Token)  
- Perfis de acesso: **ADMIN** e **USER**  
- CRUD completo de envios  
- Recalculo automático de frete em atualizações  
- Validação de CEP (ViaCEP)  
- Tratamento global de exceções  
- Testes unitários com alta cobertura (MockMvc + Mockito)  

---

## ⚙️ Tecnologias Utilizadas
- **Java 21**
- **Spring Boot 3**
- **Spring Security + JWT**
- **Spring Data JPA**
- **OpenFeign** (integrações externas)
- **JUnit 5 + Mockito**
- **Maven**

---

## ▶️ Como Rodar o Projeto

### Pré-requisitos:
- JDK 21+
- Maven 3.9+
- Conta no [MelhorEnvio](https://www.melhorenvio.com.br/) para gerar token

### Passos:
1. **Clone o repositório**
   ```bash
   git clone <seu-repo>.git
   cd envioApi
   ```
2. **Configure o arquivo `application.properties`:**
   ```properties
   server.port=8080
   jwt.secret=5H8pQ2vN9kL3mR7jX4cB6wT1yF0dG8sA2vN5kL9mR3jX7cB4wT6yF1dG0sA8pH5Q
   melhorenvio.base-url=https://melhorenvio.com.br
   melhorenvio.services=1, 2
   melhorenvio.token= Bearer {token}
   ```
3. **Execute o projeto**
   ```bash
   mvn spring-boot:run
   ```
4. Acesse:  
   👉 `http://localhost:8080`

---

## 🔐 Autenticação

### 1. Registro
`POST /auth/register`  
Cria um novo usuário (login, senha, role)

### 2. Login
`POST /auth/login`  
Retorna o token JWT para ser usado nas demais rotas:
```
Authorization: Bearer <seu_token>
```

### Perfis:
| Role  | Permissões |
|--------|-------------|
| USER   | Consultar e listar envios |
| ADMIN  | Criar, atualizar e deletar envios |

---

## 🧾 Exemplo de Requisição

### `POST /api/envios`

**Body (JSON):**
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

**Response (201 Created):**
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

## 📚 Endpoints Principais

| Método | Endpoint | Descrição | Autorização |
|--------|-----------|------------|--------------|
| POST | `/auth/register` | Registrar usuário | Pública |
| POST | `/auth/login` | Login e gerar token | Pública |
| GET | `/api/envios` | Listar todos os envios | ADMIN / USER |
| GET | `/api/envios/{id}` | Buscar envio por ID | ADMIN / USER |
| POST | `/api/envios` | Criar envio com cálculo de frete | ADMIN |
| PATCH | `/api/envios/{id}` | Atualizar parcialmente | ADMIN |
| DELETE | `/api/envios/{id}` | Excluir envio | ADMIN |

---

## 🧪 Testes
- Testes unitários e de integração com **MockMvc + Mockito**
- Cobertura atual: **≈71%**  
- Para executar:
  ```bash
  mvn test
  ```
- Arquivo de relatório:  
  `target/site/jacoco/index.html`

---

## 🧰 Extras
- `HEAD /api/envios/{id}` → retorna metadados do envio  
- `OPTIONS /api/envios` → retorna métodos suportados  

---

## 📦 Documentação Postman
Coleção disponível com todos os endpoints, exemplos de resposta, variáveis (`base_url`, `token_admin`) e scripts automáticos de login.  
👉 [Acessar documentação Postman](https://documenter.getpostman.com/view/47278313/2sB3WvLHTf)

---

## 👨‍💻 Autor
**Victor Hugo Santos**  
Desenvolvedor Java Backend • GFT Technologies  
📍 São José dos Campos, SP  
