# 🔧 RECONSTRUIR DOCKER - CORREÇÃO DO PRAZO

## ⚠️ IMPORTANTE

O problema do prazo está corrigido no código, mas o Docker precisa ser **reconstruído** para aplicar a correção!

## 📋 Passos OBRIGATÓRIOS:

### 1. Pare o Docker:
```bash
cd envioApi
docker compose down
```

### 2. Remova a imagem antiga (importante!):
```bash
docker rmi envio-rapido:1.0
```

### 3. Reconstrua COMPLETAMENTE:
```bash
docker compose build --no-cache
docker compose up
```

### 4. Aguarde até ver:
```
Started EnvioApiApplication
```

### 5. Teste criando um NOVO envio

## ✅ O que foi corrigido:

1. ✅ `FreteCalculatorService.java` - Agora usa `getPrazoEfetivo()` em vez de `getPrecoEfetivo()` para o prazo
2. ✅ `docker-compose.yml` - Agora constrói a imagem a partir do código fonte
3. ✅ Adicionado log de debug para verificar valores

## 🎯 Após reconstruir:

1. **Delete o envio antigo** (se ainda existir)
2. **Crie um novo envio**
3. **Verifique** - Agora deve mostrar: `R$ 17.39 - 2 dias` (ou o prazo correto)

## ⚡ Comando Rápido (copie e cole tudo):

```bash
cd envioApi
docker compose down
docker rmi envio-rapido:1.0
docker compose build --no-cache
docker compose up
```

**IMPORTANTE:** O `--no-cache` garante que a imagem será reconstruída do zero!


