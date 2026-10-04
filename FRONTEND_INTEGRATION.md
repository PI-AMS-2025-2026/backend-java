# FRONTEND_INTEGRATION.md — Catálogo Completo de Contratos da API (Requests & Responses)

> **Destinatário:** Time de Engenharia de Frontend (Next.js / TypeScript)  
> **Finalidade:** Especificação contratual exaustiva (100% dos endpoints) para integração com o backend GINI.  
> **Padrão de URL:** `kebab-case` plural  
> **Padrão de Payload e Query Parameters:** `snake_case` estrito  
> **Padrão de Chave Estrangeira em Requests:** Objeto associativo `LongDTO` (`{"campo": {"id": <long>}}`)  
> **Versão:** 3.0.0  
> **Data:** 01/10/2026  

---

## 1. Padrões Globais da API

### 1.1 Configuração Base
* **Base URL Dev:** `http://localhost:8080`
* **CORS:** Origens liberadas: `http://localhost:3000` (Next.js) e `http://localhost:4200`
* **Autenticação:** Cabeçalho padrão `Authorization: Bearer <access_token>`

### 1.2 Estrutura de Resposta Paginada (`PageResponse<T>`)
Todas as rotas de listagem (`GET`) retornam o envelope de paginação padronizado:
```json
{
  "content": [ ... ],
  "page": 0,
  "size": 10,
  "total_elements": 45,
  "total_pages": 5
}
```

### 1.3 Estrutura de Resposta de Erro
* **Erro de Validação (`400 Bad Request`):**
```json
{
  "timestamp": "2026-10-01T18:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validação dos campos",
  "path": "/alocacoes",
  "errors": [
    {
      "field_name": "bloco_horario",
      "message": "Bloco de horário é obrigatório"
    }
  ]
}
```
* **Erro de Negócio / Regra Violada (`400 Bad Request` ou `409 Conflict`):**
```json
{
  "timestamp": "2026-10-01T18:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Conflito de horário: o professor já possui alocação neste mesmo bloco e dia.",
  "path": "/alocacoes"
}
```
* **Não Encontrado (`404 Not Found`):**
```json
{
  "timestamp": "2026-10-01T18:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Quadro horário ativo não encontrado para o curso informado",
  "path": "/motor-quadro/cursos/1"
}
```

---

## 2. Catálogo Exaustivo de Endpoints

---

### MÓDULO 1: Autenticação e Sessões (`/auth`)

#### 1.1 `POST /auth/login`
Autentica o usuário e emite os tokens JWT de acesso e de renovação.
* **Permissão:** Público
* **Request Body:**
```json
{
  "email": "admin@fatec.sp.gov.br",
  "senha": "AdminPassword123"
}
```
* **Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "4a7b9e12-8821-4f51-b841-3b7c9e012345"
}
```

#### 1.2 `POST /auth/refresh`
Renova o token de acesso utilizando um refresh token válido.
* **Permissão:** Público
* **Request Body:**
```json
{
  "refresh_token": "4a7b9e12-8821-4f51-b841-3b7c9e012345"
}
```
* **Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "8b9c0d23-9932-5a62-c952-4c8d0f123456"
}
```

#### 1.3 `POST /auth/logout`
Revoga imediatamente a sessão ativa do usuário.
* **Permissão:** Público
* **Request Body:**
```json
{
  "refresh_token": "8b9c0d23-9932-5a62-c952-4c8d0f123456"
}
```
* **Response (204 No Content):** *Sem corpo*

#### 1.4 `POST /auth/solicitar-recuperacao`
Solicita o link para redefinição de senha via e-mail (token com validade de 15 min).
* **Permissão:** Público
* **Request Body:**
```json
{
  "email": "coordenador@fatec.sp.gov.br"
}
```
* **Response (200 OK):**
```json
{
  "mensagem": "Se o e-mail informado estiver cadastrado, as instruções para recuperação foram enviadas."
}
```

#### 1.5 `POST /auth/redefinir-senha`
Define a nova senha utilizando o token de uso único recebido por e-mail.
* **Permissão:** Público
* **Request Body:**
```json
{
  "token": "e5f6a7b8-1234-4567-89ab-cdef01234567",
  "nova_senha": "NovaSenhaForte@2026"
}
```
* **Response (200 OK):**
```json
{
  "mensagem": "Senha redefinida com sucesso."
}
```

---

### MÓDULO 2: Usuários (`/usuarios`)

#### 2.1 `GET /usuarios`
* **Permissão:** ADMIN
* **Query Params:** `nome` (string), `email` (string), `tipo_usuario` (`ADMINISTRADOR`, `COORDENADOR`, `PROFESSOR`), `status` (`ATIVO`, `INATIVO`), `page` (int, default 0), `size` (int, default 10)
* **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "nome": "Administrador Geral",
      "email": "admin@fatec.sp.gov.br",
      "tipo_usuario": "ADMINISTRADOR",
      "status": "ATIVO",
      "curso": null,
      "created_at": "2026-08-18T10:00:00",
      "updated_at": "2026-08-18T10:00:00"
    },
    {
      "id": 2,
      "nome": "Coordenador ADS",
      "email": "coord.ads@fatec.sp.gov.br",
      "tipo_usuario": "COORDENADOR",
      "status": "ATIVO",
      "curso": {
        "id": 1,
        "nome": "AMS - Análise e Desenvolvimento de Sistemas",
        "periodicidade": "Semestral",
        "status": "ATIVO",
        "duracao": 6,
        "created_at": "2026-08-18T10:00:00",
        "updated_at": "2026-08-18T10:00:00"
      },
      "created_at": "2026-08-18T10:00:00",
      "updated_at": "2026-08-18T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 2,
  "total_pages": 1
}
```

#### 2.2 `GET /usuarios/{id}`
* **Response (200 OK):** Objeto `UsuarioResponse` individual.

#### 2.3 `POST /usuarios`
* **Permissão:** ADMIN
* **Request Body:**
```json
{
  "nome": "Novo Coordenador",
  "email": "novo.coord@fatec.sp.gov.br",
  "senha": "SenhaInicial123",
  "status": "ATIVO",
  "tipo_usuario": "COORDENADOR",
  "curso": { "id": 1 }
}
```
* **Response (201 Created):** Objeto `UsuarioResponse` criado.

#### 2.4 `PUT /usuarios/{id}`
* **Permissão:** ADMIN
* **Request Body:** Mesmo schema do `POST /usuarios` (`senha` opcional se não for alterada).
* **Response (200 OK):** Objeto `UsuarioResponse` atualizado.

#### 2.5 `DELETE /usuarios/{id}`
Inativação lógica do usuário (`status = INATIVO`).
* **Permissão:** ADMIN
* **Response (204 No Content):** *Sem corpo*

#### 2.6 `GET /usuarios/tipos`
Lista os tipos possíveis de usuários.
* **Response (200 OK):** `["ADMINISTRADOR", "COORDENADOR", "PROFESSOR"]`

---

### MÓDULO 3: Cursos (`/cursos`)

#### 3.1 `GET /cursos`
* **Permissão:** ADMIN, COORDENADOR
* **Query Params:** `nome` (string), `periodicidade` (`Semestral`, `Anual`), `status` (`ATIVO`, `INATIVO`), `duracao` (int), `page` (int), `size` (int)
* **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "nome": "AMS - Análise e Desenvolvimento de Sistemas",
      "periodicidade": "Semestral",
      "status": "ATIVO",
      "duracao": 6,
      "created_at": "2026-08-18T10:00:00",
      "updated_at": "2026-08-18T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 1,
  "total_pages": 1
}
```

#### 3.2 `GET /cursos/{id}`
* **Response (200 OK):** Objeto `CursoResponse`.

#### 3.3 `POST /cursos`
* **Permissão:** ADMIN
* **Request Body:**
```json
{
  "nome": "Gestão Empresarial",
  "periodicidade": "Semestral",
  "status": "ATIVO",
  "duracao": 6
}
```
* **Response (201 Created):** Objeto `CursoResponse`.

#### 3.4 `PUT /cursos/{id}`
* **Permissão:** ADMIN
* **Request Body:** Mesmo schema do `POST /cursos`.
* **Response (200 OK):** Objeto `CursoResponse` atualizado.

#### 3.5 `DELETE /cursos/{id}`
* **Permissão:** ADMIN
* **Response (204 No Content):** Inativação lógica do curso.

---

### MÓDULO 4: Turmas (`/turmas`)

#### 4.1 `GET /turmas`
* **Permissão:** ADMIN, COORDENADOR
* **Query Params:** `codigo` (string), `periodo` (int), `ano` (int), `curso_id` (long), `page` (int), `size` (int)
* **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 10,
      "codigo": "4º ANO",
      "periodo": 1,
      "ano": 2026,
      "numero_alunos": 35,
      "curso": {
        "id": 1,
        "nome": "AMS - Análise e Desenvolvimento de Sistemas",
        "periodicidade": "Semestral",
        "status": "ATIVO",
        "duracao": 6,
        "created_at": "2026-08-18T10:00:00",
        "updated_at": "2026-08-18T10:00:00"
      },
      "created_at": "2026-08-18T10:00:00",
      "updated_at": "2026-08-18T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 1,
  "total_pages": 1
}
```

#### 4.2 `POST /turmas`
* **Permissão:** ADMIN, COORDENADOR
* **Request Body (com `LongDTO`):**
```json
{
  "periodo": 1,
  "ano": 2026,
  "numero_alunos": 40,
  "curso": { "id": 1 }
}
```
* **Response (201 Created):** Objeto `TurmaResponse`.

#### 4.3 `PUT /turmas/{id}`
* **Request Body:** Mesmo schema do `POST /turmas`.
* **Response (200 OK):** Objeto `TurmaResponse`.

#### 4.4 `DELETE /turmas/{id}`
* **Response (204 No Content)**

---

### MÓDULO 5: Disciplinas (`/disciplinas`)

#### 5.1 `GET /disciplinas`
* **Permissão:** ADMIN, COORDENADOR
* **Query Params:** `nome` (string), `carga_horaria` (int), `tipo_disciplina` (string), `periodo` (int), `modalidade` (string), `cod_disciplina` (string), `curso_id` (long), `tipo_sala_id` (long), `page` (int), `size` (int)
* **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 31,
      "nome": "Projeto Integrador I",
      "carga_horaria": 80,
      "tipo_disciplina": "OBRIGATORIA",
      "periodo": 4,
      "modalidade": "PRESENCIAL",
      "cod_disciplina": "PI001",
      "cor": "#1A365D",
      "curso": {
        "id": 1,
        "nome": "AMS - Análise e Desenvolvimento de Sistemas",
        "periodicidade": "Semestral",
        "status": "ATIVO",
        "duracao": 6,
        "created_at": "2026-08-18T10:00:00",
        "updated_at": "2026-08-18T10:00:00"
      },
      "tipo_sala": {
        "id": 2,
        "nome": "Laboratório de Informática",
        "created_at": "2026-08-18T10:00:00",
        "updated_at": "2026-08-18T10:00:00"
      },
      "created_at": "2026-08-18T10:00:00",
      "updated_at": "2026-08-18T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 1,
  "total_pages": 1
}
```

#### 5.2 `POST /disciplinas`
* **Permissão:** ADMIN
* **Request Body (com `LongDTO`):**
```json
{
  "nome": "Projeto Integrador I",
  "carga_horaria": 80,
  "tipo_disciplina": "OBRIGATORIA",
  "periodo": 4,
  "modalidade": "PRESENCIAL",
  "cod_disciplina": "PI001",
  "cor": "#1A365D",
  "curso": { "id": 1 },
  "tipo_sala": { "id": 2 }
}
```
* **Response (201 Created):** Objeto `DisciplinaResponse`.

#### 5.3 `PUT /disciplinas/{id}`
* **Request Body:** Mesmo schema do `POST /disciplinas`.
* **Response (200 OK):** Objeto `DisciplinaResponse`.

#### 5.4 `DELETE /disciplinas/{id}`
* **Response (204 No Content)**

---

### MÓDULO 6: Professores (`/professores`)

#### 6.1 `GET /professores`
* **Permissão:** ADMIN, COORDENADOR
* **Query Params:** `nome` (string), `email` (string), `cidade` (string), `status` (`ATIVO`, `INATIVO`), `page` (int), `size` (int)
* **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 5,
      "nome": "Prof. Dr. Carlos Silva",
      "email": "carlos.silva@fatec.sp.gov.br",
      "cidade": "Itu",
      "status": "ATIVO",
      "created_at": "2026-08-18T10:00:00",
      "updated_at": "2026-08-18T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 1,
  "total_pages": 1
}
```

#### 6.2 `POST /professores`
* **Permissão:** ADMIN
* **Request Body:**
```json
{
  "nome": "Prof. Dr. Carlos Silva",
  "email": "carlos.silva@fatec.sp.gov.br",
  "cidade": "Itu",
  "status": "ATIVO"
}
```
* **Response (201 Created):** Objeto `ProfessorResponse`.

#### 6.3 `PUT /professores/{id}`
* **Request Body:** Mesmo schema do `POST /professores`.
* **Response (200 OK):** Objeto `ProfessorResponse`.

#### 6.4 `DELETE /professores/{id}`
* **Response (204 No Content)**

---

### MÓDULO 7: Vínculo Professor-Disciplina (`/professores-disciplinas`)

#### 7.1 `GET /professores-disciplinas`
* **Permissão:** ADMIN, COORDENADOR
* **Query Params:** `professor_id` (long), `disciplina_id` (long), `page` (int), `size` (int)
* **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "professor_id": 5,
      "disciplina_id": 31
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 1,
  "total_pages": 1
}
```

#### 7.2 `POST /professores-disciplinas`
* **Permissão:** ADMIN
* **Request Body (com `LongDTO`):**
```json
{
  "professor": { "id": 5 },
  "disciplina": { "id": 31 }
}
```
* **Response (201 Created):** Objeto `ProfessorDisciplinaResponse`.

#### 7.3 `DELETE /professores-disciplinas/{id}`
* **Response (204 No Content)**

---

### MÓDULO 8: Salas (`/salas`)

#### 8.1 `GET /salas`
* **Query Params:** `codigo` (string), `capacidade` (int), `tipo_sala_id` (long), `page` (int), `size` (int)
* **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 41,
      "codigo": "LAB INF 02",
      "capacidade": 40,
      "tipo_sala": {
        "id": 2,
        "nome": "Laboratório de Informática",
        "created_at": "2026-08-18T10:00:00",
        "updated_at": "2026-08-18T10:00:00"
      },
      "created_at": "2026-08-18T10:00:00",
      "updated_at": "2026-08-18T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 1,
  "total_pages": 1
}
```

#### 8.2 `POST /salas`
* **Permissão:** ADMIN
* **Request Body (com `LongDTO`):**
```json
{
  "codigo": "LAB INF 02",
  "capacidade": 40,
  "tipo_sala": { "id": 2 }
}
```
* **Response (201 Created):** Objeto `SalaResponse`.

#### 8.3 `PUT /salas/{id}`
* **Request Body:** Mesmo schema do `POST /salas`.
* **Response (200 OK):** Objeto `SalaResponse`.

#### 8.4 `DELETE /salas/{id}`
* **Response (204 No Content)**

---

### MÓDULO 9: Tipos de Sala (`/tipos-salas`)

#### 9.1 `GET /tipos-salas`
* **Response (200 OK):** Lista paginada `PageResponse<TipoSalaResponse>`.
```json
{
  "content": [
    {
      "id": 2,
      "nome": "Laboratório de Informática",
      "created_at": "2026-08-18T10:00:00",
      "updated_at": "2026-08-18T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 1,
  "total_pages": 1
}
```

#### 9.2 `POST /tipos-salas`
* **Request Body:**
```json
{
  "nome": "Auditório"
}
```
* **Response (201 Created):** Objeto `TipoSalaResponse`.

---

### MÓDULO 10: Recursos e Tipos de Recursos (`/recursos`, `/tipos-recursos`)

#### 10.1 `POST /tipos-recursos`
* **Request Body:** `{"nome": "Audiovisual"}`
* **Response (201 Created):** `{"id": 1, "nome": "Audiovisual", "created_at": "...", "updated_at": "..."}`

#### 10.2 `POST /recursos`
* **Request Body (com `LongDTO`):**
```json
{
  "nome": "Projetor HDMI",
  "tipo_recurso": { "id": 1 }
}
```
* **Response (201 Created):** `{"id": 10, "nome": "Projetor HDMI", "tipo_recurso": {...}}`

---

### MÓDULO 11: Recursos em Salas (`/recursos-salas`)

#### 11.1 `GET /recursos-salas`
* **Query Params:** `sala_id` (long), `recurso_id` (long), `page` (int), `size` (int)
* **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "quantidade": 1,
      "sala": { "id": 41, "codigo": "LAB INF 02" },
      "recurso": { "id": 10, "nome": "Projetor HDMI" }
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 1,
  "total_pages": 1
}
```

#### 11.2 `POST /recursos-salas`
* **Request Body (com `LongDTO`):**
```json
{
  "sala": { "id": 41 },
  "recurso": { "id": 10 },
  "quantidade": 1
}
```
* **Response (201 Created):** Objeto `RecursoSalaResponse`.

---

### MÓDULO 12: Períodos de Atividade do Quadro (`/periodos-atividade-quadro`)

#### 12.1 `GET /periodos-atividade-quadro`
* **Query Params:** `ano` (int), `periodo` (int), `status` (`ATIVO`, `INATIVO`), `data_inicio` (LocalDate `YYYY-MM-DD`), `data_fim` (LocalDate `YYYY-MM-DD`), `page` (int), `size` (int)
* **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "ano": 2026,
      "periodo": 1,
      "data_inicio": "2026-02-02",
      "data_fim": "2026-06-30",
      "status": "ATIVO",
      "created_at": "2026-08-18T10:00:00",
      "updated_at": "2026-08-18T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 1,
  "total_pages": 1
}
```

#### 12.2 `POST /periodos-atividade-quadro`
* **Request Body:**
```json
{
  "ano": 2026,
  "periodo": 2,
  "data_inicio": "2026-08-01",
  "data_fim": "2026-12-20",
  "status": "ATIVO"
}
```
* **Response (201 Created):** Objeto `PeriodoAtividadeQuadroResponse`.

#### 12.3 `GET /periodos-atividade-quadro/tipos`
* **Response (200 OK):** `["1", "2"]` (Semestres) ou tipos de período disponíveis.

---

### MÓDULO 13: Blocos de Horário (`/bloco-horarios`)

#### 13.1 `GET /bloco-horarios`
* **Query Params:** `hora_inicio` (`HH:mm:ss`), `hora_fim` (`HH:mm:ss`), `page` (int), `size` (int)
* **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "hora_inicio": "13:20:00",
      "hora_fim": "14:10:00"
    },
    {
      "id": 2,
      "hora_inicio": "14:10:00",
      "hora_fim": "15:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 2,
  "total_pages": 1
}
```

#### 13.2 `POST /bloco-horarios`
* **Request Body:**
```json
{
  "hora_inicio": "13:20:00",
  "hora_fim": "14:10:00"
}
```
* **Response (201 Created):** Objeto `BlocoHorarioResponse`.

#### 13.3 `POST /bloco-horarios/lote`
Cria múltiplos blocos horários atomicamente.
* **Request Body:**
```json
[
  { "hora_inicio": "13:20:00", "hora_fim": "14:10:00" },
  { "hora_inicio": "14:10:00", "hora_fim": "15:00:00" },
  { "hora_inicio": "15:10:00", "hora_fim": "16:00:00" }
]
```
* **Response (201 Created):** Lista `List<BlocoHorarioResponse>`.

---

### MÓDULO 14: Dias da Semana (`/dias-semana`)

#### 14.1 `GET /dias-semana`
* **Response (200 OK):**
```json
[
  "SEGUNDA",
  "TERCA",
  "QUARTA",
  "QUINTA",
  "SEXTA",
  "SABADO",
  "DOMINGO"
]
```

---

### MÓDULO 15: Disponibilidade de Professores (`/disponibilidades-professores`)

#### 15.1 `GET /disponibilidades-professores`
* **Query Params:** `professor_id` (long), `dia_semana` (string), `bloco_horario_id` (long), `page` (int), `size` (int)
* **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "professor": { "id": 5, "nome": "Prof. Dr. Carlos Silva" },
      "dia_semana": "SEGUNDA",
      "bloco_horario": { "id": 1, "hora_inicio": "13:20:00", "hora_fim": "14:10:00" }
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 1,
  "total_pages": 1
}
```

#### 15.2 `POST /disponibilidades-professores`
* **Request Body (com `LongDTO`):**
```json
{
  "professor": { "id": 5 },
  "dia_semana": "SEGUNDA",
  "bloco_horario": { "id": 1 }
}
```
* **Response (201 Created):** Objeto `DisponibilidadeProfessorResponse`.

---

### MÓDULO 16: Quadros Horários (`/quadros-horarios`)

#### 16.1 `GET /quadros-horarios`
* **Query Params:** `curso_id` (long), `periodo_atividade_quadro_id` (long), `status` (`ATIVO`, `INATIVO`), `versao` (int), `page` (int), `size` (int)
* **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 10,
      "versao": 1,
      "status": "ATIVO",
      "curso": { "id": 1, "nome": "AMS - Análise e Desenvolvimento de Sistemas" },
      "periodo_atividade_quadro": { "id": 1, "ano": 2026, "periodo": 1 },
      "created_at": "2026-08-18T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 1,
  "total_pages": 1
}
```

#### 16.2 `POST /quadros-horarios`
* **Request Body (com `LongDTO`):**
```json
{
  "versao": 1,
  "status": "ATIVO",
  "curso": { "id": 1 },
  "periodo_atividade_quadro": { "id": 1 }
}
```
* **Response (201 Created):** Objeto `QuadroHorarioResponse`.

#### 16.3 `POST /quadros-horarios/{id}/copiar`
Copia todas as alocações ativas do quadro para um novo período.
* **Query Params:** `novo_periodo_id` (long)
* **Response (201 Created):** Objeto `QuadroHorarioResponse` do novo quadro gerado.

---

### MÓDULO 17: Alocações (`/alocacoes`)

#### 17.1 `GET /alocacoes`
* **Query Params:** `turma_id` (long), `disciplina_id` (long), `sala_id` (long), `professor_id` (long), `dia_semana` (string), `bloco_horario_id` (long), `quadro_horario_id` (long), `page` (int), `size` (int)
* **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 51,
      "turma": { "id": 10, "codigo": "4º ANO", "ano": 2026, "periodo": 1 },
      "disciplina": { "id": 31, "nome": "Projeto Integrador I", "cod_disciplina": "PI001" },
      "sala": { "id": 41, "codigo": "LAB INF 02" },
      "professor": { "id": 5, "nome": "Prof. Dr. Carlos Silva" },
      "dia_semana": "SEGUNDA",
      "bloco_horario": { "id": 1, "hora_inicio": "13:20:00", "hora_fim": "14:10:00" },
      "quadro_horario": { "id": 10, "versao": 1, "status": "ATIVO" },
      "created_at": "2026-08-18T10:00:00",
      "updated_at": "2026-08-18T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 1,
  "total_pages": 1
}
```

#### 17.2 `POST /alocacoes`
Cria uma alocação executando validações completas (choque de sala, choque de turma, choque de docente, descanso interjornada de 12 horas).
* **Request Body (com `LongDTO`):**
```json
{
  "turma": { "id": 10 },
  "disciplina": { "id": 31 },
  "sala": { "id": 41 },
  "professor": { "id": 5 },
  "dia_semana": "SEGUNDA",
  "bloco_horario": { "id": 1 },
  "quadro_horario": { "id": 10 },
  "justificativa_alteracao": "Alocação inicial do semestre",
  "usuario_alteracao": { "id": 1 }
}
```
* **Response (201 Created):** Objeto `AlocacaoResponse`.

#### 17.3 `PUT /alocacoes/{id}`
Atualiza a alocação e registra automaticamente o log em `HistoricoVersaoAlocacao`.
* **Request Body:** Mesmo schema do `POST /alocacoes`.
* **Response (200 OK):** Objeto `AlocacaoResponse`.

#### 17.4 `DELETE /alocacoes/{id}`
Remove a alocação.
* **Response (204 No Content)**

#### 17.5 `POST /alocacoes/validar-carga-horaria`
Valida previamente se a alocação do professor desrespeita a regra de 12h ou a carga horária máxima semanal antes de salvar.
* **Request Body:**
```json
{
  "professor": { "id": 5 },
  "dia_semana": "SEGUNDA",
  "horario": { "id": 1 }
}
```
* **Response (200 OK):**
```json
{
  "valido": true,
  "mensagem": "Carga horária e interjornada válidas."
}
```

---

### MÓDULO 18: Históricos de Versão da Alocação (`/historicos-versoes-alocacoes`)

#### 18.1 `GET /historicos-versoes-alocacoes`
* **Permissão:** ADMIN, COORDENADOR
* **Query Params:** `alocacao_id` (long), `usuario_id` (long), `page` (int), `size` (int)
* **Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "data_alteracao": "2026-09-10",
      "justificativa": "Troca de laboratório por manutenção de computadores",
      "campo_alterado": "sala",
      "valor_antigo": "LAB INF 01",
      "valor_novo": "LAB INF 02",
      "alocacao": { "id": 51 },
      "usuario": { "id": 1, "nome": "Administrador Geral" }
    }
  ],
  "page": 0,
  "size": 10,
  "total_elements": 1,
  "total_pages": 1
}
```

---

### MÓDULO 19: Motor de Quadro Horário (`/motor-quadro`)

#### 19.1 `GET /motor-quadro/cursos/{curso_id}`
Monta a matriz completa do curso para o quadro horário com status `ATIVO`.
* **Permissão:** ADMIN, COORDENADOR, PÚBLICO
* **Response (200 OK):**
```json
{
  "curso": {
    "id": 1,
    "nome": "AMS - Análise e Desenvolvimento de Sistemas"
  },
  "quadro_horario": {
    "id": 10,
    "versao": 1,
    "status": "ATIVO"
  },
  "blocos": [
    {
      "id": 1,
      "hora_inicio": "13:20:00",
      "hora_fim": "14:10:00",
      "rotulo": "13h20-14h10"
    },
    {
      "id": 2,
      "hora_inicio": "14:10:00",
      "hora_fim": "15:00:00",
      "rotulo": "14h10-15h00"
    }
  ],
  "turmas": [
    {
      "id": 21,
      "codigo": "4º ANO",
      "ano": 4,
      "periodo": 1,
      "celulas": [
        {
          "dia_semana": "SEGUNDA",
          "bloco_horario_id": 1,
          "disciplina": {
            "id": 31,
            "nome": "Projeto Integrador I"
          },
          "sala": {
            "id": 41,
            "codigo": "LAB INF 02"
          },
          "alocacao_id": 51
        },
        {
          "dia_semana": "SEGUNDA",
          "bloco_horario_id": 2,
          "disciplina": null,
          "sala": null,
          "alocacao_id": null
        }
      ]
    }
  ]
}
```

#### 19.2 `GET /motor-quadro/cursos/{curso_id}/turmas/{turma_id}`
Retorna exclusivamente as alocações daquela turma específica no quadro ativo. Rejeita turmas de outro curso com `404 Not Found`.
* **Permissão:** ADMIN, COORDENADOR, PÚBLICO
* **Response (200 OK):**
```json
{
  "curso": {
    "id": 1,
    "nome": "AMS - Análise e Desenvolvimento de Sistemas"
  },
  "turma": {
    "id": 21,
    "codigo": "4º ANO",
    "ano": 4,
    "periodo": 1
  },
  "quadro_horario": {
    "id": 10,
    "versao": 1,
    "status": "ATIVO"
  },
  "horarios": [
    {
      "dia_semana": "SEGUNDA",
      "bloco_horario": {
        "id": 1,
        "hora_inicio": "13:20:00",
        "hora_fim": "14:10:00"
      },
      "disciplina": {
        "id": 31,
        "nome": "Projeto Integrador I"
      },
      "sala": {
        "id": 41,
        "codigo": "LAB INF 02"
      },
      "alocacao_id": 51
    }
  ]
}
```

#### 19.3 `GET /motor-quadro/cursos/{curso_id}/exportar/pdf`
Gera e faz o download direto do PDF do quadro completo formatado para impressão paisagem (`A4 landscape`).
* **Headers de Resposta:**
  * `Content-Type: application/pdf`
  * `Content-Disposition: attachment; filename="quadro-horario-curso-1.pdf"`
* **Response (200 OK):** *Arquivo Binário PDF*

#### 19.4 `GET /motor-quadro/cursos/{curso_id}/exportar/planilha`
Gera e faz o download da pasta de trabalho Excel contendo a grade formatada por turma e bloco.
* **Headers de Resposta:**
  * `Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
  * `Content-Disposition: attachment; filename="quadro-horario-curso-1.xlsx"`
* **Response (200 OK):** *Arquivo Binário XLSX*

---

## 3. Guia de Implementação no Frontend (Next.js)

1. **Atualizar `src/types/api.ts`:**
   * Substituir qualquer chave `camelCase` por `snake_case` nas interfaces de Request e Response.
   * Adicionar a interface canônica `LongDTO`:
     ```ts
     export interface LongDTO {
       id: number;
     }
     ```
2. **Atualizar URLs em `src/services/`:**
   * Apontar para os novos endpoints `kebab-case` plural detalhados acima.
3. **Download de Arquivos:**
   * Utilizar `responseType: 'blob'` no cliente HTTP para os endpoints `/exportar/pdf` e `/exportar/planilha`, criando um link temporário com `window.URL.createObjectURL(blob)` para download no navegador.
