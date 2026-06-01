# Módulo: Infraestrutura (Repositories e Mappers)

## 1. Objetivo do Módulo

O módulo de **Infraestrutura** fornece as implementações concretas de **acesso a dados** (Repositories) e **conversão de tipos** (Mappers/Converters), abstraindo a complexidade do banco de dados e facilitando testes.

### Responsabilidades Principais
- 🗄️ **Repositories**: Interface com banco de dados via Spring Data JPA
- 🔄 **Mappers**: Conversão entre entidades JPA e DTOs
- 🔧 **Converters**: Conversão de tipos especializados (enums, datas, etc)

---

## 2. Camada de Repositories

### 2.1 Padrão Repository (Spring Data JPA)

Cada **entidade JPA** possui um **Repository**:

```
src/main/java/com/fatec/gini/infrastructure/repositories/

├─ AlocacaoRepository.java            (Spring Data JPA + custom @Query)
├─ GradeHorariaRepository.java
├─ DisciplinaRepository.java
├─ TurmaRepository.java
├─ UsuarioRepository.java
├─ SalaRepository.java
├─ HorarioRepository.java
├─ DiaSemanaRepository.java
├─ PeriodoLetivoRepository.java
├─ CursoRepository.java
├─ TipoUsuarioRepository.java
├─ TipoSalaRepository.java
├─ RecursoRepository.java
├─ RecursoSalaRepository.java
├─ ProfessorDisciplinaRepository.java
├─ DisponibilidadeProfessorRepository.java
└─ HistoricoAlteracaoRepository.java

Total: 17 repositories
```

### 2.2 Estrutura de um Repository

```java
// AlocacaoRepository.java
@Repository
public interface AlocacaoRepository extends JpaRepository<Alocacao, Long> {
    
    // Métodos herdados do JpaRepository:
    // - findById(Long id)
    // - save(Alocacao entity)
    // - delete(Alocacao entity)
    // - deleteById(Long id)
    // - findAll()
    // - findAll(Pageable pageable)
    // - exists(Example)
    // ... e mais 20+ métodos
    
    // Métodos customizados com Spring Data Query DSL:
    List<Alocacao> findByTurmaId(Long turmaId);
    List<Alocacao> findByUsuarioId(Long usuarioId);
    List<Alocacao> findByGradeHorariaIdAndTurmaId(Long gradeId, Long turmaId);
    
    // Queries customizadas com @Query (JPQL):
    @Query(
        value = "SELECT a FROM Alocacao a " +
                "WHERE (:turmaId IS NULL OR a.turma.id = :turmaId) " +
                "AND (:disciplinaId IS NULL OR a.disciplina.id = :disciplinaId) " +
                "AND (:salaId IS NULL OR a.sala.id = :salaId) " +
                "AND (:usuarioId IS NULL OR a.usuario.id = :usuarioId) " +
                "AND (:diaSemanaId IS NULL OR a.diaSemana.id = :diaSemanaId) " +
                "AND (:horarioId IS NULL OR a.horario.id = :horarioId) " +
                "AND (:gradeId IS NULL OR a.gradeHoraria.id = :gradeId)"
    )
    Page<Alocacao> buscarPorFiltros(
        @Param("turmaId") Long turmaId,
        @Param("disciplinaId") Long disciplinaId,
        @Param("salaId") Long salaId,
        @Param("usuarioId") Long usuarioId,
        @Param("diaSemanaId") Long diaSemanaId,
        @Param("horarioId") Long horarioId,
        @Param("gradeId") Long gradeId,
        Pageable pageable
    );
}
```

### 2.3 Tipos de Consultas

| Tipo | Exemplo | Uso |
|------|---------|-----|
| **Nomeada (Query DSL)** | `findByTurmaId(Long)` | Simples, Spring gera SQL |
| **@Query (JPQL)** | `@Query("SELECT a FROM Alocacao...")` | Complexas, controle total |
| **Custom Method** | `buscarPorFiltros(...)` | Lógica de negócio em BD |
| **Native Query** | `@Query(value="SELECT * FROM", nativeQuery=true)` | Performance crítica |

---

## 3. Repositories Principais

### 3.1 AlocacaoRepository (CRÍTICO)

```java
@Repository
public interface AlocacaoRepository extends JpaRepository<Alocacao, Long> {
    
    // Buscar conflitos de turma
    Optional<Alocacao> findByTurmaAndDiaSemanaAndHorario(
        Turma turma, DiaSemana diaSemana, Horario horario);
    
    // Buscar conflitos de sala
    Optional<Alocacao> findBySalaAndDiaSemanaAndHorario(
        Sala sala, DiaSemana diaSemana, Horario horario);
    
    // Buscar carga horária do professor no dia
    List<Alocacao> findByUsuarioAndDiaSemana(Usuario usuario, DiaSemana diaSemana);
    
    // Buscar alocações de disciplina na grade
    List<Alocacao> findByDisciplinaAndGradeHoraria(Disciplina disciplina, GradeHoraria grade);
    
    // Filtros avançados (paginação)
    @Query(...)
    Page<Alocacao> buscarPorFiltros(...);
    
    // Verificar duplicidade
    boolean existsByTurmaAndDisciplinaAndUsuarioAndDiaSemanaAndHorario(...);
}
```

### 3.2 GradeHorariaRepository

```java
@Repository
public interface GradeHorariaRepository extends JpaRepository<GradeHoraria, Long> {
    
    // Buscar grade de um curso em período específico
    Optional<GradeHoraria> findByCursoAndPeriodoLetivo(Curso curso, PeriodoLetivo periodo);
    
    // Buscar todas as grades de um curso
    List<GradeHoraria> findByCurso(Curso curso);
    
    // Buscar grades em status específico
    List<GradeHoraria> findByStatus(Status status);
}
```

### 3.3 ProfessorDisciplinaRepository

```java
@Repository
public interface ProfessorDisciplinaRepository extends JpaRepository<ProfessorDisciplina, Long> {
    
    // Validar se professor está apto para disciplina
    Optional<ProfessorDisciplina> findByUsuarioAndDisciplina(Usuario usuario, Disciplina disciplina);
    
    // Listar disciplinas de um professor
    List<ProfessorDisciplina> findByUsuario(Usuario usuario);
}
```

### 3.4 DisponibilidadeProfessorRepository

```java
@Repository
public interface DisponibilidadeProfessorRepository extends JpaRepository<DisponibilidadeProfessor, Long> {
    
    // Buscar disponibilidade de professor em dia específico
    Optional<DisponibilidadeProfessor> findByUsuarioAndDiaSemana(Usuario usuario, DiaSemana diaSemana);
    
    // Listar todos os dias disponíveis de um professor
    List<DisponibilidadeProfessor> findByUsuario(Usuario usuario);
}
```

---

## 4. Padrões de Query

### 4.1 Query DSL (Spring Data criará SQL)

```java
// Spring Data entende e traduz para SQL:
List<Alocacao> findByTurmaIdAndDiaSemanaId(Long turmaId, Long diaSemanaId);

// Traduzido para SQL:
// SELECT * FROM alocacao WHERE id_turma = ? AND id_dia_semana = ?
```

### 4.2 @Query (JPQL - Object-Oriented Query Language)

```java
@Query("SELECT a FROM Alocacao a WHERE a.turma.id = :turmaId AND a.diaSemana.id = :diaSemanaId")
List<Alocacao> findAlocacoesDaTurmaEmDia(@Param("turmaId") Long turmaId, 
                                          @Param("diaSemanaId") Long diaSemanaId);

// JPQL trabalha com objetos, não tabelas
// a.turma.id = :turmaId (navegação por relacionamentos)
```

### 4.3 @Query com Filtros Opcionais (CRÍTICO para AlocacaoRepository)

```java
@Query(
    value = "SELECT a FROM Alocacao a " +
            "WHERE (:turmaId IS NULL OR a.turma.id = :turmaId) " +
            "AND (:disciplinaId IS NULL OR a.disciplina.id = :disciplinaId) " +
            "AND (:salaId IS NULL OR a.sala.id = :salaId) " +
            "AND (:usuarioId IS NULL OR a.usuario.id = :usuarioId) " +
            "AND (:diaSemanaId IS NULL OR a.diaSemana.id = :diaSemanaId) " +
            "AND (:horarioId IS NULL OR a.horario.id = :horarioId) " +
            "AND (:gradeId IS NULL OR a.gradeHoraria.id = :gradeId)"
)
Page<Alocacao> buscarPorFiltros(
    @Param("turmaId") Long turmaId,
    @Param("disciplinaId") Long disciplinaId,
    @Param("salaId") Long salaId,
    @Param("usuarioId") Long usuarioId,
    @Param("diaSemanaId") Long diaSemanaId,
    @Param("horarioId") Long horarioId,
    @Param("gradeId") Long gradeId,
    Pageable pageable
);

// Lógica:
// Se turmaId = null, ignora filtro (TRUE OR FALSE = TRUE)
// Se turmaId != null, aplica filtro (FALSE OR (a.turma.id = turmaId))
```

---

## 5. Camada de Mappers

### 5.1 Estrutura de Mapper

```
src/main/java/com/fatec/gini/infrastructure/mappers/

├─ AlocacaoMapper.java
├─ GradeHorariaMapper.java
├─ DisciplinaMapper.java
├─ TurmaMapper.java
├─ UsuarioMapper.java
├─ SalaMapper.java
├─ HorarioMapper.java
├─ DiaSemanaMapper.java
├─ PeriodoLetivoMapper.java
├─ CursoMapper.java
├─ TipoUsuarioMapper.java
├─ TipoSalaMapper.java
├─ RecursoMapper.java
├─ RecursoSalaMapper.java
├─ ProfessorDisciplinaMapper.java
├─ DisponibilidadeProfessorMapper.java
├─ HistoricoAlteracaoMapper.java
└─ StatusConverter.java (converter)

Total: 17 mappers + 1 converter
```

### 5.2 Padrão Mapper (Implementação)

```java
// Exemplo: CursoMapper.java
public class CursoMapper {
    
    // Request → Entity
    public static Curso toEntity(CursoRequest request) {
        if (request == null) return null;
        
        Curso curso = new Curso();
        curso.setNome(request.nome());
        curso.setPeriodicidade(request.periodicidade());
        curso.setStatus(Status.valueOf(request.status()));
        curso.setDuracao(request.duracao());
        return curso;
    }
    
    // Entity → Response
    public static CursoResponse toResponse(Curso entity) {
        if (entity == null) return null;
        
        return new CursoResponse(
            entity.getId(),
            entity.getNome(),
            entity.getPeriodicidade(),
            entity.getStatus().name(),
            entity.getDuracao()
        );
    }
    
    // List → List
    public static List<CursoResponse> toResponseList(List<Curso> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(CursoMapper::toResponse)
                .collect(Collectors.toList());
    }
}
```

### 5.3 Mapper Customizado (com Relacionamentos)

```java
// Exemplo: AlocacaoMapper.java
public class AlocacaoMapper {
    
    // Request → Entity
    public static Alocacao toEntity(AlocacaoRequest request) {
        if (request == null) return null;
        
        // PROBLEMA: Implementação incompleta
        // Deveria buscar as entidades pelo ID:
        
        Alocacao alocacao = new Alocacao();
        // alocacao.setTurma(turmaRepository.findById(request.turma().id()).orElseThrow());
        // alocacao.setDisciplina(disciplinaRepository.findById(request.disciplina().id()).orElseThrow());
        // ... etc
        
        return alocacao;
    }
    
    // Entity → Response
    public static AlocacaoResponse toResponse(Alocacao entity) {
        if (entity == null) return null;
        
        return new AlocacaoResponse(
            entity.getId(),
            TurmaMapper.toResponse(entity.getTurma()),           // Nested DTO
            DisciplinaMapper.toResponse(entity.getDisciplina()), // Nested DTO
            SalaMapper.toResponse(entity.getSala()),             // Nested DTO
            UsuarioMapper.toResponse(entity.getUsuario()),       // Nested DTO
            DiaSemanaMapper.toResponse(entity.getDiaSemana()),   // Nested DTO
            HorarioMapper.toResponse(entity.getHorario()),       // Nested DTO
            GradeHorariaMapper.toResponse(entity.getGradeHoraria()) // Nested DTO
        );
    }
}
```

---

## 6. Converters

### 6.1 StatusConverter (Enum ↔ String)

```java
// StatusConverter.java
@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {
    
    @Override
    public String convertToDatabaseColumn(Status attribute) {
        return (attribute == null) ? null : attribute.name();
    }
    
    @Override
    public Status convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : Status.valueOf(dbData);
    }
}
```

**Uso em Entidade**:
```java
@Entity
public class GradeHoraria {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;  // Automaticamente convertido pelo StatusConverter
}
```

---

## 7. Transações e Persistência

### 7.1 Ciclo de Vida de Entidade

```
1. TRANSIENT
   └─ Objeto Java, sem relação com banco
   └─ Sem ID (null)

2. PERSISTENT (após save)
   └─ Objeto gerenciado pelo EntityManager
   └─ Com ID (gerado)
   └─ Mudanças são rastreadas

3. DETACHED (após fim de transação)
   └─ Objeto ainda existe em memória
   └─ Sem gerenciamento do EntityManager

4. REMOVED (após delete)
   └─ Objeto será deletado
```

### 7.2 Transações Spring Data

```java
@Service
public class AlocacaoService {
    
    @Autowired
    private AlocacaoRepository repository;
    
    // TRANSACTIONAL: cria transação, commit automático
    @Transactional
    public AlocacaoResponse criar(AlocacaoRequest request) {
        Alocacao entity = AlocacaoMapper.toEntity(request);
        Alocacao salva = repository.save(entity); // INSERT
        return AlocacaoMapper.toResponse(salva);
    }
    
    // READ-ONLY: otimização (sem buffer de changes)
    @Transactional(readOnly = true)
    public Page<AlocacaoResponse> listar(Long turmaId, int page, int size) {
        var pageRequest = PageRequest.of(page, size);
        var pageAlocacao = repository.buscarPorFiltros(turmaId, null, null, null, null, null, null, pageRequest);
        return pageAlocacao.map(AlocacaoMapper::toResponse);
    }
    
    // ROLLBACK: se exceção lançada
    @Transactional
    public void criarComValidacao(Alocacao entity) {
        validar(entity); // Se falhar, ROLLBACK
        repository.save(entity); // Não executa se validação falhou
    }
}
```

---

## 8. Arquivos Críticos

### 8.1 Repositories

```
src/main/java/com/fatec/gini/infrastructure/repositories/

├─ AlocacaoRepository.java (CRÍTICO)
│  └─ Queries de filtro + conflitos
│
├─ GradeHorariaRepository.java
│  └─ Busca por curso + período
│
├─ ProfessorDisciplinaRepository.java
│  └─ Validação de vínculo professor-disciplina
│
├─ DisponibilidadeProfessorRepository.java
│  └─ Validação de disponibilidade
│
└─ (14 repositories adicionais)
```

### 8.2 Mappers

```
src/main/java/com/fatec/gini/infrastructure/mappers/

├─ AlocacaoMapper.java (CRÍTICO - INCOMPLETO)
│  └─ toEntity() retorna vazio!
│
├─ (16 mappers adicionais)
│
└─ StatusConverter.java
   └─ Conversão de enum para string
```

---

## 9. Dependências entre Repositories

```
AlocacaoRepository
  ├─ Usa: TurmaRepository (em query)
  ├─ Usa: DisciplinaRepository (em query)
  ├─ Usa: SalaRepository (em query)
  ├─ Usa: UsuarioRepository (em query)
  ├─ Usa: DiaSemanaRepository (em query)
  ├─ Usa: HorarioRepository (em query)
  ├─ Usa: GradeHorariaRepository (em query)
  └─ Usa: HistoricoAlteracaoRepository (para histórico)

CriarAlocacaoUseCase (que usa AlocacaoRepository)
  ├─ Injeta: ValidarReferenciasObrigatoriasAlocacaoUseCase
  │  └─ Que injeta os 7 repositories acima
  ├─ Injeta: ValidarCapacidadeSalaUseCase
  │  └─ Que injeta SalaRepository
  ├─ Injeta: ValidarDisponibilidadeProfessorUseCase
  │  └─ Que injeta DisponibilidadeProfessorRepository
  └─ ... (mais dependências)

// Resultado: MUITAS INJEÇÕES = Risco de ciclo de dependência
```

---

## 10. Riscos Identificados

### 10.1 N+1 Queries (MÉDIO)

```
Problema:
  GET /alocacoes retorna Page<Alocacao>
  ├─ SELECT * FROM alocacao LIMIT 10 (query 1)
  └─ Para cada alocacao.getUsuario()
     └─ SELECT * FROM usuario WHERE id = ? (queries 2-11 = N+1!)

Solução:
  @Query("SELECT a FROM Alocacao a JOIN FETCH a.usuario ...")
  // Eager loading reduz para 1 query
```

### 10.2 Lazy Loading em Contexto Fora de Transação (ALTO)

```
Problema:
  @Transactional(readOnly = true)
  public AlocacaoResponse buscarPorId(Long id) {
      Alocacao entity = repository.findById(id).orElseThrow();
      return AlocacaoMapper.toResponse(entity);
      // Ao chamar entity.getTurma().getNome() → LazyInitializationException
      // Turma não foi carregada, transação terminada
  }

Solução:
  @Query("SELECT a FROM Alocacao a JOIN FETCH a.turma WHERE a.id = :id")
  Optional<Alocacao> findByIdWithTurma(@Param("id") Long id);
```

### 10.3 Ausência de Índices no Banco (MÉDIO)

```
Foreign keys sem índices:
  - id_turma (queries de conflito: SELECT * FROM alocacao WHERE id_turma = ?)
  - id_sala (queries de conflito: SELECT * FROM alocacao WHERE id_sala = ?)
  - id_usuario (queries de carga horária: SELECT * FROM alocacao WHERE id_usuario = ?)
  - id_grade_horaria

Recomendação:
  CREATE INDEX idx_alocacao_turma ON alocacao(id_turma);
  CREATE INDEX idx_alocacao_sala ON alocacao(id_sala);
  CREATE INDEX idx_alocacao_usuario ON alocacao(id_usuario);
  CREATE INDEX idx_alocacao_grade ON alocacao(id_grade_horaria);
```

---

## 11. Boas Práticas

✅ **Sempre usar @Transactional em writes**
✅ **Usar @Transactional(readOnly=true) em reads**
✅ **Preferir Query DSL para queries simples**
✅ **Usar @Query para queries complexas (com documentação)**
✅ **Implementar null-safety em mappers**
✅ **Evitar N+1: usar JOIN FETCH ou @EntityGraph**
✅ **Mappers sem lógica de negócio (apenas transformação)**
✅ **Converters para tipos especiais (Enum, Date, etc)**

---

## 12. Débitos Técnicos

❌ **CRÍTICO**: AlocacaoMapper.toEntity() está vazio
   - Não busca entidades relacionadas
   - Retorna alocacao vazia

❌ **ALTO**: N+1 queries em findAll com relacionamentos
   - Cada alocacao busca seu usuario separadamente

❌ **MÉDIO**: Sem índices no banco de dados
   - Queries de filtro lentas com muitas alocações

❌ **MÉDIO**: Sem paginação em algumas queries
   - Risco de OutOfMemory com muitos registros

---

## 13. Como Adicionar Novo Repository

```java
// 1. Criar novo Repository
@Repository
public interface JustificativaRepository extends JpaRepository<Justificativa, Long> {
    List<Justificativa> findByAlocacao(Alocacao alocacao);
    Optional<Justificativa> findByAlocacaoAndTipo(Alocacao alocacao, String tipo);
}

// 2. Usar em Service
@Service
public class JustificativaService {
    @Autowired
    private JustificativaRepository repository;
    
    public List<JustificativaResponse> listarPorAlocacao(Long alocacaoId) {
        Alocacao alocacao = new Alocacao();
        alocacao.setId(alocacaoId);
        return repository.findByAlocacao(alocacao).stream()
            .map(JustificativaMapper::toResponse)
            .collect(Collectors.toList());
    }
}

// 3. Criar Mapper
public class JustificativaMapper {
    public static JustificativaResponse toResponse(Justificativa entity) {
        return new JustificativaResponse(
            entity.getId(),
            entity.getTipo(),
            entity.getTexto(),
            entity.getDataCriacao()
        );
    }
}
```

---

## 14. Conclusão

A camada de Infraestrutura fornece:
- 🗄️ **17 Repositories**: Acesso aos dados com queries otimizadas
- 🔄 **17 Mappers**: Conversão limpa entre DTOs e entidades
- 🔧 **Converters**: Tipos especializados automáticos

### Próximas Ações:
- ❌ Completar AlocacaoMapper.toEntity()
- ❌ Adicionar índices no banco
- ❌ Eliminar N+1 queries (JOIN FETCH)
- ✅ Transacionalidade implementada
