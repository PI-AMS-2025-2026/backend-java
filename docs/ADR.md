# ADR: Decisões Arquiteturais - Sistema GINI
**Data**: 2026-06-01  
**Status**: ✅ Confirmado com Stakeholder

---

## ADR-001: Status da Grade Horária (ATIVO/INATIVO)

### Contexto
Inicialmente, a grade tinha múltiplos estados (RASCUNHO, VIGENTE, ARQUIVO) com fluxo de aprovação formal.

### Decisão
✅ Simplificar para apenas **ATIVO** e **INATIVO**:
- **ATIVO**: Grade em uso no semestre/ano corrente
- **INATIVO**: Grade de semestres/anos finalizados ou versões antigas

### Justificativa
- Reduz complexidade do modelo de transição de estado
- Sem necessidade de fluxo formal de aprovação no MVP
- Suporta versionamento natural (múltiplas versões, cada uma ATIVO/INATIVO)
- Coordenador marca como inativo manualmente ou sistema marca automaticamente

### Implicação
- ✅ Simplificar estateMachine
- ✅ Remover lógica de "VIGENTE"
- ✅ Manter histórico mesmo com status simples
- ✅ Versionamento: cada versão é um registro com status próprio

### Alternativas Rejeitadas
❌ Manter RASCUNHO/VIGENTE/ARQUIVO: Adiciona complexidade sem valor imediato

---

## ADR-002: Controle de Acesso por Curso (RBAC)

### Contexto
Instituição tem múltiplos cursos, cada um gerenciado por um coordenador.

### Decisão
✅ Implementar RBAC com filtro por curso:
- **ADMIN**: 100% acesso (todos cursos, todas operações)
- **COORDENADOR**: Acesso limitado apenas ao seu curso

### Justificativa
- Segurança: Coordenador não pode ver/editar cursos alheios
- Simplicidade: Cada coordenador gerencia 1 curso (confirmado em entrevista)
- Escalabilidade: Suporta múltiplos coordenadores/cursos

### Implicação
- ✅ Filtro em toda query: `WHERE grade.curso_id = :usuario.curso_id`
- ✅ Validação em todo POST/PUT/DELETE: verificar propriedade
- ✅ Auditoria respeitando limites (ADMIN vê tudo, COORDENADOR vê seu curso)
- ✅ Endpoint de listagem retorna apenas cursos do usuário
- ✅ Spring Security: criar @PreAuthorize custom

### Código Exemplo
```java
@PreAuthorize("hasRole('ADMIN') or #grade.curso_id == @usuarioService.usuarioAtual().curso_id")
@PutMapping("/{id}")
public GradeHorariaResponse atualizar(@PathVariable Long id, @RequestBody GradeHorariaRequest request) {
    // ...
}
```

---

## ADR-003: Carga Horária Máxima - 8h por DIA (não por semana)

### Contexto
Regras de negócio do Centro Paula Souza estabelecem limite de trabalho docente.

### Decisão
✅ **Máximo 8 horas por DIA** de aula (não por semana)

### Justificativa
- Conforme normas do Centro Paula Souza
- Garante qualidade de vida docente (evita sobrecarga diária)
- Semana pode ter até 40h se distribuído bem

### Implicação
- ✅ `ValidarCargaHorariaMaximaProfessorUseCase`: mudar lógica
- ✅ Agrupar alocações por DIA, não por SEMANA
- ✅ Rejeitar se `sum(duracao_aulas_mesmo_dia) > 8h`
- ✅ Dados corretos: Professor pode ter 8h seg + 8h ter + 8h qua + 8h qui + 8h sex

### Código Exemplo
```java
// ❌ ANTES (errado - por semana):
long horas_semana = alocacoes.stream()
    .map(a -> a.getDuracao())
    .reduce(0L, Long::sum);
if (horas_semana > 20) throw new BusinessException(...);

// ✅ DEPOIS (correto - por dia):
Map<DiaSemana, Long> horas_por_dia = alocacoes.stream()
    .collect(Collectors.groupingBy(
        Alocacao::getDiaSemana,
        Collectors.summingLong(Alocacao::getDuracao)
    ));
horas_por_dia.forEach((dia, horas) -> {
    if (horas > 8) throw new BusinessException(...);
});
```

---

## ADR-004: Disponibilidade de Professores - Lista de Horários Permitidos

### Contexto
Como registrar quando um professor está disponível para aulas?

### Decisão
✅ Professor define **lista de horários DISPONÍVEIS** (não indisponibilidades)
- Formato: (professor_id, dia_semana_id, horario_id, periodo_letivo_id)
- Validade: Semestral (muda cada semestre)

### Justificativa
- Modelo positivo é mais fácil de entender ("Posso dar aula nestes horários")
- Semestral permite ajustes por período
- Sem exceções ad-hoc simplifica lógica

### Implicação
- ✅ Entidade `DisponibilidadeProfessor` com chave composta
- ✅ Cascade delete quando período finaliza
- ✅ Ao copiar grade, validar se professor ainda está disponível no novo período
- ✅ UI: Grade de seleção (dia × horário)
- ✅ `ValidarDisponibilidadeProfessorUseCase`: busca registros

### Modelo de Dados
```sql
CREATE TABLE disponibilidade_professor (
    id BIGINT PRIMARY KEY,
    professor_id BIGINT NOT NULL REFERENCES professor(id),
    dia_semana_id INTEGER NOT NULL REFERENCES dia_semana(id),
    horario_id BIGINT NOT NULL REFERENCES horario(id),
    periodo_letivo_id BIGINT NOT NULL REFERENCES periodo_letivo(id),
    ativo BOOLEAN DEFAULT true,
    UNIQUE(professor_id, dia_semana_id, horario_id, periodo_letivo_id)
);
```

---

## ADR-005: Compatibilidade Disciplina-Sala via TipoSala

### Contexto
Algunas disciplinas (ex: programação) precisam de laboratório, outras (ex: cálculo) funcionam em qualquer sala.

### Decisão
✅ Usar relacionamento via **TipoSala**:
- Disciplina vinculada a 1 TipoSala
- Sala vinculada a 1 TipoSala
- Regra: `disciplina.tipo_sala_id == sala.tipo_sala_id`

### Justificativa
- Flexível: permite múltiplas salas por tipo
- Escalável: adicionar tipo nova é fácil
- Simples: constraint no banco garante integridade

### Implicação
- ✅ Adicionar coluna `tipo_sala_id` em Disciplina e Sala
- ✅ Constraint: `CHECK (sala.tipo_sala_id = disciplina.tipo_sala_id)`
- ✅ ValidarDisciplinaTipoSalaUseCase: simples comparação

### Código Exemplo
```java
@Entity
public class Disciplina {
    @ManyToOne
    @JoinColumn(name = "tipo_sala_id")
    private TipoSala tipoSala; // ex: TipoSala(id=1, nome="Laboratório")
}

@Entity
public class Sala {
    @ManyToOne
    @JoinColumn(name = "tipo_sala_id")
    private TipoSala tipoSala; // ex: TipoSala(id=1, nome="Laboratório")
}

// Validação:
if (!alocacao.getDisciplina().getTipoSala().equals(
        alocacao.getSala().getTipoSala())) {
    throw new BusinessException("Disciplina incompatível com tipo de sala");
}
```

---

## ADR-006: Auditoria Detalhada com Motivo Obrigatório

### Contexto
Stakeholder quer rastrear QUEM fez QUÊ, QUANDO e POR QUÊ.

### Decisão
✅ Registro detalhado para alocações:
- **O quê**: Criação, Edição, Deleção
- **Quem**: Usuário (email/ID)
- **Quando**: Timestamp
- **Por quê**: Motivo OBRIGATÓRIO para edição/deleção

### Justificativa
- Responsabilidade: saber quem alterou e por quê
- Auditoria: justificativa importante para compliance
- Ajustes: coordenador documenta motivo (sala reformada, professor saiu, etc)

### Implicação
- ✅ Campo `motivo` em HistoricoAlteracao (REQUIRED para UPDATE/DELETE)
- ✅ Endpoint GET para consultar histórico com filtros
- ✅ Soft delete com registro em histórico
- ✅ Acesso: ADMIN vê auditoria completa, COORDENADOR vê histórico do seu curso

### Modelo de Dados
```sql
CREATE TABLE historico_alocacao (
    id BIGINT PRIMARY KEY,
    alocacao_id BIGINT NOT NULL REFERENCES alocacao(id) ON DELETE CASCADE,
    tipo_operacao ENUM('CRIACAO', 'EDICAO', 'DELECAO') NOT NULL,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
    data_operacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(500), -- REQUIRED para EDICAO e DELECAO, NULL para CRIACAO
    detalhes_mudanca TEXT -- JSON com campos alterados
);
```

---

## ADR-007: Cópia de Grade com Limpeza Automática

### Contexto
Coordenador copia grade de semestre anterior para nova, mas dados podem estar desatualizados.

### Decisão
✅ Copiar alocações com limpeza automática:
- Remove professores inativados
- Remove salas que não existem mais
- Remove alocações com conflitos potenciais
- Mantém máximo possível de alocações válidas

### Justificativa
- Reduz trabalho manual
- Evita alocações inválidas desde o início
- Preserva 80% das alocações boas, remove 20% problemáticas

### Implicação
- ✅ Criar `CopiarGradeUseCase`
- ✅ Validar cada alocação copiada
- ✅ Marcar como "válida" ou "requer_revisao"
- ✅ Criar nova GradeHoraria v2 com alocações validadas
- ✅ Endpoint: POST /grades/{id}/copiar

### Algoritmo
```
1. Buscar alocações da grade origem
2. Para cada alocação:
   a. Validar se professor ainda existe e está ativo
   b. Validar se sala ainda existe
   c. Validar se turma ainda existe
   d. Re-validar todas as regras de negócio
   e. Se TUDO OK: copiar alocação
   f. Se FALHA: marcar como "requer_revisao" ou pular
3. Criar nova versão (v2) com alocações válidas
4. Retornar estatísticas: X copiadas, Y removidas
```

---

## ADR-008: Validação SEM Persistência (GET Endpoints)

### Contexto
Coordenador quer testar alocação antes de confirmar, mas sem salvar no banco.

### Decisão
✅ Endpoints GET para validação simulada:
- Mesmo fluxo de validação que POST/PUT
- Sem persistência no banco
- Retorna lista de erros (ou sucesso)

### Justificativa
- Feedback imediato
- Reduz cliques para coordenador ("teste antes de enviar")
- Mesmo código de validação, apenas sem persistência

### Implicação
- ✅ Controller: `GET /alocacoes/validar?...params...`
- ✅ Reuse lógica dos ValidarXXXUseCase
- ✅ Sem @Transactional (read-only)
- ✅ Retorna JSON com lista de validações

### Exemplo de Endpoint
```java
@GetMapping("/validar")
public ResponseEntity<ValidacaoAlocacaoResponse> validar(
    @RequestParam Long professorId,
    @RequestParam DiaSemana dia,
    @RequestParam String horarioInicio, // "08:00"
    @RequestParam String horarioFim,     // "10:00"
    @RequestParam Long salaId,
    @RequestParam Long turmaId,
    @RequestParam Long disciplinaId,
    @RequestParam Long gradeId
) {
    // Cria alocacao temporária (não salva)
    Alocacao temp = new Alocacao(
        professorId, dia, horarioInicio, horarioFim,
        salaId, turmaId, disciplinaId, gradeId
    );
    
    // Executa validações
    List<String> erros = new ArrayList<>();
    try {
        validarReferencias.executar(temp);
    } catch (BusinessException e) {
        erros.add(e.getMessage());
    }
    // ... mais validações ...
    
    return ResponseEntity.ok(
        new ValidacaoAlocacaoResponse(
            erros.isEmpty(),
            erros
        )
    );
}

// Response:
{
  "valido": false,
  "erros": [
    "Professor não disponível neste horário",
    "Sala não tem capacidade para turma"
  ]
}
```

---

## ADR-009: Políticas de Deleção e Soft Delete

### Contexto
Stakeholder quer proteger dados em uso, mas permite limpeza de dados desnecessários.

### Decisão
✅ Política de soft/hard delete por tipo de entidade:
- **COM status**: Soft delete (marcar INATIVA)
  - GradeHoraria, Professor, Sala, etc
- **SEM status**: Hard delete
  - Horario, DiaSemana, TipoSala, etc

✅ Proteção de integridade:
- **Professor INATIVADO + tem alocações ATIVAS**: ❌ REJEITAR
- **Professor INATIVADO + grade INATIVA**: ✅ OK
- Mesma regra para Sala

### Justificativa
- Dados com status frequentemente retornam (ex: professor licença)
- Dados estruturais (Horario, DiaSemana) não mudam e podem ser deletados
- Proteção garante integridade (professor usado não é deletado)

### Implicação
- ✅ Adicionar coluna `ativo BOOLEAN DEFAULT true` em Professor, Sala, etc
- ✅ ValidarCanDeletarProfessorUseCase: verificar alocações ativas
- ✅ Soft delete: `UPDATE professor SET ativo = false WHERE id = ?`
- ✅ Hard delete: `DELETE FROM horario WHERE id = ?` (sem proteção)
- ✅ Histórico: registrar motivo da inativação

### Código Exemplo
```java
@Service
public class DeletarProfessorUseCase {
    public void executar(Long professorId) {
        Professor prof = repo.findById(professorId).orElseThrow();
        
        // Verificar se tem alocações em grades ATIVAS
        List<Alocacao> alocacoesAtivas = alocacaoRepo.findByProfessorAndGradeAtivo(
            professorId, true
        );
        
        if (!alocacoesAtivas.isEmpty()) {
            throw new BusinessException(
                "Não pode deletar professor com alocações em grades ativas"
            );
        }
        
        // Soft delete
        prof.setAtivo(false);
        repo.save(prof);
        
        // Registrar auditoria
        historico.save(new HistoricoOperacao(
            TipoOperacao.INATIVACAO,
            "Professor",
            professorId,
            usuarioAtual(),
            "Inativação de professor"
        ));
    }
}
```

---

## ADR-010: Nomenclatura de Turmas com Período

### Contexto
Como identificar turmas de forma consistente?

### Decisão
✅ Padrão de nomenclatura: **{PERIODO}{CURSO}-{ANO}**
- Exemplo: `2ADS-2026` (2º período ADS em 2026)
- Exemplo: `1LOG-2026` (1º período Logística em 2026)

### Justificativa
- Único: identifica período, curso e ano facilmente
- Natural: coordenadores já usam assim
- Parseable: fácil extrair informações (período, curso, ano)

### Implicação
- ✅ Validar pattern em Turma.nome com regex: `^\d+[A-Z]+\d{4}$`
- ✅ Documentação: sempre usar este padrão
- ✅ UI: validar entrada de usuário

### Validação
```java
@Entity
public class Turma {
    @Pattern(regexp = "^\\d+[A-Z]+\\d{4}$", message = "Deve seguir padrão: {PERIODO}{CURSO}-{ANO}")
    private String nome; // ex: 2ADS-2026
}
```

---

## ADR-011: Descanso de 12 Horas entre Jornadas

### Contexto
Legislação garante descanso mínimo entre períodos de trabalho.

### Decisão
✅ **Professor precisa de 12h de descanso** entre última aula de um dia e primeira aula do dia seguinte

### Justificativa
- Qualidade de vida docente
- Conformidade com legislação (Centro Paula Souza)
- Evita fadiga

### Implicação
- ✅ ValidarDescansoMinimoUseCase
- ✅ Lógica: se última aula de seg é 17:00, próxima só pode ser ter após 05:00

### Código Exemplo
```java
LocalDateTime ultimaAula = alocacoes.stream()
    .filter(a -> a.getDiaSemana() < diaNova)
    .map(a -> a.getHorarioFim())
    .max(Comparator.naturalOrder())
    .get(); // 2026-06-01 17:00:00 (segunda)

LocalDateTime proximaAula = diaNova.atTime(horarioInicio); // 2026-06-02 08:00:00 (terça)

long minutos_descanso = ChronoUnit.MINUTES.between(ultimaAula, proximaAula);
if (minutos_descanso < 12 * 60) {
    throw new BusinessException("Professor precisa de 12h descanso entre jornadas");
}
```

---

## ADR-012: Período Letivo Semestral

### Contexto
Como dividir o ano acadêmico?

### Decisão
✅ **Semestral**: 2 períodos por ano
- Período 1: início/02 até final/06
- Período 2: início/08 até final/12
- Disponibilidades de professor: semestral (muda cada semestre)

### Justificativa
- Padrão em instituições brasileiras
- Alinha com calendário acadêmico
- Permite ajustes de disponibilidade professor 2x ao ano

### Implicação
- ✅ Entidade Periodo: (codigo, data_inicio, data_fim)
- ✅ Grade vinculada a 1 Periodo
- ✅ Alocacao vinculada a Periodo (via Grade)
- ✅ DisponibilidadeProfessor: semestral (muda)

---

## ADR-013: Exportação PDF e Excel

### Contexto
Coordenador precisa distribuir grade em papel/email.

### Decisão
✅ Implementar exportação em 2 formatos:
- **PDF**: Para imprimir
- **Excel**: Para enviar/editar

### Justificativa
- PDF: formato universal, preserva formatação
- Excel: facilita reedição se necessário
- Endpoints simples: GET /grades/{id}/exportar/pdf e /exportar/excel

### Implicação
- ✅ Dependências: iText (PDF) ou Apache POI (Excel)
- ✅ Controlador: adicionar endpoints de exportação
- ✅ Formatação: definir layout (colunas, cores, paginação)
- ✅ Performance: cache se necessário

---

## ADR-014: Cadastro Manual (sem integração SIS)

### Contexto
Dados dos alunos, turmas, cursos vêm de qual sistema?

### Decisão
✅ **Cadastro 100% manual no GINI**
- Sem integração com SIS externo na Fase 1
- Dados inseridos pelo ADMIN via UI/API

### Justificativa
- MVP simples: foca em validações de grade
- Flexível: sem dependências de sistemas legados
- Futuro: integrações podem vir depois

### Implicação
- ✅ CRUD completo para todas entidades
- ✅ Endpoints de criação/edição/deleção
- ✅ Seed data para testes
- ✅ Considerar importação batch (CSV/Excel) no futuro

---

## Resumo de Decisões

| ADR | Título | Status |
|-----|--------|--------|
| 001 | Status Grade: ATIVO/INATIVO | ✅ Confirmado |
| 002 | RBAC por Curso | ✅ Confirmado |
| 003 | Carga Horária: 8h/DIA | ✅ Confirmado |
| 004 | Disponibilidade: Lista Permitidos | ✅ Confirmado |
| 005 | Compatibilidade: TipoSala | ✅ Confirmado |
| 006 | Auditoria com Motivo | ✅ Confirmado |
| 007 | Cópia Grade com Limpeza | ✅ Confirmado |
| 008 | Validação GET (sem persistência) | ✅ Confirmado |
| 009 | Soft/Hard Delete | ⚠️ Precisa refino |
| 010 | Nomenclatura Turmas | ✅ Confirmado |
| 011 | Descanso 12 horas | ✅ Confirmado |
| 012 | Período Semestral | ✅ Confirmado |
| 013 | Exportação PDF/Excel | ✅ Confirmado |
| 014 | Cadastro Manual | ✅ Confirmado |

---

**Próxima Revisão**: Após implementação das decisões (ADRs 001-008 críticas)
