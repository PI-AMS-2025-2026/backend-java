# Integracao Frontend - Autenticacao JWT

## 1. Login

Endpoint:
- POST /auth/login

Request:
```json
{
  "email": "admin@fatec.local",
  "senha": "123456"
}
```

Response 200:
```json
{
  "tokenType": "Bearer",
  "accessToken": "<jwt>",
  "expiresIn": 3600,
  "role": "ADMIN"
}
```

## 2. Uso do token Bearer

Envie o token em todas as chamadas protegidas:

Authorization: Bearer <jwt>

Exemplo com fetch:
```javascript
const response = await fetch('/usuarios', {
  headers: {
    Authorization: `Bearer ${token}`
  }
});
```

## 3. Codigos de erro de autenticacao/autorizacao

- 401 Unauthorized:
  - token ausente em endpoint protegido
  - token invalido
  - token expirado
- 403 Forbidden:
  - usuario autenticado sem permissao para o endpoint/metodo

## 4. Matriz endpoint x papel

### Endpoints publicos

- POST /auth/login
- GET /swagger-ui/**
- GET /v3/api-docs/**

### Usuarios e tipos de usuario

- GET /usuarios/**: ADMIN
- POST /usuarios/**: ADMIN
- PUT /usuarios/**: ADMIN
- DELETE /usuarios/**: ADMIN
- GET /tipo-usuario/**: ADMIN
- POST /tipo-usuario/**: ADMIN
- PUT /tipo-usuario/**: ADMIN
- DELETE /tipo-usuario/**: ADMIN

### Recursos de negocio

Endpoints:
- /curso/**
- /dias-semana/**
- /horarios/**
- /periodos-letivos/**
- /recursos/**
- /tipos-sala/**

Regras:
- GET: ADMIN, COORDENADOR, PROFESSOR
- POST: ADMIN, COORDENADOR
- PUT: ADMIN, COORDENADOR
- DELETE: ADMIN, COORDENADOR

Observacao importante:
- A granularidade exata de recursos autorizados para COORDENADOR e PROFESSOR deve ser refinada na matriz oficial do produto; o ponto de ajuste esta centralizado na configuracao de seguranca.

## 5. Apoio a desenvolvimento e testes

Em ambientes dev/test, existe um mecanismo opcional e controlado para facilitar testes de autorizacao:
- Header tecnico: X-Debug-Role (ADMIN|COORDENADOR|PROFESSOR)
- So funciona com app.security.debug-role-enabled=true
- Em producao, o uso deste mecanismo e bloqueado por fail-fast na inicializacao

## 6. Credenciais locais para dev/test

- admin@fatec.local / 123456
- coordenador@fatec.local / 123456
- professor@fatec.local / 123456
