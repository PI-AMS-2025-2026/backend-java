# Módulo: DTOs (Data Transfer Objects)

## 1. Objetivo do Módulo

O módulo de **DTOs** é responsável por **desacoplar a API REST da camada de domínio**, garantindo que:
- Dados externos não conhecem estrutura interna de entidades JPA
- Validações de entrada são centralizadas antes de chegarem ao serviço
- Resposta da API é otimizada (apenas campos relevantes)
- Mudanças internas não quebram clientes da API

### Responsabilidade Principal
Fornecer interfaces de contrato entre **Cliente HTTP** ↔ **Backend**, com validação rigorosa de dados de entrada e resposta bem-estruturada.

---

## 2. Estrutura de DTOs

### 2.1 Padrão Utilizado

Cada **domínio funcional** possui seus próprios DTOs em subpacote:

```
src/main/java/com/fatec/gini/dto/
│
├─ alocacao/
│  ├─ AlocacaoRequest.java    (entrada)
│  └─ AlocacaoResponse.java   (saída)
│
├─ gradeHoraria/
│  ├─ GradeHorariaRequest.java
│  └─ GradeHorariaResponse.java
│
├─ disciplina/
│  ├─ DisciplinaRequest.java
│  └─ DisciplinaResponse.java
│
├─ turma/
│  ├─ TurmaRequest.java
│  └─ TurmaResponse.java
│
├─ usuario/
│  ├─ UsuarioRequest.java
│  └─ UsuarioResponse.java
│
├─ sala/
│  ├─ SalaRequest.java
│  └─ SalaResponse.java
│
├─ periodo_letivo/ / professorDisciplina/ / disponibilidadeProfessor/ / ... (14 outros domínios)
│
└─ id/
   └─ LongDTO.java (DTO de suporte para IDs)
```

### 2.2 Tipos de DTO

#### **XxxRequest** (Entrada)
```java
// Exemplo: AlocacaoRequest.java
public record AlocacaoRequest(
    @NotNull(message = "Turma é obrigatória")
    LongDTO turma,
    
    @NotNull(message = "Disciplina é obrigatória")
    LongDTO disciplina,
    
    // ... outros campos
    
    String justificativaAlteracao,
    
    @NotNull(message = "Usuário responsável é obrigatório")
    LongDTO usuarioAlteracao
) {
}
```

**Características**:
- ✅ Contém **apenas** campos que API **aceita** como entrada
- ✅ Usa **Records** Java 16+ (imutabilidade automática)
- ✅ Anotações de validação: `@NotNull`, `@NotEmpty`, `@Size`, etc
- ✅ Mensagens de erro customizadas
- ✅ IDs são `LongDTO` (não Long direto, para clareza)

#### **XxxResponse** (Saída)
```java
// Exemplo: AlocacaoResponse
public record AlocacaoResponse(
    Long id,
    TurmaResponse turma,
    DisciplinaResponse disciplina,
    SalaResponse sala,
    UsuarioResponse usuario,
    DiaSemanaResponse diaSemana,
    HorarioResponse horario,
    GradeHorariaResponse gradeHoraria
) {
}
```

**Características**:
- ✅ Contém **apenas** campos que API **retorna** ao cliente
- ✅ DTOs são **aninhadas** (não apenas IDs)
- ✅ Estrutura espelha entidade JPA sem dados sensíveis
- ✅ Sem validações (já foi validado na entrada)

#### **LongDTO** (Suporte)
```java
// Simples wrapper para IDs
public record LongDTO(
    @NotNull(message = "ID é obrigatório")
    Long id
) {
}
```

---

## 3. Fluxo de Dados

### 3.1 Criação de Alocação

```
1. Cliente HTTP envia:
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

2. Spring Web deserializa → AlocacaoRequest
   └─ @Valid valida anotações:
      ├─ turma.id != null? ✓
      ├─ disciplina.id != null? ✓
      ├─ ... (todos NotNull)
      └─ Se falhar: MethodArgumentNotValidException

3. ResourceExceptionHandler.validationException()
   └─ Resposta 422 Unprocessable Entity:
   {
     "timestamp": "2025-01-15T10:30:00Z",
     "status": 422,
     "error": "Erro de validação",
     "message": "Erro de validação nos campos enviados",
     "errors": [
       "Turma é obrigatória"
     ]
   }

4. Se validação OK → AlocacaoController.criar(request)
   ├─ AlocacaoService.criar(request)
   ├─ AlocacaoMapper.toEntity(request)
   ├─ CriarAlocacaoUseCase.executar(entity, usuarioId)
   ├─ AlocacaoRepository.save(alocacao)
   └─ AlocacaoMapper.toResponse(alocacaoSalva)

5. Spring Web serializa → AlocacaoResponse
   └─ HTTP 201 Created:
   {
     "id": 42,
     "turma": {
       "id": 5,
       "nome": "ADS-2A",
       "capacidade": 40
     },
     "disciplina": {
       "id": 12,
       "nome": "Programação II",
       "cargaHoraria": 4
     },
     ... (etc)
   }
```

### 3.2 Validação em Camadas

```
1. CAMADA DTO (Bean Validation)
   ├─ Tipo de dado (Long vs String)
   ├─ Presença (@NotNull)
   ├─ Tamanho (@Size)
   └─ Formato (@Email, @Pattern)

2. CAMADA SERVICE
   ├─ IDs existem no banco?
   ├─ Dados relacionados consistentes?
   └─ Lógica básica de negócio

3. CAMADA USECASE
   ├─ Validações complexas (13 regras)
   ├─ Cálculos e comparações
   └─ Decisões de persistência
```

---

## 4. DTOs Disponíveis

### 4.1 DTOs por Domínio

| Domínio | Request | Response | Notas |
|---------|---------|----------|-------|
| Alocacao | ✅ AlocacaoRequest | ✅ AlocacaoResponse | Core + justificativa para história |
| GradeHoraria | ✅ GradeHorariaRequest | ✅ GradeHorariaResponse | Versão automática, status gerenciado |
| Disciplina | ✅ DisciplinaRequest | ✅ DisciplinaResponse | Carga horária + tipo de sala |
| Turma | ✅ TurmaRequest | ✅ TurmaResponse | Curso + capacidade |
| Usuario | ✅ UsuarioRequest | ✅ UsuarioResponse | Tipo de usuário, sem senha em response |
| Sala | ✅ SalaRequest | ✅ SalaResponse | Tipo de sala + capacidade |
| Curso | ✅ CursoRequest | ✅ CursoResponse | Periodicidade + status |
| DiaSemana | ✅ DiaSemanaRequest | ✅ DiaSemanaResponse | Simples (segunda, terça, etc) |
| Horario | ✅ HorarioRequest | ✅ HorarioResponse | Hora início + fim |
| PeriodoLetivo | ✅ PeriodoLetivoRequest | ✅ PeriodoLetivoResponse | Data início/fim + status |
| TipoUsuario | ✅ TipoUsuarioRequest | ✅ TipoUsuarioResponse | ADMIN, COORDENADOR, PROFESSOR |
| TipoSala | ✅ TipoSalaRequest | ✅ TipoSalaResponse | Laboratório, Teórica, Auditório |
| Recurso | ✅ RecursoRequest | ✅ RecursoResponse | Tipo de recurso |
| RecursoSala | ✅ RecursoSalaRequest | ✅ RecursoSalaResponse | Quantidade por sala |
| ProfessorDisciplina | ✅ ProfessorDisciplinaRequest | ✅ ProfessorDisciplinaResponse | Vínculo professor-disciplina |
| DisponibilidadeProfessor | ✅ DisponibilidadeProfessorRequest | ✅ DisponibilidadeProfessorResponse | Dias e horários disponíveis |
| HistoricoAlteracao | ❌ Request não necessário | ✅ HistoricoAlteracaoResponse | Apenas leitura (auto-gerado) |
| LongDTO | ✅ LongDTO (suporte) | N/A | Wrapper para IDs |

---

## 5. Dependências de DTOs

### 5.1 Relacionamento entre DTOs

```
AlocacaoResponse depende de:
  ├─ TurmaResponse
  │  └─ CursoResponse
  ├─ DisciplinaResponse
  ├─ SalaResponse
  │  └─ TipoSalaResponse
  ├─ UsuarioResponse
  │  └─ TipoUsuarioResponse
  ├─ DiaSemanaResponse
  ├─ HorarioResponse
  └─ GradeHorariaResponse
     ├─ CursoResponse
     └─ PeriodoLetivoResponse

AlocacaoRequest depende de:
  ├─ LongDTO (turma, disciplina, sala, usuario, diaSemana, horario, gradeHoraria, usuarioAlteracao)
  └─ String (justificativaAlteracao)
```

### 5.2 Mapeamento DTO ↔ Entidade

```
AlocacaoRequest (JSON)
  ↓ @Valid valida
  ↓ AlocacaoController recebe
  ↓ AlocacaoMapper.toEntity(request)
  ↓ Alocacao (JPA entity) ← PROBLEMA: mapper vazio!
  ↓ CriarAlocacaoUseCase.executar(entity)
  ↓ AlocacaoRepository.save(entity)
  ↓ Alocacao (com ID gerado)
  ↓ AlocacaoMapper.toResponse(alocacao)
  ↓ AlocacaoResponse (JSON)
  ↓ Cliente HTTP recebe
```

---

## 6. Convenções de Validação

### 6.1 Anotações Utilizadas

| Anotação | Campo Típico | Mensagem Exemplo |
|----------|------------|-----------------|
| `@NotNull` | ID obrigatório | "Turma é obrigatória" |
| `@NotEmpty` | String não vazia | "Nome não pode ser vazio" |
| `@Size(min, max)` | Tamanho de string/lista | "Nome deve ter entre 3 e 100 caracteres" |
| `@Email` | Email | "Email inválido" |
| `@Pattern` | Formato específico | "Código deve conter apenas letras e números" |
| `@Positive` | Número positivo | "Capacidade deve ser maior que 0" |
| `@Future/@Past` | Data | "Data deve ser no futuro" |

### 6.2 Exemplo de DTO Completo

```java
package com.fatec.gini.dto.alocacao;

import com.fatec.gini.dto.id.LongDTO;
import jakarta.validation.constraints.NotNull;

public record AlocacaoRequest(
    @NotNull(message = "Turma é obrigatória")
    LongDTO turma,
    
    @NotNull(message = "Disciplina é obrigatória")
    LongDTO disciplina,
    
    @NotNull(message = "Sala é obrigatória")
    LongDTO sala,
    
    @NotNull(message = "Usuário é obrigatório")
    LongDTO usuario,
    
    @NotNull(message = "Dia da semana é obrigatório")
    LongDTO diaSemana,
    
    @NotNull(message = "Horário é obrigatório")
    LongDTO horario,
    
    @NotNull(message = "Grade horária é obrigatória")
    LongDTO gradeHoraria,
    
    // Opcional: para atualização
    String justificativaAlteracao,
    
    @NotNull(message = "Usuário responsável pela alteração é obrigatório")
    LongDTO usuarioAlteracao
) {
}
```

---

## 7. Mappers (Conversão DTOs ↔ Entidades)

### 7.1 Padrão Mapper

```java
// AlocacaoMapper.java
public class AlocacaoMapper {
    
    // Request → Entity
    public static Alocacao toEntity(AlocacaoRequest request) {
        if (request == null) return null;
        
        // PROBLEMA: Implementação incompleta!
        // Deveria ser:
        Alocacao alocacao = new Alocacao();
        // Buscar as entidades relacionadas pelos IDs
        // alocacao.setTurma(turmaRepository.findById(request.turma().id()));
        // alocacao.setDisciplina(disciplinaRepository.findById(...));
        // ... etc
        return alocacao;
    }
    
    // Entity → Response
    public static AlocacaoResponse toResponse(Alocacao entity) {
        return new AlocacaoResponse(
            entity.getId(),
            TurmaMapper.toResponse(entity.getTurma()),
            DisciplinaMapper.toResponse(entity.getDisciplina()),
            SalaMapper.toResponse(entity.getSala()),
            UsuarioMapper.toResponse(entity.getUsuario()),
            DiaSemanaMapper.toResponse(entity.getDiaSemana()),
            HorarioMapper.toResponse(entity.getHorario()),
            GradeHorariaMapper.toResponse(entity.getGradeHoraria())
        );
    }
}
```

### 7.2 Arquivos de Mapper

```
src/main/java/com/fatec/gini/infrastructure/mappers/
├─ AlocacaoMapper.java
├─ GradeHorariaMapper.java
├─ DisciplinaMapper.java
├─ TurmaMapper.java
├─ UsuarioMapper.java
├─ SalaMapper.java
... (17 mappers totais)
```

---

## 8. Tratamento de Erros de Validação

### 8.1 Erro de Validação DTO

```json
// Cliente envia JSON com turma = null
{
  "turma": null,
  "disciplina": {"id": 12}
  // ... outro null
}

// Sistema retorna 422:
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 422,
  "error": "Erro de validação",
  "message": "Erro de validação nos campos enviados",
  "errors": [
    "Turma é obrigatória",
    "Sala é obrigatória",
    "Usuário é obrigatório"
  ],
  "path": "/alocacoes"
}
```

### 8.2 Handler de Validação

```java
// ResourceExceptionHandler.java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ValidationError> validationException(
    MethodArgumentNotValidException exception,
    HttpServletRequest request) {
    
    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    ValidationError error = new ValidationError();
    error.setTimeStamp(Instant.now());
    error.setStatus(status.value());
    error.setError("Erro de validação");
    error.setMessage("Erro de validação nos campos enviados");
    error.setPath(request.getRequestURI());
    
    exception.getBindingResult()
        .getFieldErrors()
        .forEach(e -> error.addError(e.getDefaultMessage()));
    
    return ResponseEntity.status(status).body(error);
}
```

---

## 9. Arquivos Críticos

```
src/main/java/com/fatec/gini/dto/
├─ (17 subpacotes com Request/Response)
└─ id/LongDTO.java (suporte)

src/main/java/com/fatec/gini/infrastructure/mappers/
├─ (17 mappers)
└─ AlocacaoMapper.java (CRÍTICO - INCOMPLETO)

src/main/java/com/fatec/gini/web/exception/
├─ ValidationError.java (tratamento de DTOs inválidos)
└─ ResourceExceptionHandler.java (dispatcher)
```

---

## 10. Débitos Técnicos

❌ **CRÍTICO**: `AlocacaoMapper.toEntity()` retorna entidade vazia
   - Não busca entidades relacionadas pelos IDs
   - Não popula nenhum campo
   - Seria erro em produção

❌ **MÉDIO**: Mappers não utilizam builder pattern ou constructor
   - Usar `mapstruct` ou `ModelMapper` considerado para futuro
   - Reduziria boilerplate

❌ **BAIXO**: Sem logging de desserialização JSON
   - Poderia ajudar debugging de erros JSON malformados

---

## 11. Boas Práticas

✅ **Usar Records** para imutabilidade
✅ **Validações descritivas** com mensagens claras
✅ **Separar Request/Response** (não reutilizar)
✅ **DTOs aninhadas** em Response (não apenas IDs)
✅ **LongDTO** ao invés de Long puro (clareza semântica)
✅ **Nenhum campo sensível** em Response (ex: senha)
✅ **Mappers com null-safety** (null checks)

---

## 12. Como Adicionar Novo DTO

### Exemplo: Novo DTO para "CopiarGradeRequest"

```java
// 1. Criar arquivo: src/main/java/com/fatec/gini/dto/gradeHoraria/CopiarGradeRequest.java
package com.fatec.gini.dto.gradeHoraria;

import com.fatec.gini.dto.id.LongDTO;
import jakarta.validation.constraints.NotNull;

public record CopiarGradeRequest(
    @NotNull(message = "Grade origem é obrigatória")
    LongDTO gradeOrigem,
    
    @NotNull(message = "Curso destino é obrigatório")
    LongDTO cursoDestino,
    
    @NotNull(message = "Período letivo destino é obrigatório")
    LongDTO periodoLetivoDestino
) {
}

// 2. Usar em Controller:
@PostMapping("/{id}/copiar")
public ResponseEntity<GradeHorariaResponse> copiar(
    @PathVariable Long id,
    @RequestBody @Valid CopiarGradeRequest request) {
    // ...
}

// 3. Mapear em GradeHorariaMapper se necessário
```

---

## 13. Conclusão

O módulo de DTOs garante **contrato claro entre Cliente e Backend**, com:
- ✅ Validação rigorosa na entrada
- ✅ Estrutura clara de resposta
- ✅ Desacoplamento de entidades internas
- ✅ Mensagens de erro úteis

### Próximas Ações:
- ❌ Completar todos os Mappers (especially toEntity)
- ❌ Considerar MapStruct para otimização
- ✅ Documentação de API (Swagger automático)
