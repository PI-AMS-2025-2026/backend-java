# Módulo: Controllers (Camada de Apresentação)

## 1. Objetivo do Módulo

O módulo de **Controllers** é a **camada de apresentação** do sistema, responsável por expor os **endpoints REST** e gerenciar a comunicação HTTP entre cliente e backend.

### Responsabilidades Principais
- 🌐 **Exposição de endpoints REST**: CRUD e operações customizadas
- 📥 **Recepção de requisições HTTP**: Parse e validação de entrada
- 📤 **Envio de respostas**: Serialização de dados e status codes
- ✓ **Validação de entrada**: @Valid em DTOs
- 🔀 **Roteamento**: Delegação para services

---

## 2. Estrutura de Controllers

### 2.1 Convenção de Organização

```
src/main/java/com/fatec/gini/web/controller/

├─ AlocacaoController.java          (23 controllers no total)
├─ GradeHorariaController.java
├─ DisciplinaController.java
├─ TurmaController.java
├─ UsuarioController.java
├─ SalaController.java
├─ CursoController.java
├─ HorarioController.java
├─ DiaSemanaController.java
├─ PeriodoLetivoController.java
├─ TipoUsuarioController.java
├─ TipoSalaController.java
├─ RecursoController.java
├─ RecursoSalaController.java
├─ ProfessorDisciplinaController.java
├─ DisponibilidadeProfessorController.java
├─ HistoricoAlteracaoController.java
└─ (6 controllers adicionais)
```

### 2.2 Padrão de Controller REST

```java
@RestController
@RequestMapping("/alocacoes")  // Rota base (recurso em plural)
@CrossOrigin                    // Permite requisições de outros domínios
public class AlocacaoController {
    
    @Autowired
    private AlocacaoService service;  // Injeção de serviço
    
    // 1. CREATE
    @PostMapping
    public ResponseEntity<AlocacaoResponse> criar(@RequestBody @Valid AlocacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }
    
    // 2. READ (Lista com filtros + paginação)
    @GetMapping
    public ResponseEntity<Page<AlocacaoResponse>> listar(
            @RequestParam(required = false) Long turma,
            @RequestParam(required = false) Long disciplina,
            // ... outros filtros ...
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.listar(turma, disciplina, ..., page, size));
    }
    
    // 3. READ (Busca por ID)
    @GetMapping("/{id}")
    public ResponseEntity<AlocacaoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
    
    // 4. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<AlocacaoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid AlocacaoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }
    
    // 5. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();  // 204
    }
}
```

---

## 3. Mapeamento de Requisições HTTP

### 3.1 Métodos HTTP e Convenções

| Método | URI | Ação | Status Sucesso |
|--------|-----|------|---|
| **POST** | `/alocacoes` | Criar | 201 Created |
| **GET** | `/alocacoes` | Listar com filtros | 200 OK |
| **GET** | `/alocacoes/{id}` | Buscar por ID | 200 OK |
| **PUT** | `/alocacoes/{id}` | Atualizar | 200 OK |
| **DELETE** | `/alocacoes/{id}` | Deletar | 204 No Content |

### 3.2 Exemplo de Requisição e Resposta

#### POST /alocacoes (Criar)

```bash
# Requisição
POST http://localhost:8080/alocacoes
Content-Type: application/json

{
  "turma": {"id": 5},
  "disciplina": {"id": 12},
  "sala": {"id": 3},
  "usuario": {"id": 8},
  "diaSemana": {"id": 1},
  "horario": {"id": 2},
  "gradeHoraria": {"id": 10},
  "usuarioAlteracao": {"id": 1}
}

# Resposta (201 Created)
{
  "id": 42,
  "turma": {
    "id": 5,
    "nome": "ADS-2A",
    "capacidade": 40,
    "curso": {...}
  },
  "disciplina": {...},
  "sala": {...},
  "usuario": {...},
  "diaSemana": {...},
  "horario": {...},
  "gradeHoraria": {...}
}
```

#### GET /alocacoes (Listar com filtros)

```bash
# Requisição com filtros opcionais
GET http://localhost:8080/alocacoes?turma=5&sala=3&page=0&size=10
Content-Type: application/json

# Resposta (200 OK) - Página paginada
{
  "content": [
    {
      "id": 42,
      "turma": {...},
      ...
    },
    ...
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 25,
    "totalPages": 3
  },
  "last": false,
  "numberOfElements": 10
}
```

#### GET /alocacoes/{id} (Buscar por ID)

```bash
# Requisição
GET http://localhost:8080/alocacoes/42
Content-Type: application/json

# Resposta (200 OK)
{
  "id": 42,
  "turma": {...},
  "disciplina": {...},
  ...
}
```

#### PUT /alocacoes/{id} (Atualizar)

```bash
# Requisição
PUT http://localhost:8080/alocacoes/42
Content-Type: application/json

{
  "turma": {"id": 5},
  "disciplina": {"id": 12},
  "sala": {"id": 4},  // MUDOU: de 3 para 4
  "usuario": {"id": 8},
  "diaSemana": {"id": 1},
  "horario": {"id": 2},
  "gradeHoraria": {"id": 10},
  "usuarioAlteracao": {"id": 1},
  "justificativaAlteracao": "Sala 3 em reformas"  // ADICIONADO
}

# Resposta (200 OK)
{
  "id": 42,
  "turma": {...},
  "sala": {  // ATUALIZADA
    "id": 4,
    "codigo": "SALA-13"
  },
  ...
}
```

#### DELETE /alocacoes/{id} (Deletar)

```bash
# Requisição
DELETE http://localhost:8080/alocacoes/42
Content-Type: application/json

# Resposta (204 No Content)
(sem corpo)
```

---

## 4. Tratamento de Erros HTTP

### 4.1 Exceções Mapeadas

```
BusinessException → 409 Conflict
  └─ "Professor João já tem aula no mesmo horário"

EntityNotFoundException → 404 Not Found
  └─ "Alocação não encontrada com ID: 999"

MethodArgumentNotValidException → 422 Unprocessable Entity
  └─ "Turma é obrigatória"

DataIntegrityViolationException → 400 Bad Request
  └─ "Erro de integridade no banco de dados"

ParameterException → 400 Bad Request
  └─ "Parâmetro inválido"

DatabaseException → 400 Bad Request
  └─ "Erro de banco de dados"

IllegalArgumentException → 400 Bad Request
  └─ "Parâmetro inválido"

HttpMessageNotReadableException → 400 Bad Request
  └─ "Corpo da requisição inválido"
```

### 4.2 Exemplo de Resposta de Erro

```json
// 409 Conflict (Business Exception)
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 409,
  "error": "Erro de regra de negócio",
  "message": "Professor João já tem aula no mesmo horário",
  "path": "/alocacoes"
}

// 404 Not Found
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 404,
  "error": "Recurso não encontrado",
  "message": "Alocação não encontrada com ID: 999",
  "path": "/alocacoes/999"
}

// 422 Unprocessable Entity (Validação DTO)
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 422,
  "error": "Erro de validação",
  "message": "Erro de validação nos campos enviados",
  "errors": [
    "Turma é obrigatória",
    "Sala é obrigatória"
  ],
  "path": "/alocacoes"
}
```

---

## 5. Controllers Principais

### 5.1 AlocacaoController (CRÍTICO)

```java
@RestController
@RequestMapping("/alocacoes")
@CrossOrigin
public class AlocacaoController {
    
    @Autowired
    private AlocacaoService service;
    
    // POST /alocacoes - Criar alocação
    @PostMapping
    public ResponseEntity<AlocacaoResponse> criar(@RequestBody @Valid AlocacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }
    
    // GET /alocacoes?turma=5&disciplina=12&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<AlocacaoResponse>> listar(
            @RequestParam(required = false) Long turma,
            @RequestParam(required = false) Long disciplina,
            @RequestParam(required = false) Long sala,
            @RequestParam(required = false) Long usuario,
            @RequestParam(required = false, name = "dia_semana") Long diaSemana,
            @RequestParam(required = false) Long horario,
            @RequestParam(required = false) Long grade,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        return ResponseEntity.ok(
            service.listar(turma, disciplina, sala, usuario, diaSemana, horario, grade, page, size)
        );
    }
    
    // GET /alocacoes/42
    @GetMapping("/{id}")
    public ResponseEntity<AlocacaoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
    
    // PUT /alocacoes/42
    @PutMapping("/{id}")
    public ResponseEntity<AlocacaoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid AlocacaoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }
    
    // DELETE /alocacoes/42
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 5.2 GradeHorariaController

```java
@RestController
@RequestMapping("/grades-horarias")
@CrossOrigin
public class GradeHorariaController {
    
    @Autowired
    private GradeHorariaService service;
    
    // CRUD padrão
    @PostMapping
    public ResponseEntity<GradeHorariaResponse> criar(@RequestBody @Valid GradeHorariaRequest request)
    
    @GetMapping
    public ResponseEntity<Page<GradeHorariaResponse>> listar(
            @RequestParam(required = false) Long curso,
            @RequestParam(required = false) Long periodoLetivo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    
    @GetMapping("/{id}")
    public ResponseEntity<GradeHorariaResponse> buscarPorId(@PathVariable Long id)
    
    @PutMapping("/{id}")
    public ResponseEntity<GradeHorariaResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid GradeHorariaRequest request)
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id)
}
```

### 5.3 HistoricoAlteracaoController (Apenas Leitura)

```java
@RestController
@RequestMapping("/historico-alteracoes")
@CrossOrigin
public class HistoricoAlteracaoController {
    
    @Autowired
    private HistoricoAlteracaoService service;
    
    // Apenas consultas (sem create/update/delete)
    @GetMapping
    public ResponseEntity<Page<HistoricoAlteracaoResponse>> listar(
            @RequestParam(required = false) Long alocacao,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long usuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    
    @GetMapping("/{id}")
    public ResponseEntity<HistoricoAlteracaoResponse> buscarPorId(@PathVariable Long id)
}
```

---

## 6. Anotações Utilizadas

### 6.1 Mapeamento de Requisições

```java
@RestController              // Combina @Controller + @ResponseBody
@RequestMapping("/alocacoes") // Rota base para classe
@CrossOrigin                 // Permite CORS

@PostMapping                 // Mapa POST
@GetMapping                  // Mapa GET
@PutMapping                  // Mapa PUT
@DeleteMapping               // Mapa DELETE

@RequestBody @Valid          // Deserializa JSON + valida DTO
@PathVariable Long id        // Extrai ID da URL (/alocacoes/42)
@RequestParam Long turma     // Extrai parâmetro de query (?turma=5)
```

### 6.2 Resposta HTTP

```java
ResponseEntity.status(HttpStatus.CREATED).body(response)  // 201 + body
ResponseEntity.ok(response)                               // 200 + body
ResponseEntity.noContent().build()                        // 204 sem body
ResponseEntity.notFound().build()                         // 404
ResponseEntity.status(409).body(error)                    // 409 + error
```

---

## 7. Fluxo Completo de Requisição

```
1. Cliente HTTP envia POST /alocacoes com JSON

2. DispatcherServlet (Spring Web) intercepta requisição
   └─ Encontra AlocacaoController.criar()

3. Spring deserializa JSON → AlocacaoRequest
   └─ @RequestBody dispara deserialização

4. Spring valida DTO → @Valid
   └─ Verifica @NotNull, @Size, @Pattern, etc
   └─ Se falhar → MethodArgumentNotValidException
   └─ Handler captura → resposta 422

5. Se validação OK → Método criar() executado
   └─ AlocacaoController.criar(request)
   └─ Chama AlocacaoService.criar(request)
   └─ Service chama UseCase
   └─ UseCase executa 13+ validações
   └─ Se OK: persiste em banco
   └─ Se falha: lança BusinessException
   └─ Handler captura → resposta 409

6. Se tudo OK → Mapper converte Alocacao → AlocacaoResponse

7. Spring serializa Response → JSON
   └─ @RestController automático (@ResponseBody)

8. DispatcherServlet retorna:
   ├─ Status: 201 Created
   ├─ Headers: Content-Type: application/json
   └─ Body: JSON da resposta

9. Cliente HTTP recebe resposta
```

---

## 8. Validação de Entrada

### 8.1 Validação em Camadas

```
1. ANOTAÇÕES DTO (@NotNull, @Size, etc)
   └─ ResourceExceptionHandler → 422

2. LÓGICA SERVICE
   └─ Verifica se IDs existem no banco
   └─ ResourceExceptionHandler → 404/400

3. LÓGICA USECASE
   └─ Validações complexas (13+ regras de negócio)
   └─ ResourceExceptionHandler → 409

4. BANCO DE DADOS
   └─ Constraints SQL (NOT NULL, UNIQUE, FK)
   └─ DataIntegrityViolationException → 400
```

### 8.2 Exemplo com Filtros Opcionais

```java
@GetMapping
public ResponseEntity<Page<AlocacaoResponse>> listar(
        @RequestParam(required = false) Long turma,  // Opcional
        @RequestParam(required = false) Long sala,   // Opcional
        @RequestParam(defaultValue = "0") int page,  // Padrão 0
        @RequestParam(defaultValue = "10") int size) { // Padrão 10
    
    // Se turma = null → buscar sem filtro de turma
    // Se sala = null → buscar sem filtro de sala
    // Filtros são AND (turma AND sala)
    
    return ResponseEntity.ok(
        service.listar(turma, sala, page, size)
    );
}

// Exemplos de URLs válidas:
// GET /alocacoes                              (sem filtros, página 0, 10 itens)
// GET /alocacoes?turma=5                      (apenas turma 5)
// GET /alocacoes?turma=5&sala=3               (turma 5 E sala 3)
// GET /alocacoes?page=2&size=20               (página 2, 20 itens por página)
// GET /alocacoes?turma=5&page=1&size=50       (turma 5, página 1, 50 itens)
```

---

## 9. CORS (Cross-Origin Resource Sharing)

### 9.1 @CrossOrigin

```java
@RestController
@CrossOrigin  // Permite requisições de qualquer origin
public class AlocacaoController {
    // Todos os endpoints permitem CORS
}

// Ou customizado:
@CrossOrigin(origins = "http://localhost:3000")
public class AlocacaoController {
    // Apenas http://localhost:3000 pode acessar
}

// Ou por método:
@PostMapping
@CrossOrigin(origins = "http://localhost:3000")
public ResponseEntity<AlocacaoResponse> criar(...) {
    // Apenas este método permite CORS customizado
}
```

---

## 10. Documentação com Swagger

### 10.1 Acesso Automático

Todos os controllers são **automaticamente documentados** via SpringDoc OpenAPI:

```
http://localhost:8080/swagger-ui.html
```

### 10.2 Swagger mostra:
- Todos os endpoints
- Parâmetros (path, query, body)
- Validações (required, min, max)
- Tipos de resposta (200, 201, 404, 409)
- Schema de DTOs
- **Try it out**: testar endpoint direto do navegador

---

## 11. Arquivos Críticos

```
src/main/java/com/fatec/gini/web/controller/

├─ AlocacaoController.java (CRÍTICO)
│  └─ 5 endpoints principais
│
├─ GradeHorariaController.java
│  └─ CRUD + operações customizadas
│
├─ HistoricoAlteracaoController.java
│  └─ Apenas consultas (sem write)
│
└─ (20 controllers adicionais)
```

---

## 12. Boas Práticas

✅ **Uma responsabilidade por controller**: AlocacaoController para Alocacao
✅ **Nomes semanticamente corretos**: `/alocacoes` (plural, recurso REST)
✅ **@RequestParam para filtros**: Opcionais, com defaults
✅ **@PathVariable para IDs**: Parte da URL (sem query string)
✅ **@Valid em RequestBody**: Sempre validar DTOs
✅ **ResponseEntity para controle**: Status HTTP explícito
✅ **@CrossOrigin para CORS**: Necessário para frontends remotos
✅ **Sem lógica de negócio**: Delegado para Services
✅ **Paginação**: page + size como padrão

---

## 13. Débitos Técnicos

❌ **MÉDIO - Segurança aberta**: ConfiguracaoSeguranca permite tudo (TODO)
   - Implementar autenticação (JWT ou OAuth2)
   - Implementar autorização por perfil (@PreAuthorize)

❌ **MÉDIO - Sem rate limiting**: Sem proteção contra abuso
   - Considerar Spring Cloud Consul
   - Ou implementar filter de rate limit

❌ **BAIXO - Sem versionamento de API**: Sem /v1/ na URL
   - Considerar estratégia para futuro (v1, v2, v3)

---

## 14. Como Adicionar Novo Controller

```java
// 1. Criar novo Controller
@RestController
@RequestMapping("/meu-recurso")
@CrossOrigin
public class MeuRecursoController {
    
    @Autowired
    private MeuRecursoService service;
    
    @PostMapping
    public ResponseEntity<MeuRecursoResponse> criar(@RequestBody @Valid MeuRecursoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }
    
    @GetMapping
    public ResponseEntity<Page<MeuRecursoResponse>> listar(
            @RequestParam(required = false) Long filtro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.listar(filtro, page, size));
    }
    
    // ... outros endpoints
}

// 2. Injetar no @RestController (Spring Boot auto-detecta)
// 3. Endpoints estão prontos em /meu-recurso
// 4. Swagger auto-documenta em /swagger-ui.html
```

---

## 15. Checklist de Development

- [ ] Controller criado com @RestController
- [ ] @RequestMapping com rota semântica
- [ ] Service injetado com @Autowired
- [ ] @Valid em todos os @RequestBody
- [ ] DTOs Request/Response corretos
- [ ] ResponseEntity com status HTTP apropriado
- [ ] @CrossOrigin adicionado (se frontend remoto)
- [ ] Testes de integração para endpoints
- [ ] Verificado em Swagger (/swagger-ui.html)
- [ ] Filtros opcionais com @RequestParam(required=false)
- [ ] Paginação com page + size
- [ ] Sem lógica de negócio (delegado para service)

---

## 16. Conclusão

A camada de Controllers **conecta o cliente HTTP com a lógica de negócio**. Com:
- ✅ **23 endpoints REST** bem-estruturados
- ✅ **Validação rigorosa** de entrada
- ✅ **Tratamento centralizado** de erros
- ✅ **CORS habilitado** para frontends remotos
- ✅ **Documentação automática** via Swagger

### Próximas Ações:
- ❌ Implementar autenticação (JWT)
- ❌ Implementar autorização (RBAC)
- ✅ Endpoints expostos e documentados
