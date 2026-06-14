# Arquitetura do Sistema GINI

## 1. Visão Geral Arquitetural

### Propósito do Sistema
O sistema **GINI** (Grade Horária Inteligente) é um backend de gerenciamento acadêmico desenvolvido para automatizar a criação, validação e gerenciamento de grades horárias das instituições de ensino superior. O sistema reduz conflitos e validações manuais por meio de algoritmos de validação de regras de negócio.

### Tecnologias Principais
- **Framework**: Spring Boot 3.5.14
- **Linguagem**: Java 21
- **Persistência**: Spring Data JPA + H2 (desenvolvimento) / PostgreSQL (produção)
- **Segurança**: Spring Security com autenticação básica
- **Validação**: Jakarta Bean Validation (annotations)
- **Documentação API**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven 3.9+
- **Transaction Management**: JPA com suporte a transações distribuídas

---

## 2. Arquitetura em Camadas

### Estrutura de Camadas
```
┌─────────────────────────────────────────────────────────┐
│  Camada de Apresentação (web.controller)                │
│  - Endpoints REST                                        │
│  - Request/Response validation                           │
│  - HTTP status codes                                     │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│  Camada de Aplicação (domain.services)                  │
│  - Orquestração de usecases                             │
│  - Composição de validações                             │
│  - Mapeamento de DTOs                                   │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│  Camada de Domínio (domain.services.usecase)            │
│  - Read UseCases (validações)                           │
│  - Write UseCases (operações de persistência)           │
│  - Regras de negócio encapsuladas                       │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│  Camada de Infraestrutura (infrastructure)              │
│  - Repositories (acesso a dados)                        │
│  - Mappers (conversão DTO ↔ Entity)                     │
│  - Conversores (tipos especiais)                        │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│  Camada de Persistência (JPA/Banco de Dados)            │
│  - Entidades JPA                                        │
│  - Relacionamentos (1:N, N:M, etc)                      │
│  - Schema do banco                                      │
└─────────────────────────────────────────────────────────┘
```

### Pacotes Principais

```
com.fatec.gini/
├── web/                              # Camada de Apresentação
│   ├── controller/                   # Endpoints REST (23 controllers)
│   ├── config/                       # Configurações (segurança, etc)
│   └── exception/                    # Tratamento global de exceções
│
├── domain/                           # Camada de Domínio
│   ├── entities/                     # Entidades JPA (17 entidades)
│   └── services/                     # Serviços de negócio
│       ├── *.Service                 # 14 serviços principais
│       └── usecase/
│           ├── read/                 # 13 ValidarXXXUseCase (read-only)
│           └── write/                # 5 XXXUseCase (write operations)
│
├── infrastructure/                   # Camada de Infraestrutura
│   ├── repositories/                 # 17 repositories Spring Data JPA
│   ├── mappers/                      # 17 mappers (entity ↔ DTO)
│   └── converters/                   # Conversores especializados
│
└── dto/                              # Transfer Objects (entrada/saída)
    └── [módulos]/                    # 17 grupos de DTOs (Request/Response)
```

---

## 3. Padrões e Convenções Arquiteturais

### 3.1 Padrão de Camadas (Layered Architecture)
- **Separação clara de responsabilidades** entre camadas
- **Acoplamento horizontal**: controllers → services → repositories
- **Acoplamento vertical mínimo**: cada camada conhece apenas a camada imediatamente inferior

### 3.2 Padrão Repository
- **Spring Data JPA** para acesso a dados
- **Interfaces de Repository** com métodos nomeados semanticamente
- **Métodos de queryagem customizados** em `AlocacaoRepository` com `@Query`

### 3.3 Padrão DTO (Data Transfer Object)
- **Request DTOs**: recebem dados da API (com validações)
- **Response DTOs**: retornam dados ao cliente
- **Records Java** para imutabilidade e concisão
- **Mappers especializados** (padrão Mapper) para conversão entidade ↔ DTO

### 3.4 Padrão UseCase
- **CriarAlocacaoUseCase**: orquestra validações e persistência de alocações
- **AtualizarAlocacaoUseCase**: gerencia atualização com histórico
- **ValidarXXXUseCase** (13 use cases): validações específicas de negócio (read-only)
- **Composição de usecases**: cada usecase agrupa múltiplas validações

### 3.5 Padrão Service
- **AlocacaoService, GradeHorariaService, etc**: orquestram usecases
- **Responsabilidade**: mapear DTOs e coordenar fluxo de execução
- **Transacionalidade**: marcadas com `@Transactional`

### 3.6 Tratamento de Exceções
- **ControllerAdvice global**: centraliza tratamento de exceções
- **Exceções customizadas**: `BusinessException`, `DatabaseException`, `ParameterException`
- **Mapeamento de status HTTP**:
  - 404: `EntityNotFoundException`
  - 409: `BusinessException` (conflito de regra de negócio)
  - 422: `MethodArgumentNotValidException` (validação de DTO)
  - 400: `DataIntegrityViolationException`, `ParameterException`

### 3.7 Validação em Camadas
1. **Nível DTO**: validações com `@NotNull`, `@NotEmpty`, etc (Bean Validation)
2. **Nível Service**: verificação de existência de referências obrigatórias
3. **Nível UseCase**: validações de regras de negócio complexas

---

## 4. Regras Arquiteturais e Convenções Técnicas

### 4.1 Convenções de Nomenclatura

| Elemento | Convenção | Exemplo |
|----------|-----------|---------|
| Entidades JPA | PascalCase | `Alocacao`, `GradeHoraria` |
| Controllers | PascalCase + "Controller" | `AlocacaoController` |
| Services | PascalCase + "Service" | `AlocacaoService` |
| Repositories | PascalCase + "Repository" | `AlocacaoRepository` |
| Mappers | PascalCase + "Mapper" | `AlocacaoMapper` |
| Read UseCases | "Validar" + PascalCase + "UseCase" | `ValidarCapacidadeSalaUseCase` |
| Write UseCases | Verb + PascalCase + "UseCase" | `CriarAlocacaoUseCase`, `AtualizarAlocacaoUseCase` |
| DTOs Request | PascalCase + "Request" | `AlocacaoRequest` |
| DTOs Response | PascalCase + "Response" | `AlocacaoResponse` |
| Métodos Controllers | camelCase (ação RESTful) | `criar()`, `atualizar()`, `deletar()` |
| Métodos Services | camelCase (ação de domínio) | `executar()`, `validar()` |

### 4.2 Regras de Responsabilidade

| Camada | Responsabilidades | O que NÃO fazer |
|--------|-------------------|-----------------|
| **Controller** | Receber requisições HTTP, validar entrada, chamar service, retornar resposta | Lógica de negócio, acesso direto ao banco |
| **Service** | Orquestrar usecases, mapear DTOs, coordenar transações | Validação de negócio, acesso direto ao banco |
| **UseCase (Read)** | Validar regras de negócio, retornar verdadeiro/falso | Alterar estado, persistir dados |
| **UseCase (Write)** | Persistir dados, chamar validações, registrar histórico | Validações complexas sem isolamento |
| **Repository** | Acessar banco de dados, executar queries | Lógica de negócio, manipulação de dados |
| **Mapper** | Converter entre entidades e DTOs | Validação, lógica de negócio |

### 4.3 Regras de Transações

- **@Transactional** obrigatório em operações de escrita (CREATE, UPDATE, DELETE)
- **@Transactional(readOnly = true)** em consultas para otimização
- **Propagação**: `REQUIRED` (padrão) para usecases aninhados
- **Isolamento**: `READ_COMMITTED` (padrão PostgreSQL)

### 4.4 Regras de Acesso a Dados

- **Nunca construir queries diretas** fora de Repositories
- **Preferir métodos nomeados** (`findByXXX()`, `existsXXX()`)
- **Usar @Query** apenas para queries complexas documentadas
- **Lazy loading cuidadoso**: risco de N+1 queries

### 4.5 Regras de Segurança (RBAC por Curso)

- **Spring Security obrigatório** com autenticação local (sem SSO/LDAP na Fase 1)
- **BCryptPasswordEncoder** para criptografia de senhas
- **Perfis de usuário na Fase 1**: 
  - `ADMIN`: 100% acesso a todos os dados e operações
  - `COORDENADOR`: Acesso limitado apenas ao seu CURSO
- **Sem Professor como usuário na Fase 1** (Fase 2+)
- **CSRF desabilitado** para APIs REST (curl, ajax)
- **Filtro por Curso**: Toda query deve validar `grade.curso_id == usuario.curso_id`
- **Auditoria**: ADMIN vê tudo, COORDENADOR vê apenas seu curso

---

## 5. Separação de Responsabilidades

### 5.1 Matriz de Responsabilidades

```
┌─────────────────────────────────────────────────────────────────────┐
│  CONTROLLER (AlocacaoController)                                    │
│  ↓ Recebe POST /alocacoes                                           │
│  ↓ Valida DTO com @Valid                                            │
│  ↓ Chama service.criar(request)                                     │
│  ↓ Retorna 201 Created + AlocacaoResponse                           │
└─────────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────────┐
│  SERVICE (AlocacaoService)                                          │
│  ↓ Recebe AlocacaoRequest                                           │
│  ↓ Mapeia para Alocacao entity (AlocacaoMapper.toEntity)            │
│  ↓ Chama criarAlocacaoUseCase.executar(entity, usuarioId)           │
│  ↓ Mapeia resultado para AlocacaoResponse                           │
│  ↓ Retorna resposta ao controller                                   │
└─────────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────────┐
│  USECASE - WRITE (CriarAlocacaoUseCase)                             │
│  ↓ Validação de referências obrigatórias                            │
│  ↓ Validação de compatibilidade disciplina-sala                     │
│  ↓ 11+ validações de negócio (via ValidarXXX usecases)              │
│  ↓ Persiste alocacao (alocacaoRepository.save)                      │
│  ↓ Registra histórico de alteração                                  │
│  ↓ Retorna Alocacao salva                                           │
└─────────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────────┐
│  USECASE - READ (13 ValidarXXX)                                     │
│  ├─ ValidarCapacidadeSalaUseCase                                    │
│  ├─ ValidarDisponibilidadeProfessorUseCase                          │
│  ├─ ValidarConflitoSalaHorarioUseCase                               │
│  ├─ ValidarVinculoProfessorDisciplinaUseCase                        │
│  └─ ... (10 outras validações)                                      │
│                                                                      │
│  ↓ Cada um: consulta banco, valida regra, lança exceção se falhar   │
│  ↓ Lógica encapsulada e reutilizável                                │
└─────────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────────┐
│  REPOSITORY (AlocacaoRepository)                                    │
│  ↓ findById, save, delete, findAll                                  │
│  ↓ buscarPorFiltros (custom query)                                  │
│  ↓ Acesso único a dados via JPA                                     │
└─────────────────────────────────────────────────────────────────────┘
                            ↓
                    [DATABASE]
```

---

## 6. Fluxo de Comunicação entre Módulos

### 6.1 Fluxo de Criação de Alocação (Happy Path)

```
1. Cliente HTTP
   └─→ POST /alocacoes
       ├─ Body: AlocacaoRequest (JSON)
       └─ Response: 201 Created + AlocacaoResponse

2. AlocacaoController.criar()
   └─→ service.criar(request)

3. AlocacaoService.criar()
   └─→ AlocacaoMapper.toEntity(request)
   └─→ criarAlocacaoUseCase.executar(entity, usuarioId)

4. CriarAlocacaoUseCase.executar()
   Validações executadas em sequência:
   ├─ ValidarReferenciasObrigatoriasAlocacao (Turma, Disciplina, Sala, Usuario, DiaSemana, Horario, Grade)
   ├─ ValidarDisciplinaTipoSala (compatibilidade)
   ├─ ValidarCargaHorariaDisciplina (limite máximo)
   ├─ ValidarCargaHorariaMaximaProfessor (limite diário)
   ├─ ValidarGradeHoraria (grade ativa e válida)
   ├─ ValidarVinculoProfessorDisciplina (professor apto)
   ├─ ValidarDisponibilidadeProfessor (professor disponível)
   ├─ ValidarConflitoTurmaHorario (turma sem conflito)
   ├─ ValidarConflitoSalaHorario (sala sem conflito)
   ├─ ValidarCapacidadeSala (capacidade física)
   ├─ ValidarDuplicidadeAlocacao (sem duplicação)
   └─ ValidarCoerenciaCurso (coerência de cursos)

5. Persistência
   └─→ alocacaoRepository.save(entity)
   └─→ historicoAlocacaoRepository.save(historico)

6. Retorno
   └─→ AlocacaoMapper.toResponse(alocacaoSalva)
   └─→ ResponseEntity<AlocacaoResponse> (200 OK)
```

### 6.2 Mapa de Dependências entre Serviços

```
AlocacaoService
├── CriarAlocacaoUseCase
│   ├── ValidarReferenciasObrigatoriasAlocacaoUseCase
│   ├── ValidarDisciplinaTipoSalaUseCase
│   ├── ValidarCargaHorariaDisciplinaUseCase
│   ├── ValidarGradeHorariaUseCase
│   ├── ValidarVinculoProfessorDisciplinaUseCase
│   ├── ValidarDisponibilidadeProfessorUseCase
│   ├── ValidarConflitoTurmaHorarioUseCase
│   ├── ValidarConflitoSalaHorarioUseCase
│   ├── ValidarDuplicidadeAlocacaoUseCase
│   ├── ValidarCapacidadeSalaUseCase
│   ├── RegistrarHistoricoAlocacaoUseCase
│   ├── ValidarCargaHorariaMaximaProfessorUseCase
│   └── ValidarCoerenciaUseCase
│
├── AtualizarAlocacaoUseCase
│   └── (similar ao criar + histórico de mudança)
│
└── AlocacaoRepository
    └── [Banco de Dados]
```

### 6.3 Grafo de Relacionamentos de Entidades

```
┌──────────────────┐
│    GradeHoraria  │──┐ (1:N) ┌─────────────┐
│  ├─ versao       │  └─────→ │  Alocacao   │
│  ├─ status       │          │ ├─ turma    │
│  └─ periodoLetivo│          │ ├─ disciplina
└──────────────────┘          │ ├─ sala
                              │ ├─ usuario
                              │ ├─ diaSemana
                              │ └─ horario
                              └─────────────┘
                                     │
                                     │(1:N)
                                     ↓
                            ┌─────────────────────┐
                            │ HistoricoAlteracao  │
                            │ ├─ tipo             │
                            │ ├─ dataAlteracao    │
                            │ └─ justificativa    │
                            └─────────────────────┘

┌──────────────┐
│   Usuario    │◄─────────────────────────┐
│ ├─ tipoUser  │                          │
│ └─ senha     │       ┌──────────────┐   │
└──────────────┘       │  Disciplina  │   │
       ▲               │ ├─ cargaHor  │   │
       │               │ └─ tipoSala  │   │
       │               └──────────────┘   │
       │                      ▲            │
       │                      │            │
┌──────┴──────────┐    ┌──────┴────────┐  │
│ ProfessorDiscipl│    │   Turma       │  │
│ ├─ professor    ├────│ ├─ curso      │  │
│ └─ disciplina   │    │ └─ capacidade │  │
└─────────────────┘    └───────────────┘  │
                              ▲            │
                              │            │
                         ┌────┴──────┐    │
                         │   Curso   │    │
                         └───────────┘    │
                                          │
                          ┌───────────┐   │
                          │   Sala    │───┘
                          │ ├─ tipo   │
                          │ └─ capac  │
                          └───────────┘
```

---

## 7. Dependências Críticas

### 7.1 Dependências Externas (pom.xml)

| Dependência | Versão | Propósito | Criticidade |
|-------------|--------|----------|-------------|
| Spring Boot Starter Web | 3.5.14 | Endpoints REST, DispatcherServlet | **CRÍTICA** |
| Spring Boot Starter Data JPA | 3.5.14 | ORM, persistência | **CRÍTICA** |
| Spring Boot Starter Validation | 3.5.14 | Bean Validation (JSR-380) | **ALTA** |
| Spring Boot Starter Security | 3.5.14 | Autenticação e autorização | **ALTA** |
| SpringDoc OpenAPI Starter | 2.8.15 | Documentação Swagger | **MÉDIA** |
| H2 Database | Latest | Banco em memória (dev) | **MÉDIA** |
| Jakarta Persistence | 6.x | JPA annotations | **CRÍTICA** |
| Spring Boot DevTools | 3.5.14 | Hot reload (dev) | **BAIXA** |
| Spring Boot Test | 3.5.14 | Testes unitários e integração | **ALTA** |

### 7.2 Dependências Internas (entre módulos)

**Direção obrigatória de dependências** (respeita camadas):
- **Controller** → Service → Repository → Database
- **Service** → UseCase → Repository
- **UseCase** → Repository → Database

**Acoplamentos permitidos**:
- Controller + Service = DTOs (mapeamento bidirecional)
- Service + UseCase = Entidades (reutilização)
- Múltiplos UseCases = Validações especializadas

### 7.3 Pontos de Risco em Dependências

1. **CriarAlocacaoUseCase**: 13 dependências de validação
   - Risco: mudança em uma validação quebra o fluxo
   - Mitigação: cada validação é independente e testada

2. **AlocacaoRepository**: queries complexas
   - Risco: mudança no modelo quebra queries
   - Mitigação: usar @Query documentadas e versionadas

3. **Relacionamentos circulares potenciais**:
   - Alocacao ↔ GradeHoraria (controlado via JPA mapping)
   - Alocacao → Usuário → TipoUsuário (lazy loading)

---

## 8. Riscos Técnicos e Acoplamentos Importantes

### 8.1 Riscos Identificados

| Risco | Severidade | Descrição | Mitigação |
|-------|-----------|-----------|-----------|
| **N+1 Queries** | ALTA | Lazy loading em alocações retorna professor para cada alocação | Use @Transactional(readOnly=true), considere EAGER loading seletivo |
| **Segurança aberta** | CRÍTICA | ConfiguracaoSeguranca permite todo acesso (TODO no código) | Implementar autenticação real e autorização por perfil |
| **Mapper incompleto** | ALTA | AlocacaoMapper.toEntity() retorna entity vazia (lógica faltando) | Completar mapeamento de campos do DTO para entidade |
| **Validações no UseCase** | MÉDIA | 13 validações executadas sequencialmente sem paralelismo | Considerar execução paralela de validações independentes |
| **Falta de índices DB** | MÉDIA | Queries sem índices em colunas de FK | Adicionar índices em foreign keys críticas (turma, usuario, sala) |
| **Histórico sempre criado** | BAIXA | HistoricoAlteracao criado mesmo em falhas de validação | Mover registro de histórico para após sucesso de persistência |
| **Transações aninhadas** | MÉDIA | CriarAlocacaoUseCase chama RegistrarHistoricoUseCase em transação | Validar comportamento em rollback parcial |
| **Sem versionamento de API** | MÉDIA | Não há v1/, v2/ na URL dos endpoints | Considerar estratégia de versionamento futuro |

### 8.2 Acoplamentos Fortes Identificados

```
1. ACOPLAMENTO TEMPORAL
   └─ CriarAlocacaoUseCase depende de ordem de validações
   └─ Risco: mudar ordem quebra lógica de negócio
   └─ Mitigação: documentar ordem esperada, adicionar testes

2. ACOPLAMENTO DE DADOS
   └─ Alocacao depende de 7 entidades diferentes
   └─ Risco: mudança em Turma/Sala/Usuario quebra Alocacao
   └─ Mitigação: usar DTOs para desacoplamento, adicionar camada de abstração

3. ACOPLAMENTO DE BANCO
   └─ AlocacaoRepository usa queries customizadas
   └─ Risco: mudar schema quebra queries
   └─ Mitigação: usar JPA criteria API, versionamento de schema

4. ACOPLAMENTO COM HISTÓRICO
   └─ Alocacao → HistoricoAlteracao (OneToMany)
   └─ Risco: deletar alocacao sem atualizar histórico
   └─ Mitigação: usar cascade delete configurado corretamente
```

### 8.3 Débitos Técnicos Identificados

1. **Segurança incompleta**: ConfiguracaoSeguranca marcada com TODO
2. **Mapper vazio**: AlocacaoMapper.toEntity() não popula campos
3. **Testes limitados**: apenas 8 testes unitários para 13+ validações
4. **Documentação de código**: faltam JavaDoc em várias classes
5. **Validações não paralelas**: execução sequencial de 13 validações
6. **Sem auditoria completa**: histórico não rastreia todos os campos
7. **Sem cache**: todas as queries consultam banco (sem Redis/Cache)

---

## 9. Diretrizes para Futuras Implementações

### 9.1 Adição de Novo Caso de Uso

Ao adicionar um novo caso de uso (ex: "Deletar Alocação com regras"):

1. **Criar NovoUseCase** em `domain.services.usecase.write`
   ```
   @Service
   public class DeletarAlocacaoComValidacaoUseCase {
       @Autowired
       private ValidarCanDeletarAlocacaoUseCase validar;
       @Autowired
       private AlocacaoRepository repo;
       
       @Transactional
       public void executar(Long id) {
           validar.executar(id);
           repo.deleteById(id);
       }
   }
   ```

2. **Criar Validações necessárias** em `domain.services.usecase.read`
   - ValidarCanDeletarAlocacaoUseCase
   - ValidarAlocacaoComDependenciasUseCase

3. **Adicionar Service** em `domain.services`
   - DeletarAlocacaoService que orquestra o usecase

4. **Adicionar Endpoint** em `web.controller`
   - @DeleteMapping com validações de DTO

5. **Adicionar Testes** em `src/test/java`
   - Testes unitários para cada validação
   - Testes de integração para fluxo completo

### 9.2 Adição de Nova Entidade

Ao adicionar nova entidade (ex: "Justificativa"):

1. Criar entidade em `domain.entities.Justificativa`
2. Criar repository em `infrastructure.repositories.JustificativaRepository`
3. Criar mapper em `infrastructure.mappers.JustificativaMapper`
4. Criar DTOs em `dto.justificativa` (Request/Response)
5. Criar service em `domain.services.JustificativaService`
6. Criar controller em `web.controller.JustificativaController`
7. Adicionar relacionamentos nas entidades existentes
8. Atualizar CriarAlocacaoUseCase se aplicável

### 9.3 Princípios a Manter

1. **Coesão alta**: cada classe tem uma única responsabilidade
2. **Acoplamento baixo**: usar injeção de dependência, evitar new()
3. **SOLID principles**:
   - **S**ingle Responsibility: cada usecase valida uma coisa
   - **O**pen/Closed: extensível sem modificar código existente
   - **L**iskov Substitution: repository intercambiável
   - **I**nterface Segregation: usar interfaces mínimas
   - **D**ependency Inversion: depender de abstrações, não implementações

4. **DRY** (Don't Repeat Yourself): reutilizar validações em múltiplos usecases
5. **Transacionalidade**: sempre usar @Transactional em operações de escrita
6. **Tratamento de exceções**: nunca engolir exceções, sempre loguear

### 9.4 Checklist para Pull Requests (Code Review)

- [ ] Segue convenções de nomenclatura
- [ ] Tem testes unitários (>80% coverage)
- [ ] Respeita camadas arquiteturais
- [ ] Não há consultas N+1
- [ ] @Transactional presente em writes
- [ ] Exceções são capturadas e mapeadas em ResourceExceptionHandler
- [ ] DTOs com @Valid no Controller
- [ ] Sem logagem de dados sensíveis
- [ ] Documentação atualizada
- [ ] Sem duplicação de código

### 9.5 Estratégia de Otimização Futura

1. **Caching**: adicionar Redis para consultas frequentes (períodos letivos, horários)
2. **Validação paralela**: executar validações independentes em threads separadas
3. **Eventos**: usar Spring Events para histórico e notificações assíncronas
4. **Paginação**: implementar cursor-based pagination para grandes datasets
5. **Índices**: adicionar índices compostos em queries críticas
6. **Batch operations**: suportar criação/atualização em massa

### 9.6 Estratégia de Segurança Futura

1. **Implementar JWT**: substituir BasicAuth por JWT tokens
2. **OAuth2**: integrar com provedor SSO institucional
3. **RBAC completo**: enforcer roles (ADMIN, COORDENADOR, PROFESSOR)
4. **Rate limiting**: proteção contra abuso de API
5. **Auditoria**: logs de acesso e modificações
6. **Criptografia**: dados sensíveis em repouso e em trânsito

---

## 10. Matriz de Referência Rápida

### Controllers (23 endpoints REST)
```
AlocacaoController        → CRUD Alocações
GradeHorariaController    → CRUD Grades + cópia
HistoricoAlteracaoController → Consulta histórico
CursoController           → CRUD Cursos
DisciplinaController      → CRUD Disciplinas
TurmaController           → CRUD Turmas
UsuarioController         → CRUD Usuários
SalaController            → CRUD Salas
HorarioController         → CRUD Horários
... (13 controllers adicionais)
```

### Services (14 serviços)
```
AlocacaoService → persistência de alocações
GradeHorariaService → gestão de grades
HistoricoAlteracaoService → rastreamento
ProfessorDisciplinaService → vínculos
... (10 services adicionais)
```

### UseCases - Read (13 validações)
```
ValidarCapacidadeSalaUseCase
ValidarDisponibilidadeProfessorUseCase
ValidarConflitoSalaHorarioUseCase
ValidarConflitoTurmaHorarioUseCase
ValidarVinculoProfessorDisciplinaUseCase
ValidarCargaHorariaDisciplinaUseCase
ValidarCargaHorariaMaximaProfessorUseCase
ValidarDuplicidadeAlocacaoUseCase
ValidarGradeHorariaUseCase
ValidarDisciplinaTipoSalaUseCase
ValidarCoerenciaUseCase
ValidarReferenciasObrigatoriasAlocacaoUseCase
ValidarDisciplinaSemVinculosUseCase
... (mais validações)
```

### UseCases - Write (5 operações)
```
CriarAlocacaoUseCase
AtualizarAlocacaoUseCase
RegistrarHistoricoAlocacaoUseCase
CopiarGradeHorariaUseCase
ValidarGradeHorariaValidaUseCase
```

### Repositories (17 acesso a dados)
```
AlocacaoRepository → queries de alocação
GradeHorariaRepository → queries de grade
... (15 repositories adicionais)
```

---

## Conclusão

A arquitetura do GINI segue princípios de **Layered Architecture** com separação clara entre apresentação, negócio e persistência. O uso de **UseCases** e **Validações especializadas** permite composição flexível de regras de negócio. Os **padrões Repository, DTO e Mapper** facilitam manutenção e testes.

**Próximas prioridades**:
1. Completar implementação de segurança
2. Completar mapeadores de DTO
3. Aumentar cobertura de testes
4. Documentar validações específicas
5. Otimizar queries para evitar N+1
