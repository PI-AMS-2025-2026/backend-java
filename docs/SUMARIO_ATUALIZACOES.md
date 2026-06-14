# 📋 SUMÁRIO DE ATUALIZAÇÕES - Sistema GINI
**Data**: 2026-06-01  
**Baseado em**: Entrevista com Stakeholder sobre funcionamento do sistema

---

## 📚 Documentos Criados/Atualizados

### ✅ **Novos Documentos**

1. **ADR.md** - Decisões Arquiteturais (14 ADRs)
   - Todas as decisões de design confirmadas com stakeholder
   - Justificativas e implicações técnicas
   - Código exemplo para cada decisão

2. **ATUALIZACOES_ENTREVISTA.md** - Referência Completa
   - 14 seções com mudanças e confirmações
   - Checklist de implementação
   - Impacto na arquitetura para cada decisão

3. **Este arquivo (SUMARIO_ATUALIZACOES.md)** - Overview

### 📝 **Documentos Existentes Atualizados**

1. **OBJETIVO_DO_SISTEMA.md**
   - ✅ Seção 4: Atores (apenas ADMIN + COORDENADOR na Fase 1, sem Professor)
   - ✅ Matriz de Permissões: RBAC por curso

2. **ARQUITETURA_DO_SISTEMA.md**
   - ✅ Seção 4.5: Regras de Segurança (RBAC por curso, Spring Security obrigatório)
   - ✅ Seção 7.4: Requisitos Não-Funcionais (adicionado autenticação local, autorização por curso)

---

## 🔴 MUDANÇAS CRÍTICAS CONFIRMADAS

### 1. **STATUS DE GRADE: ATIVO/INATIVO**
- ❌ ANTES: RASCUNHO → VIGENTE → ARQUIVO
- ✅ AGORA: ATIVO ↔ INATIVO
- **Impacto**: Simplifica máquina de estados, remove fluxo formal de aprovação

### 2. **USUÁRIOS: ADMIN + COORDENADOR (RBAC por Curso)**
- ❌ ANTES: ADMIN, COORDENADOR, PROFESSOR, CONSULTOR
- ✅ AGORA: ADMIN (100%) + COORDENADOR (seu curso apenas)
- **Impacto**: Implementar filtro em todas queries, validar propriedade em POST/PUT/DELETE

### 3. **CARGA HORÁRIA: 8h/DIA (não 20h/SEMANA)**
- ❌ ANTES: Máximo 20h/semana
- ✅ AGORA: Máximo 8h por DIA
- **Impacto**: Reescrever lógica de ValidarCargaHorariaMaximaProfessor, agrupar por dia

### 4. **DISPONIBILIDADE PROFESSOR: Lista de Permitidos**
- ❌ ANTES: Indisponibilidades (dias que NÃO pode)
- ✅ AGORA: Lista de horários disponíveis (dias que PODE)
- **Impacto**: Criar entidade DisponibilidadeProfessor, validação inverte lógica

### 5. **SEM INTEGRAÇÃO SIS: 100% Manual**
- ❌ ANTES: Menção vaga a "sistema acadêmico externo"
- ✅ AGORA: Cadastro 100% manual no GINI
- **Impacto**: CRUD completo necessário, sem dependências de APIs externas

### 6. **COMPATIBILIDADE DISCIPLINA-SALA: Via TipoSala**
- ✅ CONFIRMADO: Relacionamento N:1 via TipoSala (já estava ok)
- **Validação**: Disciplina.tipo_sala_id == Sala.tipo_sala_id

### 7. **SEM APROVAÇÃO GUIADA: Apenas Ativo/Inativo**
- ❌ ANTES: Fluxo RASCUNHO → revisão → VIGENTE
- ✅ AGORA: Sem fluxo de revisão formal, apenas inativação manual/automática
- **Impacto**: Remove complexidade, coordenador responsável por revisão

### 8. **AUDITORIA COM MOTIVO OBRIGATÓRIO**
- ✅ CONFIRMADO: Rastrear quem, o quê, quando, **por quê**
- **Impacto**: Campo `motivo` obrigatório para EDIÇÃO/DELEÇÃO de alocações

### 9. **CÓPIA DE GRADE COM LIMPEZA**
- ✅ CONFIRMADO: Remove dados inválidos automaticamente
- **Impacto**: CopiarGradeUseCase valida cada alocação, remove conflitos

### 10. **ENDPOINTS GET PARA VALIDAÇÃO SEM PERSISTÊNCIA**
- ✅ CONFIRMADO: Testar alocação antes de salvar
- **Impacto**: GET /alocacoes/validar com query params, reuse ValidarXXX usecases

---

## 🗂️ ESTRUTURA DE DOCUMENTAÇÃO AGORA

```
docs/
├── OBJETIVO_DO_SISTEMA.md          [ATUALIZADO]
│   └─ Atores: ADMIN + COORDENADOR
│   └─ Fluxos: com GET validação, sem aprovação
│   └─ Regras: 8h/dia, 12h descanso, etc
│
├── ARQUITETURA_DO_SISTEMA.md       [ATUALIZADO]
│   └─ Segurança: RBAC por curso
│   └─ Requisitos Não-Funcionais: autenticação local
│
├── ADR.md                          [NOVO ✨]
│   └─ 14 Architecture Decision Records
│   └─ Justificativas, implicações, código exemplo
│   └─ ADR-001 até ADR-014
│
└── ATUALIZACOES_ENTREVISTA.md      [NOVO ✨]
    └─ Referência completa das mudanças
    └─ 14 seções com detalhes
    └─ Checklist de implementação
    └─ Impacto na arquitetura
```

---

## 🎯 PRIORIDADES DE IMPLEMENTAÇÃO

### 🔴 **CRÍTICA - Implementar Primeiro**
1. **RBAC por Curso** (ADR-002)
   - Filtro em todas as queries
   - Validação em POST/PUT/DELETE
   - Spring Security configuration
   - Afeta: TODA a arquitetura

2. **Carga Horária por DIA** (ADR-003)
   - Reescrever ValidarCargaHorariaMaximaProfessor
   - Agrupar por dia ao invés de semana
   - Testes de cálculo correto

3. **Disponibilidade Semestral** (ADR-004)
   - Entidade DisponibilidadeProfessor
   - Validação inverte lógica
   - CRUD para configurar disponibilidades

4. **Status Simples ATIVO/INATIVO** (ADR-001)
   - Simplificar modelo de estado
   - Remover RASCUNHO/VIGENTE

### 🟠 **ALTA - Implementar em Seguida**
5. **Endpoints GET Validação** (ADR-008)
   - Coordenador testa antes de salvar
   - Reuse ValidarXXX usecases
   - Sem persistência

6. **Auditoria com Motivo** (ADR-006)
   - Campo motivo obrigatório para edição/deleção
   - Endpoint de consulta histórico
   - Filtros por data/usuário/operação

7. **Cópia de Grade com Limpeza** (ADR-007)
   - CopiarGradeUseCase
   - Validação automática de alocações
   - Remove conflitos

8. **Soft/Hard Delete Policy** (ADR-009)
   - Proteção de integridade
   - ValidarCanDeletar usecases
   - Soft delete com flag ativo

### 🟡 **MÉDIA - Implementar Depois**
9. **Exportação PDF/Excel** (ADR-013)
   - Escolher biblioteca (iText, POI)
   - Definir layout
   - Endpoints GET

10. **Compatibilidade TipoSala** (ADR-005)
    - Validação (já estava ok)
    - Apenas garantir constraint no DB

11. **Nomenclatura Turmas** (ADR-010)
    - Pattern validation: `{PERIODO}{CURSO}-{ANO}`
    - Documentação do padrão

12. **Descanso 12 horas** (ADR-011)
    - ValidarDescansoMinimoUseCase
    - Cálculo entre última aula e próxima

13. **Período Semestral** (ADR-012)
    - Já confirmado, apenas validar modelo
    - Cascata com Disponibilidade

14. **Cadastro Manual** (ADR-014)
    - CRUD completo (já deve existir)
    - Sem integração SIS

---

## 📊 IMPACTO EM NÚMEROS

### Arquivos Afetados
- ✅ 2 documentos atualizados (OBJETIVO, ARQUITETURA)
- ✅ 2 documentos criados (ADR, ATUALIZACOES)
- ❓ Múltiplos arquivos de código (usecases, controllers, entities)

### Usecases que Mudam
- ✅ `ValidarCargaHorariaMaximaProfessorUseCase` - mudança crítica
- ✅ `ValidarDisponibilidadeProfessorUseCase` - mudança crítica
- ✅ Criar `CopiarGradeUseCase` - novo
- ✅ Criar `ValidarCanDeletarProfessorUseCase` - novo
- ✅ Criar `ValidarCanDeletarSalaUseCase` - novo
- ✅ Múltiplos GET endpoints para validação - novos

### Controllers que Mudam
- ✅ Todos controllers: adicionar filtro por curso (RBAC)
- ✅ AlocacaoController: adicionar GET /validar
- ✅ GradeHorariaController: adicionar POST /copiar, GET /exportar/pdf, GET /exportar/excel

### Entidades que Mudam
- ✅ `GradeHoraria`: remover RASCUNHO/VIGENTE/ARQUIVO, adicionar ATIVO/INATIVO
- ✅ `Alocacao`: adicionar/validar campo `motivo`
- ✅ `HistoricoAlteracao`: adicionar `motivo` obrigatório
- ✅ Criar `DisponibilidadeProfessor` - nova entidade
- ✅ `Disciplina`: adicionar `tipo_sala_id` (se não tiver)
- ✅ `Sala`: adicionar `tipo_sala_id` (se não tiver)
- ✅ `Professor`: adicionar `ativo` (soft delete)
- ✅ `Sala`: adicionar `ativo` (soft delete)

### Testes Necessários
- ✅ Testes de RBAC (coordenador não acessa outro curso)
- ✅ Testes de carga horária por dia (8h/dia)
- ✅ Testes de disponibilidade semestral
- ✅ Testes de cópia de grade com limpeza
- ✅ Testes de soft delete com proteção
- ✅ Testes de endpoints GET validação
- ✅ Testes de compatibilidade TipoSala

---

## 🔗 COMO USAR ESTES DOCUMENTOS

### Para Desenvolvedores
1. Leia **ADR.md** para entender as decisões
2. Consulte **ATUALIZACOES_ENTREVISTA.md** para implicações técnicas
3. Use **OBJETIVO_DO_SISTEMA.md** para fluxos de negócio
4. Use **ARQUITETURA_DO_SISTEMA.md** para padrões

### Para Product Manager
1. Leia **OBJETIVO_DO_SISTEMA.md** para escopo do MVP
2. Consulte **ATUALIZACOES_ENTREVISTA.md** para o que mudou
3. Use prioridades (crítica/alta/média) para planejamento

### Para Arquiteto
1. Leia **ADR.md** para decisões técnicas
2. Consulte **ATUALIZACOES_ENTREVISTA.md** para impactos
3. Use checklist para validar implementação

### Para QA
1. Leia **ATUALIZACOES_ENTREVISTA.md** checklist de testes
2. Consulte **ADR.md** para detalhes técnicos
3. Crie testes baseados em prioridades

---

## ✅ CHECKLIST FINAL

Após implementação, validar:

### Segurança
- [ ] ADMIN acessa 100% dos dados
- [ ] COORDENADOR acessa apenas seu curso
- [ ] Outras disciplinas retornam 403/404 para COORDENADOR
- [ ] Spring Security com RBAC configurado
- [ ] Senhas com BCrypt

### Dados
- [ ] Status: ATIVO/INATIVO (não RASCUNHO)
- [ ] Disponibilidade: semestral, lista de permitidos
- [ ] Tipo de Sala: disciplina e sala possuem
- [ ] Turmas: padrão `{PERIODO}{CURSO}-{ANO}`
- [ ] Período: semestral com datas

### Validações
- [ ] Carga: 8h/DIA (não semana)
- [ ] Descanso: 12h entre jornadas
- [ ] Compatibilidade: TipoSala match
- [ ] Disponibilidade: professores semestral
- [ ] Proteção: não deletar com alocações ativas

### Endpoints
- [ ] GET /alocacoes/validar (sem persistência)
- [ ] POST /grades/{id}/copiar (com limpeza)
- [ ] GET /grades/{id}/exportar/pdf
- [ ] GET /grades/{id}/exportar/excel
- [ ] GET /alocacoes/{id}/historico (filtros)
- [ ] Todos com RBAC por curso

### Auditoria
- [ ] Motivo obrigatório para EDIÇÃO/DELEÇÃO
- [ ] Histórico consultável via API
- [ ] ADMIN vê auditoria completa
- [ ] COORDENADOR vê histórico seu curso

---

## 🎓 CONCLUSÃO

A entrevista com o stakeholder confirmou e refinou 14 decisões arquiteturais críticas. Os documentos foram atualizados para refletir a visão real do sistema.

**Principais mudanças**:
1. RBAC por curso (não global)
2. Carga horária por dia (não semana)
3. Status simples (ATIVO/INATIVO)
4. Disponibilidade positiva (permissões, não restrições)
5. Sem aprovação formal, com auditoria forte

Próximas ações:
- ✅ Implementar ADRs críticas (1-4)
- ✅ Atualizar modelo de dados
- ✅ Implementar RBAC
- ✅ Testes abrangentes
- ✅ Deploy da Fase 1

---

**Documentos Referência**:
- `docs/ADR.md` - Decisões técnicas detalhadas
- `docs/ATUALIZACOES_ENTREVISTA.md` - Mudanças completas
- `docs/OBJETIVO_DO_SISTEMA.md` - Fluxos de negócio
- `docs/ARQUITETURA_DO_SISTEMA.md` - Padrões arquiteturais

**Última Atualização**: 2026-06-01  
**Próxima Revisão**: Após implementação das ADRs críticas
