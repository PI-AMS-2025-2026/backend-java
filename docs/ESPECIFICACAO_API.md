# Especificação de API - Sistema GINI
**Data**: 2026-06-01  
**Versão**: 1.0 (MVP)  
**Base URL**: `http://localhost:8080/api`

---

## 1. AUTENTICAÇÃO

### 1.1 Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "admin@fatec.edu.br",
  "senha": "senha123"
}

Response 200 OK:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "email": "admin@fatec.edu.br",
    "nome": "Admin",
    "role": "ADMIN"
  }
}

Response 401 Unauthorized:
{
  "erro": "Credenciais inválidas"
}
```

### 1.2 Headers Necessários
```
Authorization: Bearer {token}
Content-Type: application/json
```

---

## 2. DADOS MESTRES (CRUD Básico)

### 2.1 Professores

#### Listar Professores
```http
GET /professores?ativo=true&page=0&size=10

Response 200 OK:
{
  "content": [
    {
      "id": 1,
      "nome": "João da Silva",
      "email": "joao@fatec.edu.br",
      "ativo": true,
      "disciplinasAtas": [1, 2, 3]
    }
  ],
  "totalElements": 45,
  "totalPages": 5,
  "currentPage": 0
}

Filtros:
- ativo: true|false
- page: número página (0-based)
- size: itens por página (10-50)
```

#### Criar Professor
```http
POST /professores
Content-Type: application/json

{
  "nome": "Novo Professor",
  "email": "novo@fatec.edu.br"
}

Response 201 Created:
{
  "id": 50,
  "nome": "Novo Professor",
  "email": "novo@fatec.edu.br",
  "ativo": true
}

Response 400 Bad Request:
{
  "campo": "email",
  "erro": "Email já cadastrado"
}
```

#### Editar Professor
```http
PUT /professores/1
Content-Type: application/json

{
  "nome": "João Silva Atualizado",
  "email": "joao.silva@fatec.edu.br"
}

Response 200 OK: { ... professor atualizado ... }
Response 404 Not Found: { "erro": "Professor não encontrado" }
Response 409 Conflict: { "erro": "Professor tem alocações ativas" }
```

#### Inativar Professor (Soft Delete)
```http
DELETE /professores/1?motivo=Saiu da instituição

Response 204 No Content (sucesso)
Response 409 Conflict:
{
  "erro": "Não pode inativar professor com alocações em grades ativas"
}
```

#### Listar Disponibilidades de Professor
```http
GET /professores/1/disponibilidades?periodoId=1

Response 200 OK:
{
  "periodo": {
    "id": 1,
    "codigo": "2026/1",
    "dataInicio": "2026-03-01",
    "dataFim": "2026-06-30"
  },
  "disponibilidades": [
    {
      "id": 1,
      "diaSemana": "SEGUNDA",
      "horario": {
        "id": 1,
        "inicio": "08:00",
        "fim": "10:00",
        "descricao": "Manhã 1"
      }
    },
    {
      "id": 2,
      "diaSemana": "SEGUNDA",
      "horario": {
        "id": 2,
        "inicio": "14:00",
        "fim": "16:00",
        "descricao": "Tarde 1"
      }
    }
  ]
}
```

#### Definir Disponibilidades (Semestral)
```http
POST /professores/1/disponibilidades
Content-Type: application/json

{
  "periodoId": 1,
  "disponibilidades": [
    { "diaSemana": "SEGUNDA", "horarioId": 1 },
    { "diaSemana": "SEGUNDA", "horarioId": 2 },
    { "diaSemana": "TERÇA", "horarioId": 2 },
    { "diaSemana": "QUARTA", "horarioId": 3 }
  ]
}

Response 201 Created:
{
  "message": "Disponibilidades definidas com sucesso",
  "disponibilidades": [...]
}
```

---

### 2.2 Salas

#### Listar Salas
```http
GET /salas?tipoSalaId=1&ativo=true&page=0&size=10

Response 200 OK:
{
  "content": [
    {
      "id": 1,
      "nome": "LAB-01",
      "capacidade": 30,
      "tipoSala": {
        "id": 1,
        "nome": "Laboratório"
      },
      "ativo": true
    }
  ],
  "totalElements": 15,
  "totalPages": 2
}
```

#### Criar Sala
```http
POST /salas
Content-Type: application/json

{
  "nome": "LAB-02",
  "capacidade": 30,
  "tipoSalaId": 1
}

Response 201 Created:
{
  "id": 2,
  "nome": "LAB-02",
  "capacidade": 30,
  "tipoSala": {
    "id": 1,
    "nome": "Laboratório"
  },
  "ativo": true
}
```

#### Inativar Sala
```http
DELETE /salas/1?motivo=Demolição em andamento

Response 204 No Content
Response 409 Conflict: { "erro": "Sala tem alocações em grades ativas" }
```

---

### 2.3 Disciplinas

#### Listar Disciplinas
```http
GET /disciplinas?cursoId=1&page=0&size=10

Response 200 OK:
{
  "content": [
    {
      "id": 1,
      "nome": "Programação II",
      "cargaHoraria": 60,
      "tipoSala": {
        "id": 1,
        "nome": "Laboratório"
      },
      "curso": {
        "id": 1,
        "nome": "ADS"
      }
    }
  ],
  "totalElements": 20,
  "totalPages": 2
}
```

#### Criar Disciplina
```http
POST /disciplinas
Content-Type: application/json

{
  "nome": "Programação II",
  "cargaHoraria": 60,
  "tipoSalaId": 1,
  "cursoId": 1
}

Response 201 Created: { ... }
```

---

### 2.4 Turmas

#### Listar Turmas
```http
GET /turmas?cursoId=1&periodoId=1&page=0&size=10

Response 200 OK:
{
  "content": [
    {
      "id": 1,
      "nome": "2ADS-2026",
      "quantidadeAlunos": 35,
      "periodo": {
        "id": 1,
        "codigo": "2026/1"
      },
      "curso": {
        "id": 1,
        "nome": "ADS"
      }
    }
  ],
  "totalElements": 8,
  "totalPages": 1
}
```

#### Criar Turma
```http
POST /turmas
Content-Type: application/json

{
  "nome": "2ADS-2026",
  "quantidadeAlunos": 35,
  "periodoId": 1,
  "cursoId": 1
}

Response 201 Created: { ... }

Validação:
- nome: deve seguir padrão {PERIODO}{CURSO}-{ANO} (ex: 2ADS-2026)
```

---

## 3. GRADE HORÁRIA

### 3.1 Criar Grade

```http
POST /grades-horarias
Content-Type: application/json

{
  "cursoId": 1,
  "periodoId": 1,
  "versao": 1,
  "descricao": "Grade Semestre 2026/1"
}

Response 201 Created:
{
  "id": 1,
  "codigo": "ADS-2026-1-v1",
  "curso": { "id": 1, "nome": "ADS" },
  "periodo": { "id": 1, "codigo": "2026/1" },
  "status": "ATIVO",
  "versao": 1,
  "dataCriacao": "2026-06-01T10:30:00",
  "usuarioCriacao": { "id": 1, "email": "admin@fatec.edu.br" },
  "alocacoes": []
}

RBAC:
- ADMIN: pode criar para qualquer curso
- COORDENADOR: pode criar apenas para seu curso
```

### 3.2 Copiar Grade de Período Anterior

```http
POST /grades-horarias/{id}/copiar
Content-Type: application/json

{
  "periodoDestinoId": 2,
  "cursoDestinoId": 1
}

Response 201 Created:
{
  "id": 2,
  "codigo": "ADS-2026-2-v1",
  "alocacoesCopiadas": 45,
  "alocacoesRemovidas": 5,
  "alocacoesComErro": [
    {
      "alocacaoOriginal": "...",
      "motivo": "Professor não existe mais"
    }
  ],
  "mensagem": "Grade copiada com sucesso. 45 alocações copiadas, 5 removidas (professor inativado, sala demolida)"
}

Limpeza Automática:
- Remove professores inativados
- Remove salas não existentes
- Remove alocações com conflitos
- Mantém máximo possível de alocações válidas
```

### 3.3 Listar Grades

```http
GET /grades-horarias?cursoId=1&status=ATIVO&page=0&size=10

Response 200 OK:
{
  "content": [
    {
      "id": 1,
      "codigo": "ADS-2026-1-v1",
      "curso": { "id": 1, "nome": "ADS" },
      "periodo": { "id": 1, "codigo": "2026/1" },
      "status": "ATIVO",
      "versao": 1,
      "alocacoes": 45,
      "dataCriacao": "2026-06-01T10:30:00"
    }
  ]
}

Filtros:
- cursoId: required (COORDENADOR usa seu curso automaticamente)
- status: ATIVO|INATIVO
- page, size: paginação
```

### 3.4 Exportar Grade

#### Exportar PDF
```http
GET /grades-horarias/1/exportar/pdf?layout=horario

Response 200 OK (application/pdf):
[arquivo PDF - tabela horária pronta para imprimir]
```

#### Exportar Excel
```http
GET /grades-horarias/1/exportar/excel?layout=horario

Response 200 OK (application/vnd.ms-excel):
[arquivo Excel - tabela editável]
```

---

## 4. ALOCAÇÕES

### 4.1 Criar Alocação

```http
POST /alocacoes
Content-Type: application/json

{
  "gradeId": 1,
  "turmaId": 1,
  "disciplinaId": 1,
  "professorId": 1,
  "salaId": 1,
  "diaSemana": "SEGUNDA",
  "horarioId": 1
}

Response 201 Created:
{
  "id": 1,
  "grade": { "id": 1, "codigo": "ADS-2026-1-v1" },
  "turma": { "id": 1, "nome": "2ADS-2026" },
  "disciplina": { "id": 1, "nome": "Programação II" },
  "professor": { "id": 1, "nome": "João da Silva" },
  "sala": { "id": 1, "nome": "LAB-01" },
  "diaSemana": "SEGUNDA",
  "horario": { "id": 1, "inicio": "08:00", "fim": "10:00" },
  "dataCriacao": "2026-06-01T11:00:00"
}

Response 409 Conflict:
{
  "erro": "Professor não disponível neste horário"
}

Validações Automáticas:
1. ✅ Referências obrigatórias (turma, disciplina, professor, sala, grade)
2. ✅ Compatibilidade disciplina.tipoSala == sala.tipoSala
3. ✅ Disponibilidade professor (lista semestral)
4. ✅ Conflito horário professor (não pode estar em 2 lugares)
5. ✅ Conflito horário sala (não pode estar em 2 lugares)
6. ✅ Conflito horário turma (não pode estar em 2 lugares)
7. ✅ Capacidade sala >= quantidade alunos turma
8. ✅ Carga horária professor <= 8h POR DIA
9. ✅ Descanso de 12h entre jornadas do professor
10. ✅ Grade em status ATIVO
11. ✅ Não duplicidade (mesma alocação 2x)
12. ✅ Coerência: turma.curso == grade.curso
13. ✅ Vínculo professor apto para disciplina
```

### 4.2 Validar Alocação (sem salvar)

```http
GET /alocacoes/validar?turmaId=1&disciplinaId=1&professorId=1&salaId=1&diaSemana=SEGUNDA&horarioId=1&gradeId=1

Response 200 OK:
{
  "valido": false,
  "erros": [
    "Professor não disponível neste horário",
    "Sala não tem capacidade para turma"
  ],
  "avisos": []
}

Response 200 OK (válido):
{
  "valido": true,
  "erros": [],
  "avisos": []
}

Características:
- GET (sem persistência)
- Reusa toda lógica de validação de POST
- Útil para teste antes de confirmar
- Sem modificar banco
```

### 4.3 Editar Alocação

```http
PUT /alocacoes/1
Content-Type: application/json

{
  "salaId": 2,
  "motivo": "Sala original foi demolida"
}

Response 200 OK:
{
  "id": 1,
  "sala": { "id": 2, "nome": "LAB-02" },
  "dataPrimeiraAlocacao": "2026-06-01T11:00:00",
  "dataUltimaEdicao": "2026-06-01T14:30:00"
}

Validações:
- Mesmas de POST
- Motivo OBRIGATÓRIO (será registrado em auditoria)
- Registra mudança em histórico (HistoricoAlocacao)

Response 400 Bad Request:
{
  "campo": "motivo",
  "erro": "Motivo é obrigatório"
}
```

### 4.4 Deletar Alocação

```http
DELETE /alocacoes/1?motivo=Alocação duplicada

Response 204 No Content (sucesso)

Body com motivo (via JSON se preferir):
{
  "motivo": "Alocação duplicada"
}

Auditoria:
- Motivo OBRIGATÓRIO
- Registra em HistoricoAlocacao
- Soft delete ou hard delete conforme política

Response 400 Bad Request:
{
  "campo": "motivo",
  "erro": "Motivo é obrigatório"
}
```

### 4.5 Listar Alocações

```http
GET /alocacoes?gradeId=1&professorId=1&salaId=1&diaSemana=SEGUNDA&page=0&size=20

Response 200 OK:
{
  "content": [
    {
      "id": 1,
      "grade": { "id": 1 },
      "turma": { "id": 1, "nome": "2ADS-2026" },
      "disciplina": { "id": 1, "nome": "Programação II" },
      "professor": { "id": 1, "nome": "João" },
      "sala": { "id": 1, "nome": "LAB-01" },
      "diaSemana": "SEGUNDA",
      "horario": { "inicio": "08:00", "fim": "10:00" },
      "dataCriacao": "2026-06-01T11:00:00"
    }
  ],
  "totalElements": 45,
  "totalPages": 3
}

Filtros:
- gradeId: obrigatório para COORDENADOR
- professorId, salaId, diaSemana: opcionais
- page, size: paginação

RBAC:
- ADMIN: vê todos alocações
- COORDENADOR: vê apenas alocações de seu curso
```

### 4.6 Ver Histórico de Alocação

```http
GET /alocacoes/1/historico?page=0&size=10

Response 200 OK:
{
  "content": [
    {
      "id": 1,
      "tipo": "CRIACAO",
      "usuario": { "id": 1, "email": "admin@fatec.edu.br" },
      "data": "2026-06-01T11:00:00",
      "motivo": null,
      "detalhes": null
    },
    {
      "id": 2,
      "tipo": "EDICAO",
      "usuario": { "id": 2, "email": "coord@fatec.edu.br" },
      "data": "2026-06-01T14:30:00",
      "motivo": "Sala original foi demolida",
      "detalhes": {
        "campo_anterior": { "salaId": 1, "sala": "LAB-01" },
        "campo_novo": { "salaId": 2, "sala": "LAB-02" }
      }
    },
    {
      "id": 3,
      "tipo": "DELECAO",
      "usuario": { "id": 2, "email": "coord@fatec.edu.br" },
      "data": "2026-06-02T09:00:00",
      "motivo": "Alocação duplicada",
      "detalhes": null
    }
  ],
  "totalElements": 3
}

Tipos de Operação: CRIACAO, EDICAO, DELECAO
```

---

## 5. AUDITORIA E RELATÓRIOS

### 5.1 Auditoria Completa (ADMIN only)

```http
GET /auditoria?tipo=EDICAO&dataInicio=2026-06-01&dataFim=2026-06-30&usuarioId=2&page=0&size=20

Response 200 OK:
{
  "content": [
    {
      "id": 1,
      "entidade": "Alocacao",
      "entidadeId": 1,
      "tipo": "EDICAO",
      "usuario": { "id": 2, "email": "coord@fatec.edu.br" },
      "data": "2026-06-01T14:30:00",
      "motivo": "Sala original foi demolida",
      "ip": "192.168.1.100"
    }
  ],
  "totalElements": 150,
  "totalPages": 8
}

Filtros:
- tipo: CRIACAO, EDICAO, DELECAO
- dataInicio, dataFim: range de datas
- usuarioId, entidade: opcionais

RBAC:
- ADMIN: vê auditoria completa (todos cursos)
- COORDENADOR: ❌ acesso negado (403)
```

### 5.2 Histórico Coordenador

```http
GET /grades-horarias/1/historico?page=0&size=20

Response 200 OK:
[lista de históricos de todas alocações da grade]

RBAC:
- ADMIN: vê histórico de qualquer grade
- COORDENADOR: vê apenas histórico de seu curso
```

---

## 6. ESTRUTURA DE ERROS

### Erro 400 - Bad Request
```json
{
  "erro": "Bad Request",
  "campos": [
    {
      "campo": "nome",
      "mensagem": "Campo obrigatório"
    },
    {
      "campo": "email",
      "mensagem": "Email deve ser válido"
    }
  ],
  "timestamp": "2026-06-01T11:00:00"
}
```

### Erro 403 - Forbidden
```json
{
  "erro": "Acesso negado",
  "detalhes": "Você não tem permissão para acessar este recurso",
  "timestamp": "2026-06-01T11:00:00"
}
```

### Erro 404 - Not Found
```json
{
  "erro": "Recurso não encontrado",
  "detalhes": "Grade horária com ID 999 não existe",
  "timestamp": "2026-06-01T11:00:00"
}
```

### Erro 409 - Conflict
```json
{
  "erro": "Conflito de negócio",
  "detalhes": "Professor não disponível neste horário",
  "timestamp": "2026-06-01T11:00:00"
}
```

### Erro 500 - Internal Server Error
```json
{
  "erro": "Erro interno do servidor",
  "detalhes": "Contate o administrador",
  "timestamp": "2026-06-01T11:00:00"
}
```

---

## 7. STATUS HTTP

| Código | Significado | Caso de Uso |
|--------|-------------|-----------|
| 200 | OK | GET com sucesso, PUT com sucesso |
| 201 | Created | POST com sucesso (novo recurso criado) |
| 204 | No Content | DELETE com sucesso |
| 400 | Bad Request | Validação de DTO falhou |
| 401 | Unauthorized | Token inválido/expirado |
| 403 | Forbidden | Sem permissão (RBAC) |
| 404 | Not Found | Recurso não existe |
| 409 | Conflict | Violação de regra de negócio |
| 422 | Unprocessable Entity | Validação de negócio falhou |
| 500 | Internal Server Error | Erro no servidor |

---

## 8. PAGINAÇÃO

Todos endpoints de listagem suportam paginação:

```
GET /alocacoes?page=0&size=20

Response:
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 456,
    "totalPages": 23
  },
  "links": {
    "first": "/alocacoes?page=0&size=20",
    "next": "/alocacoes?page=1&size=20",
    "last": "/alocacoes?page=22&size=20"
  }
}

Padrão:
- page: número da página (0-based)
- size: itens por página (10-100)
- sort: campo+direção (ex: "nome,asc" ou "data,desc")
```

---

## 9. EXEMPLO DE FLUXO COMPLETO

### Cenário: Coordenador cria nova grade

```
1. GET /auth/login
   ↓ Retorna token

2. POST /grades-horarias
   Body: { cursoId: 1, periodoId: 1 }
   ↓ Retorna grade vazia

3. POST /alocacoes (múltiplas vezes)
   Body: { gradeId: 1, turmaId: 1, ... }
   ↓ Cada alocação validada automaticamente

4. GET /alocacoes?gradeId=1
   ↓ Visualiza todas alocações

5. GET /alocacoes/validar
   ↓ Testa nova alocação antes de POST

6. GET /grades-horarias/1/exportar/pdf
   ↓ Exporta para imprimir/distribuir

7. GET /alocacoes/1/historico
   ↓ Consulta mudanças

✅ Grade completa e pronta!
```

---

**Próximas Versões**:
- v1.1: Notificações
- v1.2: Integração com SIS
- v2.0: App Mobile
- v2.1: Relatórios avançados

**Última Atualização**: 2026-06-01
