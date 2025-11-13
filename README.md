# 🚀📦 Envio Rápido API
### API completa para gestão de envios + cálculo automático de frete (MelhorEnvio) + validação real de CEP (ViaCEP)

![Java](https://img.shields.io/badge/Java-21+-red?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-green?style=for-the-badge)
![JWT](https://img.shields.io/badge/JWT-Security-blue?style=for-the-badge)
![Coverage](https://img.shields.io/badge/Test%20Coverage-82%25-brightgreen?style=for-the-badge)
![Status](https://img.shields.io/badge/STATUS-COMPLETO-success?style=for-the-badge)

---

## 📄 Descrição Geral

A **Envio Rápido API** é um sistema backend robusto desenvolvido em **Java 21 + Spring Boot 3** para:

✔ Criar e gerenciar envios  
✔ Validar CEPs usando **ViaCEP**  
✔ Calcular valores de frete usando **MelhorEnvio**  
✔ Possuir autenticação segura com **JWT + Roles (ADMIN e USER)**  
✔ Realizar atualização parcial via **PATCH**  
✔ Oferecer documentação completa via **Postman**  
✔ Garantir qualidade com testes automatizados (**82% coverage**)  

---

## 🧱 Arquitetura da Aplicação

```
src/main/java/com/gft/envioapi
│
├── controller        → Pontos de entrada da API (REST)
├── service           → Regras de negócio (frete, validação, envios)
├── repository        → Acesso ao banco com Spring Data JPA
├── dto               → Dados de entrada e saída
├── client            → Consumo de APIs externas via OpenFeign
├── security          → JWT, filtros e configuração
└── exception         → Tratamento global de erros
```

---

## 🌐 Fluxo Geral da Aplicação

```
Usuário → /auth/login → JWT → /api/envios → Valida CEP → Calcula frete → Resposta final
```

---

## ⚙️ Tecnologias Utilizadas

- Java 21  
- Spring Boot 3  
- JWT + Spring Security  
- JPA + Hibernate  
- OpenFeign  
- JUnit + Mockito + MockMvc  
- Jacoco  
- Maven  

---

## ▶️ Como Rodar o Projeto

### 1️⃣ Clonar o repositório
```
git clone https://github.com/seuusuario/envio-api.git
cd envio-api
```

### 2️⃣ Configurar o application.properties
```
server.port=8080
jwt.secret=5H8pQ2vN9kL3mR7jX4cB6wT1yF0dG8sA2vN5kL9mR3jX7cB4wT6yF1dG0sA8pH5Q
melhorenvio.base-url=https://melhorenvio.com.br
melhorenvio.services=1, 2
melhorenvio.token=Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiMWM5NWQxNDVlZjE4NGJmNWFmM2Q2NzE5NWEzNmMwYjQyYjg1MzQzMzRkOTRhOTg2ZTQ4YWQ4MmU5M2NhZjg3ZWVkNjE5MjhkZjUzYjMyNTgiLCJpYXQiOjE3NjIzNjM4ODguNDMwNTI0LCJuYmYiOjE3NjIzNjM4ODguNDMwNTI1LCJleHAiOjE3OTM4OTk4ODguMzc2MjgzLCJzdWIiOiJhMDQ5MzkzOS0yYmZhLTRhYWEtOTI0Ny0zMDcwYzAwM2Q0ZmMiLCJzY29wZXMiOlsic2hpcHBpbmctY2FsY3VsYXRlIl19.ngaKD1G51LLCaWit-g2WjddBTiZYawN2dUBMU6shOfzPI9wq8-YP0prmV-KdMqQHUwGh7ssZmpXhbD9IYeF362BG_EL3sZGneUwalGaQPp2q0WRL-gyk-0wMhlQgzYgjJKyZWIVn547URXdMhjud8qCYtmsaUD2Rbyz0LUTqRpihLb8v7Vv3Hu0GIm9f7oPoqO0s1_X1OM6P4S-MQnfSvhZwgI4kU6X3m9LJCDhShts86puOJmluEpQdlkOSdOLy_iTaXUlMNgDhzwF9vz_8ufxe9Ph05dvg07vIWzwkikwqMg_x45zpY0VqqFKCrBzhDvElaWkyJrntU-VfdwgzfV_3merx5Skwsn6zt4WZM5zxqfYqitVI3ayqQ6PY6mkWXOl2XrbZVoowP2vvnfCt3eyocXOy3Smy0GjSk8m0E4PI01vUM2m6XWKvKlouL7rTUV1Y6U3N5OxgcboSt-0ir3kr3jtTUJFN7Ps0Z_xJOD_qcmzMG5xDVDhEt9Qvwd5v0DX_eGvvdPufFxmgrkPqn6n7E41IsxDlH7YPTrMJ4Wty_cd0vKIAa9NzfDC0as3xMnPK4q7xmAPUr_9Vvu9FC-T8S1q9K8rkArbAmn8wGIRaccviOafSf7nH-Q2CX69opX4z4bolLI88oLE1fH7LSNX92qI3FXvHg5AV9nKeT-0

```

### 3️⃣ Executar
```
mvn spring-boot:run
```

---

## 🔐 Autenticação JWT

### Registrar
```
POST /auth/register
```

### Login
```
POST /auth/login
```

Header:
```
Authorization: Bearer <token>
```

| Role | Permissões |
|------|------------|
| USER | Listar / Consultar envios |
| ADMIN | Criar / Atualizar / Deletar |

---

## 📚 Endpoints Principais

| Método | Endpoint | Descrição | Role |
|--------|----------|------------|-------|
| POST | /auth/register | Registrar usuário | Público |
| POST | /auth/login | Login | Público |
| GET | /api/envios | Listar envios | USER/ADMIN |
| GET | /api/envios/{id} | Detalhar envio | USER/ADMIN |
| POST | /api/envios | Criar envio | ADMIN |
| PATCH | /api/envios/{id} | Atualizar | ADMIN |
| DELETE | /api/envios/{id} | Deletar | ADMIN |
| HEAD | /api/envios/{id} | Verificar existência | USER/ADMIN |
| OPTIONS | /api/envios | Métodos suportados | Público |

---

## 🧾 Exemplo — Criar Envio

### Request
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

### Response
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

| Código | Motivo |
|--------|---------|
| 400 | CEP inválido |
| 403 | Sem permissão |
| 404 | Envio não encontrado |
| 415 | Content-Type inválido |
| 500 | Erro interno |

---

## 🧪 Testes Automatizados

- Cobertura de **82%**
- Testes de controller, service, segurança e integrações
- Jacoco disponível em:
```
/target/site/jacoco/index.html
```

---

## 📦 Documentação Postman

👉 https://documenter.getpostman.com/view/47278313/2sB3WvLHTf

---

## 👨‍💻 Autor

**Victor Hugo Santos**  
Desenvolvedor Backend — GFT Technologies  
São José dos Campos/SP  
