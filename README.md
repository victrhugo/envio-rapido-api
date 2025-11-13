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
✔ Autenticação segura com **JWT + Roles (ADMIN e USER)**  
✔ Suporte a atualização parcial via **PATCH**  
✔ Documentação completa via **Postman**  
✔ Qualidade garantida com **82% de cobertura de testes**

---

## 🧱 Arquitetura da Aplicação

```
src/main/java/com/gft/envioapi
│
├── controller        → Endpoints REST
├── service           → Regras de negócio
├── repository        → Persistência com JPA
├── dto               → Objetos de transferência
├── client            → OpenFeign (MelhorEnvio / ViaCEP)
├── security          → JWT + filtros
└── exception         → Tratamento global de erros
```

---

## 🌐 Fluxo Geral

```
Usuário → /auth/login → Recebe JWT → /api/envios → Validação de CEP → Cálculo de frete → Resposta final
```

---

# 🛠️ Configuração do `application.properties`

O arquivo **application.properties real NÃO é versionado** (está no `.gitignore`).  
No projeto existe apenas o arquivo de exemplo:

```
src/main/resources/application-example.properties
```

### ▶️ Como configurar:

1. Copie o arquivo:
```
application-example.properties → application.properties
```

2. Preencha com suas credenciais:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/enviorapidoapi
spring.datasource.username=root
spring.datasource.password=SUA_SENHA_AQUI

jwt.secret=SUA_SECRET_KEY

melhorenvio.token=Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiMWM5NWQxNDVlZjE4NGJmNWFmM2Q2NzE5NWEzNmMwYjQyYjg1MzQzMzRkOTRhOTg2ZTQ4YWQ4MmU5M2NhZjg3ZWVkNjE5MjhkZjUzYjMyNTgiLCJpYXQiOjE3NjIzNjM4ODguNDMwNTI0LCJuYmYiOjE3NjIzNjM4ODguNDMwNTI1LCJleHAiOjE3OTM4OTk4ODguMzc2MjgzLCJzdWIiOiJhMDQ5MzkzOS0yYmZhLTRhYWEtOTI0Ny0zMDcwYzAwM2Q0ZmMiLCJzY29wZXMiOlsic2hpcHBpbmctY2FsY3VsYXRlIl19.ngaKD1G51LLCaWit-g2WjddBTiZYawN2dUBMU6shOfzPI9wq8-YP0prmV-KdMqQHUwGh7ssZmpXhbD9IYeF362BG_EL3sZGneUwalGaQPp2q0WRL-gyk-0wMhlQgzYgjJKyZWIVn547URXdMhjud8qCYtmsaUD2Rbyz0LUTqRpihLb8v7Vv3Hu0GIm9f7oPoqO0s1_X1OM6P4S-MQnfSvhZwgI4kU6X3m9LJCDhShts86puOJmluEpQdlkOSdOLy_iTaXUlMNgDhzwF9vz_8ufxe9Ph05dvg07vIWzwkikwqMg_x45zpY0VqqFKCrBzhDvElaWkyJrntU-VfdwgzfV_3merx5Skwsn6zt4WZM5zxqfYqitVI3ayqQ6PY6mkWXOl2XrbZVoowP2vvnfCt3eyocXOy3Smy0GjSk8m0E4PI01vUM2m6XWKvKlouL7rTUV1Y6U3N5OxgcboSt-0ir3kr3jtTUJFN7Ps0Z_xJOD_qcmzMG5xDVDhEt9Qvwd5v0DX_eGvvdPufFxmgrkPqn6n7E41IsxDlH7YPTrMJ4Wty_cd0vKIAa9NzfDC0as3xMnPK4q7xmAPUr_9Vvu9FC-T8S1q9K8rkArbAmn8wGIRaccviOafSf7nH-Q2CX69opX4z4bolLI88oLE1fH7LSNX92qI3FXvHg5AV9nKeT-0

melhorenvio.services=1,2
```

⚠️ **Nunca commitar o application.properties real!**

---

## ▶️ Executando o projeto

### 1. Clone o repositório
```
git clone https://github.com/seuusuario/envio-api.git
cd envio-api
```

### 2. Configure o `application.properties`

### 3. Rode o projeto
```
mvn spring-boot:run
```

---

# 🔐 Autenticação JWT

### Registrar
```
POST /auth/register
```

### Login
```
POST /auth/login
```

Envie o token no header:
```
Authorization: Bearer <token>
```

| Perfil | Permissões |
|--------|------------|
| USER | Consultar / listar envios |
| ADMIN | Criar / atualizar / excluir |

---

# 📚 Endpoints Principais

| Método | Rota | Descrição | Acesso |
|--------|-------|-----------|---------|
| POST | /auth/register | Registrar usuário | Público |
| POST | /auth/login | Login | Público |
| GET | /api/envios | Listar envios | USER/ADMIN |
| GET | /api/envios/{id} | Buscar envio | USER/ADMIN |
| POST | /api/envios | Criar envio | ADMIN |
| PATCH | /api/envios/{id} | Atualizar | ADMIN |
| DELETE | /api/envios/{id} | Remover | ADMIN |
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

## 🧪 Testes Automatizados

- **82% de cobertura**
- Jacoco em:
```
target/site/jacoco/index.html
```

---

## 📦 Documentação Postman

👉 https://documenter.getpostman.com/view/47278313/2sB3WvLHTf

---

## 👨‍💻 Autor

**Victor Hugo Santos**  
Desenvolvedor Backend — GFT Technologies  
São José dos Campos/SP
