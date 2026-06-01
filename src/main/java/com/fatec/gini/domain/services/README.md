# Módulo: Alocações

## 1. Objetivo do Módulo

O módulo de **Alocações** é o **núcleo funcional** do sistema GINI. Gerencia a criação, atualização e validação de alocações de aulas, que são a unidade fundamental de uma grade horária.

Uma **alocação** representa uma aula específica: turma X disciplina X professor X sala X dia X horário.

### Responsabilidade Principal
Garantir que cada alocação criada no sistema respeite **todas as 13+ regras de negócio críticas**, prevenindo conflitos e garantindo a viabilidade da grade horária.

---

## 2. Funcionalidades Existentes

### 2.1 Operações CRUD

```
POST   /alocacoes
├─ Criar nova alocação
├─ Validação de todas as 13 regras de negócio
├─ Registro automático de histórico
└─ Resposta: 201 Created + AlocacaoResponse

GET    /alocacoes
├─ Listar alocações com filtros avançados
├─ Suporte a paginação
└─ Parâmetros: turma, disciplina, sala, usuario, dia_semana, horario, grade, page, size

GET    /alocacoes/{id}
├─ Buscar alocação por ID
└─ Retorna detalhes completos com relacionamentos

PUT    /alocacoes/{id}
├─ Atualizar alocação existente
├─ Exige justificativa de alteração (para auditoria)
├─ Registra mudança no histórico
└─ Resposta: 200 OK + AlocacaoResponse

DELETE /alocacoes/{id}
├─ Deletar alocação
├─ Verifica se alocação pode ser removida
└─ Resposta: 204 No Content
```

### 2.2 Validações Automáticas

Quando uma alocação é **criada** ou **atualizada**, o sistema executa automaticamente:

| # | Validação | O que verifica |
|---|-----------|----------------|
| 1 | **Referências Obrigatórias** | Turma, Disciplina, Sala, Usuario, DiaSemana, Horario, GradeHoraria existem? |
| 2 | **Compatibilidade Disciplina-Sala** | Disciplina de "Programação" está em "Laboratório"? |
| 3 | **Carga Horária Disciplina** | Disciplina "Programação" não ultrapassou 4h/semana? |
| 4 | **Carga Horária Máxima Professor** | Professor não ultrapassou 20h/semana? |
| 5 | **Disponibilidade Professor** | Professor está disponível neste dia/horário? |
| 6 | **Conflito Turma-Horário** | Turma já tem aula neste dia/horário? |
| 7 | **Conflito Sala-Horário** | Sala já está alocada neste dia/horário? |
| 8 | **Vínculo Professor-Disciplina** | Professor está apto para ministrar esta disciplina? |
| 9 | **Grade Horária Ativa** | Grade está em status RASCUNHO ou VIGENTE (não ARQUIVO)? |
| 10 | **Capacidade Sala** | Sala tem capacidade para turma (40 alunos em sala de 35 é erro)? |
| 11 | **Duplicidade** | Não existe alocação idêntica já salva? |
| 12 | **Carga Horária Diária** | Professor não tem mais de X horas no mesmo dia? |
| 13 | **Coerência de Cursos** | Turma pertence ao mesmo curso da grade? |

**Se alguma validação falhar**: Sistema retorna erro 409 Conflict com mensagem específica.

### 2.3 Histórico de Alterações

Cada operação em alocação é registrada automaticamente:

```
CRIAR alocação
  └─ HistoricoAlteracao com tipo: CRIACAO

ATUALIZAR alocação
  └─ HistoricoAlteracao com tipo: ATUALIZACAO + justificativa do usuário

DELETAR alocação
  └─ Registra deleção (soft delete considerado para futuro)
```

Cada histórico contém:
- Tipo de operação (CRIACAO, ATUALIZACAO)
- Data/hora do registro
- Usuário que realizou operação
- Justificativa (para updates)
- Campos alterados (futura melhoria)

---

## 3. Dependências Internas

### 3.1 Dependências do Módulo (Entidades Relacionadas)

```
Alocacao
  ├─→ Turma (obrigatório)
  │   └─→ Curso
  │       └─→ Status (ATIVO/INATIVO)
  │
  ├─→ Disciplina (obrigatório)
  │   ├─→ Carga Horária
  │   └─→ Tipo de Sala requerido
  │
  ├─→ Sala (obrigatório)
  │   ├─→ Tipo de Sala
  │   ├─→ Capacidade
  │   └─→ Recursos
  │
  ├─→ Usuario (obrigatório - Professor)
  │   ├─→ Tipo de Usuário
  │   └─→ Disponibilidade de Professor
  │
  ├─→ DiaSemana (obrigatório)
  │   └─→ Nome (Segunda, Terça, etc)
  │
  ├─→ Horario (obrigatório)
  │   ├─→ Hora de início
  │   └─→ Hora de fim
  │
  ├─→ GradeHoraria (obrigatório)
  │   ├─→ Versão
  │   ├─→ Status (RASCUNHO, VIGENTE, ARQUIVO)
  │   ├─→ PeriodoLetivo
  │   └─→ Curso
  │
  └─→ HistoricoAlteracao (gerado automaticamente)
      ├─→ Usuario (quem fez)
      ├─→ Data/Hora
      └─→ Justificativa
```

### 3.2 Fluxo de Dependências (Como são resolvidas)

```
AlocacaoController
  ↓ @PostMapping /alocacoes
  ↓ recebe AlocacaoRequest (DTOs com IDs)
  ↓ chama AlocacaoService.criar()
  │
  └─→ AlocacaoMapper.toEntity()
      └─ Cria Alocacao vazia (PROBLEMA: não copia IDs!)
  
  └─→ CriarAlocacaoUseCase.executar()
      │
      ├─ ValidarReferenciasObrigatoriasAlocacaoUseCase
      │  └─ Busca entities pelo ID em cada repository
      │     ├─ turmaRepository.findById(request.turma().id())
      │     ├─ disciplinaRepository.findById(request.disciplina().id())
      │     ├─ salaRepository.findById(request.sala().id())
      │     ├─ usuarioRepository.findById(request.usuario().id())
      │     ├─ diaSemanaRepository.findById(request.diaSemana().id())
      │     ├─ horarioRepository.findById(request.horario().id())
      │     └─ gradeHorariaRepository.findById(request.gradeHoraria().id())
      │  └─ Se qualquer um falhar → EntityNotFoundException
      │
      ├─ ValidarDisciplinaTipoSalaUseCase
      │  └─ Compara tipo de sala requerido vs tipo de sala oferecido
      │
      ├─ [outras 11 validações]
      │  └─ Cada uma busca dados, compara com regra
      │
      └─ AlocacaoRepository.save(alocacao)
         └─ Persiste em banco
```

---

## 4. Módulos Relacionados

### 4.1 Módulos que DEPENDEM de Alocação

```
┌─────────────────────────────────────┐
│  GradeHoraria (1:N)                │
│  ├─ Uma grade contém muitas        │
│  │  alocações                       │
│  └─ Operações: buscar alocações   │
│     de uma grade, copiar grade      │
└─────────────────────────────────────┘
         ▲
         │ depende de
         │
Alocacao

┌─────────────────────────────────────┐
│  HistoricoAlteracao (1:N)           │
│  ├─ Uma alocação tem histórico      │
│  └─ Operações: consultar mudanças  │
└─────────────────────────────────────┘
         ▲
         │ gerado por
         │
Alocacao
```

### 4.2 Módulos dos quais Alocação DEPENDE

```
Turma, Disciplina, Sala, Usuario, DiaSemana, Horario, GradeHoraria
  └─ Devem existir ANTES de criar alocação
  └─ Se qualquer um não existir → erro 404 EntityNotFoundException
  └─ Se qualquer um mudar status (ex: sala desativada) → validação futura
```

### 4.3 Fluxo de Criação com Dependências

```
Prerequisito: Todos os cadastros existem
  ├─ Turma ADS-2A (id: 5)
  ├─ Disciplina Programação II (id: 12)
  ├─ Sala LAB-01 (id: 3)
  ├─ Professor João (id: 8)
  ├─ DiaSemana Segunda (id: 1)
  ├─ Horario 08:00-10:00 (id: 2)
  └─ GradeHoraria 2025-1 ADS (id: 10)

Usuario faz POST /alocacoes:
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

Sistema processa:
  1. Valida AlocacaoRequest (notNulls)
  2. Busca todas as entities pelos IDs
     ├─ Turma(5) ← OK
     ├─ Disciplina(12) ← OK
     ├─ Sala(3) ← OK
     ├─ Usuario(8) ← OK
     ├─ DiaSemana(1) ← OK
     ├─ Horario(2) ← OK
     ├─ GradeHoraria(10) ← OK
     └─ Usuario alteracao(1) ← OK
  3. Executa 13 validações (todas acessam dados de relacionamentos)
  4. Se TUDO OK: salva alocacao
  5. Retorna 201 Created
```

---

## 5. Pontos de Entrada

### 5.1 Controller REST

**Classe**: `AlocacaoController`
**Localização**: `com.fatec.gini.web.controller.AlocacaoController`

```java
@RestController
@RequestMapping("/alocacoes")
@CrossOrigin
public class AlocacaoController {
    
    // 1. POST /alocacoes
    @PostMapping
    public ResponseEntity<AlocacaoResponse> criar(@RequestBody @Valid AlocacaoRequest request)
    
    // 2. GET /alocacoes
    @GetMapping
    public ResponseEntity<Page<AlocacaoResponse>> listar(...)
    
    // 3. GET /alocacoes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AlocacaoResponse> buscarPorId(@PathVariable Long id)
    
    // 4. PUT /alocacoes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<AlocacaoResponse> atualizar(@PathVariable Long id, @RequestBody @Valid AlocacaoRequest request)
    
    // 5. DELETE /alocacoes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id)
}
```

### 5.2 Service Orquestrador

**Classe**: `AlocacaoService`
**Localização**: `com.fatec.gini.domain.services.AlocacaoService`
**Responsabilidade**: Mapear DTOs e orquestrar usecases

```java
public AlocacaoResponse criar(AlocacaoRequest request) {
    Alocacao entity = AlocacaoMapper.toEntity(request);
    return AlocacaoMapper.toResponse(
        criarAlocacaoUseCase.executar(entity, request.usuarioAlteracao().id())
    );
}
```

### 5.3 UseCase Principal (Write)

**Classe**: `CriarAlocacaoUseCase`
**Localização**: `com.fatec.gini.domain.services.usecase.write`
**Responsabilidade**: Executar validações e persistir

```java
@Transactional
public Alocacao executar(Alocacao entity, long usuarioAlteracao) {
    // 13+ validações
    validarRefObrigatorias.validar(entity);
    validarDisciplinaTipoSala.validarCompatibilidade(...);
    // ... etc
    
    // Persistir
    Alocacao alocacaoSalva = alocacaoRepository.save(entity);
    
    // Histórico
    historicoAlocacaoUseCase.registrarCriacao(alocacaoSalva, usuarioAlteracaoEntity);
    
    return alocacaoSalva;
}
```

---

## 6. Fluxos Importantes

### 6.1 Fluxo de Criação (Happy Path)

```
1. Cliente envia POST /alocacoes
   └─ Corpo: AlocacaoRequest (JSON com IDs)

2. AlocacaoController.criar()
   ├─ @Valid valida DTO (@NotNull campos)
   └─ chama AlocacaoService.criar()

3. AlocacaoService.criar()
   ├─ AlocacaoMapper.toEntity() → cria Alocacao vazia
   └─ chama CriarAlocacaoUseCase.executar()

4. CriarAlocacaoUseCase.executar()
   ├─ ValidarReferenciasObrigatoriasAlocacao
   │  └─ busca todas as entities pelos IDs em request
   │  └─ se alguma não encontrada → EntityNotFoundException
   │
   ├─ ValidarDisciplinaTipoSala
   │  └─ disciplina.tipoSalaNecessario == sala.tipo ?
   │  └─ se não → BusinessException
   │
   ├─ ValidarCargaHorariaDisciplina
   │  └─ soma horas de disciplina → <= limite?
   │  └─ se não → BusinessException
   │
   ├─ ValidarCargaHorariaMaximaProfessor
   │  └─ soma horas do professor neste dia
   │  └─ se não → BusinessException
   │
   ├─ ValidarGradeHoraria
   │  └─ grade.status == RASCUNHO ou VIGENTE?
   │  └─ se não → BusinessException
   │
   ├─ ValidarVinculoProfessorDisciplina
   │  └─ professor está vinculado à disciplina?
   │  └─ se não → BusinessException
   │
   ├─ ValidarDisponibilidadeProfessor
   │  └─ professor disponível neste dia?
   │  └─ se não → BusinessException
   │
   ├─ ValidarConflitoTurmaHorario
   │  └─ turma já tem aula no dia/horário?
   │  └─ se sim → BusinessException
   │
   ├─ ValidarConflitoSalaHorario
   │  └─ sala já está alocada no dia/horário?
   │  └─ se sim → BusinessException
   │
   ├─ ValidarCapacidadeSala
   │  └─ sala.capacidade >= turma.capacidade?
   │  └─ se não → BusinessException
   │
   ├─ ValidarDuplicidadeAlocacao
   │  └─ alocação idêntica já existe?
   │  └─ se sim → BusinessException
   │
   ├─ ValidarCoerenciaCurso
   │  └─ grade.curso == turma.curso?
   │  └─ se não → BusinessException
   │
   ├─ AlocacaoRepository.save(entity)
   │  └─ INSERT INTO alocacao (...)
   │
   └─ RegistrarHistoricoAlocacao
      └─ INSERT INTO historico_alteracao (...)

5. AlocacaoService.criar() retorna AlocacaoResponse

6. AlocacaoController retorna ResponseEntity
   └─ Status: 201 Created
   └─ Body: AlocacaoResponse (JSON)
```

### 6.2 Fluxo de Atualização

```
Similar ao criar, mas:
  1. Busca alocacao existente por ID
  2. Valida nova alocacao (mesmas 13 validações)
  3. Atualiza campos
  4. Registra histórico com tipo ATUALIZACAO + justificativa
  5. Retorna 200 OK
```

### 6.3 Fluxo de Erro

```
Se qualquer validação falhar:

1. UseCase lança BusinessException("mensagem específica")
   └─ Ex: "Professor João já tem aula no mesmo horário"

2. ResourceExceptionHandler.businessException()
   ├─ Captura exceção
   ├─ Monta resposta StandardError
   │  ├─ status: 409
   │  ├─ error: "Erro de regra de negócio"
   │  ├─ message: "Professor João já tem aula no mesmo horário"
   │  └─ timestamp: instant agora
   └─ Retorna ResponseEntity(status=409, body=StandardError)

3. Cliente recebe:
   {
     "timestamp": "2025-01-15T10:30:00Z",
     "status": 409,
     "error": "Erro de regra de negócio",
     "message": "Professor João já tem aula no mesmo horário",
     "path": "/alocacoes"
   }
```

---

## 7. Arquivos Críticos

### 7.1 Core Domain

```
src/main/java/com/fatec/gini/domain/entities/Alocacao.java
  └─ Entidade JPA com 7 ForeignKeys
  └─ Relacionamentos: Turma, Disciplina, Sala, Usuario, DiaSemana, Horario, GradeHoraria
  └─ OneToMany com HistoricoAlteracao
```

### 7.2 Services

```
src/main/java/com/fatec/gini/domain/services/AlocacaoService.java
  └─ Orquestrador: mapeia DTO → entity → usecase → response

src/main/java/com/fatec/gini/domain/services/usecase/write/CriarAlocacaoUseCase.java
  └─ CRÍTICO: 13 validações encadeadas

src/main/java/com/fatec/gini/domain/services/usecase/write/AtualizarAlocacaoUseCase.java
  └─ Similar ao criar, com histórico de mudança

src/main/java/com/fatec/gini/domain/services/usecase/write/RegistrarHistoricoAlocacaoUseCase.java
  └─ Registra criação/atualização em tabela histórico
```

### 7.3 Validações (Read UseCases)

```
src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarCapacidadeSalaUseCase.java
src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarDisponibilidadeProfessorUseCase.java
src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarConflitoSalaHorarioUseCase.java
src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarConflitoTurmaHorarioUseCase.java
src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarVinculoProfessorDisciplinaUseCase.java
src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarCargaHorariaDisciplinaUseCase.java
src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarCargaHorariaMaximaProfessorUseCase.java
src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarDuplicidadeAlocacaoUseCase.java
src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarGradeHorariaUseCase.java
src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarDisciplinaTipoSalaUseCase.java
src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarCoerenciaUseCase.java
src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarReferenciasObrigatoriasAlocacaoUseCase.java
... (mais validações)
```

### 7.4 Controller e DTOs

```
src/main/java/com/fatec/gini/web/controller/AlocacaoController.java
  └─ 5 endpoints REST

src/main/java/com/fatec/gini/dto/alocacao/AlocacaoRequest.java
  └─ Record com 8 campos (IDs + usuarioAlteracao + justificativa)

src/main/java/com/fatec/gini/dto/alocacao/AlocacaoResponse.java
  └─ Record com entidades mapeadas (não apenas IDs)
```

### 7.5 Repository e Mapper

```
src/main/java/com/fatec/gini/infrastructure/repositories/AlocacaoRepository.java
  └─ Extends JpaRepository<Alocacao, Long>
  └─ Método custom: buscarPorFiltros com @Query

src/main/java/com/fatec/gini/infrastructure/mappers/AlocacaoMapper.java
  └─ toEntity(AlocacaoRequest) - INCOMPLETO (DÉBITO!)
  └─ toResponse(Alocacao)
```

---

## 8. Observações Técnicas

### 8.1 Padrões Aplicados

✅ **Service Layer Pattern**: AlocacaoService orquestra usecases
✅ **UseCase Pattern**: CriarAlocacaoUseCase encapsula lógica complexa
✅ **Repository Pattern**: AlocacaoRepository acesso a dados
✅ **DTO Pattern**: AlocacaoRequest/AlocacaoResponse para API
✅ **Mapper Pattern**: AlocacaoMapper converte entity ↔ DTO
✅ **Transaction Management**: @Transactional em creates/updates
✅ **Exception Handling**: ResourceExceptionHandler mapeia exceções

### 8.2 Boas Práticas Seguidas

✅ Validação em camadas (DTO → Service → UseCase)
✅ Separação clara de responsabilidades
✅ Transacionalidade garantida
✅ Paginação em listagens
✅ CORS habilitado
✅ Anotações para documentação (Swagger pronto)

### 8.3 Débitos Técnicos (Problemas Identificados)

❌ **CRÍTICO - AlocacaoMapper.toEntity() está vazio**: Não copia campos do DTO para entidade, retorna entidade vazia
   - **Impacto**: Entidades sempre criadas sem dados (seria erro em produção)
   - **Ação**: Implementar mapeamento correto

❌ **CRÍTICO - Segurança**: ConfiguracaoSeguranca permite todo acesso (TODO no código)
   - **Impacto**: Qualquer um pode acessar qualquer endpoint
   - **Ação**: Implementar autenticação e RBAC

❌ **MÉDIO - N+1 Queries**: Alocações lazy-loaded, consulta cada professor separadamente
   - **Impacto**: Performance com muitas alocações
   - **Ação**: Considerar EAGER loading ou @Query com JOIN FETCH

❌ **MÉDIO - Validações sequenciais**: 13 validações executadas em ordem sem paralelismo
   - **Impacto**: Tempo de resposta lento com muitas alocações simultâneas
   - **Ação**: Considerar validações paralelas com CompletableFuture

❌ **BAIXO - Soft Delete**: Deletar não marca como removido, apenas apaga
   - **Impacto**: Histórico não rastreia deletions
   - **Ação**: Implementar soft delete com status de alocacao

---

## 9. Testes Existentes

```
src/test/java/com/fatec/gini/domain/services/usecase/write/

├─ CriarAlocacaoUseCaseTest.java
├─ AtualizarAlocacaoUseCaseTest.java
├─ RegistrarHistoricoAlocacaoUseCaseTest.java

src/test/java/com/fatec/gini/domain/services/usecase/read/

├─ ValidarCapacidadeSalaUseCaseTest.java
├─ ValidarDisponibilidadeProfessorUseCaseTest.java
├─ ValidarConflitoSalaHorarioUseCaseTest.java
├─ ValidarConflitoTurmaHorarioUseCaseTest.java
├─ ValidarVinculoProfessorDisciplinaUseCaseTest.java
├─ ValidarDuplicidadeAlocacaoUseCaseTest.java
├─ ValidarGradeHorariaUseCaseTest.java
├─ ValidarCargaHorariaMaximaProfessorUseCaseTest.java
├─ ValidarReferenciasObrigatoriasAlocacaoUseCaseTest.java
```

**Total**: ~9 testes unitários
**Coverage estimado**: ~40% (débito: muitas validações sem testes)

---

## 10. Como Adicionar Nova Validação

Exemplo: Adicionar validação "Professor não pode ter aula depois das 18:00"

### Passo 1: Criar UseCase de Validação

```java
// ValidarHorarioMaximoProfessorUseCase.java
@Service
public class ValidarHorarioMaximoProfessorUseCase {
    @Autowired
    private HorarioRepository horarioRepository;
    
    public void executar(Alocacao entity) {
        Horario horario = entity.getHorario();
        if (horario.getHorarioFim().isAfter(LocalTime.of(18, 0))) {
            throw new BusinessException("Professor não pode ter aula depois das 18:00");
        }
    }
}
```

### Passo 2: Adicionar Injeção em CriarAlocacaoUseCase

```java
@Service
public class CriarAlocacaoUseCase {
    @Autowired
    private ValidarHorarioMaximoProfessorUseCase validarHorarioMaximo;
    
    @Transactional
    public Alocacao executar(Alocacao entity, long usuarioAlteracao) {
        // ... validações existentes ...
        
        // Nova validação
        validarHorarioMaximo.executar(entity);
        
        // ... resto do código ...
    }
}
```

### Passo 3: Adicionar Teste

```java
@Test
public void testHorarioDepoisDas18_DeveLancarException() {
    Alocacao alocacao = new Alocacao();
    alocacao.setHorario(new Horario(LocalTime.of(18, 30), LocalTime.of(19, 30)));
    
    assertThrows(BusinessException.class, () -> {
        validarHorarioMaximoProfessorUseCase.executar(alocacao);
    });
}
```

---

## 11. Checklist de Desenvolvimento

Ao modificar módulo de Alocações:

- [ ] Criou teste unitário para nova funcionalidade
- [ ] Respeitou validações em ordem: DTO → Service → UseCase
- [ ] Adicionou @Transactional em operações de escrita
- [ ] Atualizou histórico quando aplicável
- [ ] Documentou exceção em ResourceExceptionHandler
- [ ] Manteve padrão de nomenclatura
- [ ] Sem N+1 queries (verificou EXPLAIN PLAN)
- [ ] Testou filtros com valores null/edge cases
- [ ] Adicionou comentário no código se lógica é não-óbvia
- [ ] Atualizou este README

---

## 12. Conclusão

O módulo de Alocações é o **coração funcional** do GINI. Com 13+ validações encadeadas e lógica complexa de persistência, é fundamental:

1. **Manter testes cobrindo validações**
2. **Documentar ordem de validações**
3. **Evitar acoplamentos circulares**
4. **Reutilizar usecases em múltiplos fluxos**
5. **Rastrear história de mudanças**

### Próximas Ações:
- ❌ Completar AlocacaoMapper.toEntity()
- ❌ Implementar segurança (RBAC)
- ❌ Adicionar testes de integração
- ❌ Otimizar queries (eliminar N+1)
- ✅ Documentação de API (Swagger)
