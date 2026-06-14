# Atualizações do Sistema GINI - Baseado em Entrevista com Stakeholder
**Data da Entrevista**: 2026-06-01  
**Status**: Reflete decisões confirmadas que devem atualizar os documentos principais

---

## ⚡ MUDANÇAS CRÍTICAS NA VISÃO DO SISTEMA

### 1. STATUS DA GRADE HORÁRIA

**❌ ANTES**: RASCUNHO → APROVADA → VIGENTE  
**✅ AGORA**: ATIVO / INATIVO

#### Detalhes:
- **ATIVO**: Grade em uso no semestre/ano corrente
- **INATIVO**: Grade de semestres/anos finalizados ou versões antigas
- **Sem fluxo de aprovação guiada**: não há revisão sistemática, apenas inativação manual ou automática
- **Versioning**: Cada grade tem múltiplas versões (v1, v2, v3...), cada uma é um registro separado
- **Implicação**: Simplifica o modelo de transição de estado
- **Impacto na Arquitetura**: 
  - Removar status RASCUNHO, VIGENTE, ARQUIVO
  - Simplificar máquina de estados
  - Usar apenas enum com 2 valores: ATIVO, INATIVO

---

### 2. USUÁRIOS E CONTROLE DE ACESSO

**❌ ANTES**: ADMIN, COORDENADOR, PROFESSOR, CONSULTOR/ANALISTA  
**✅ AGORA**: ADMIN, COORDENADOR (sem Professor na Fase 1)

#### ADMIN
- ✅ Acesso 100% a todos os dados e operações
- Autenticação: Local simples

#### COORDENADOR
- ✅ Acesso limitado apenas ao seu CURSO (RBAC por departamento/curso)
- Cada coordenador gerencia **UM ÚNICO CURSO**
- Pode gerenciar **MÚLTIPLAS TURMAS** desse curso
- Visualiza e modifica apenas grades do seu curso
- Acessa auditoria e histórico apenas do seu curso

#### PROFESSOR
- ❌ **Não é usuário do sistema na Fase 1**
- Futura integração (Fase 2+)
- Apenas acesso de leitura quando implementado

#### Autenticação
- Local simples (sem SSO/LDAP/OAuth por enquanto)
- Usando Spring Security com roles ADMIN e COORDENADOR

**Impacto na Arquitetura**:
- Implementar filtros de autorização por CURSO
- Não permitir COORDENADOR ver/editar cursos alheios
- Toda query deve verificar propriedade do curso
- Auditoria deve respeitar limites de visibilidade

---

### 3. REGRAS DE NEGÓCIO - CARGA HORÁRIA

**❌ ANTES**: "Professor: máx 20h/semana"  
**✅ AGORA**: **8 horas por DIA (não por semana)**

#### Detalhes Confirmados:
- Limite: **8h máximo por DIA** de aula
- Contagem: **Apenas aulas do GINI** (não inclui outras instituições)
- Professor compartilhado: Pode lecionar em múltiplos cursos
- Exemplos válidos:
  - Segunda: 8h de aulas no GINI ✅
  - Terça: 8h de aulas no GINI ✅
  - Quarta: 6h de aulas no GINI ✅
- Exemplos inválidos:
  - Segunda: 8:00-10:00 + 14:00-16:00 + 16:00-18:00 = 6h ✅ OK
  - Segunda: 8:00-10:00 + 10:00-12:00 + 14:00-18:00 = 8h ✅ OK (exatamente limite)
  - Segunda: 8:00-10:00 + 10:00-12:00 + 14:00-18:00 + 18:00-19:00 = 9h ❌ ERRO

**Impacto na Arquitetura**:
- `ValidarCargaHorariaMaximaProfessorUseCase` deve verificar por DIA, não por SEMANA
- Mudar lógica de cálculo de carga horária
- Adicionar constraint no banco de dados

---

### 4. DISPONIBILIDADE DE PROFESSORES

**❌ ANTES**: Indisponibilidades (dias/horários que NÃO pode)  
**✅ AGORA**: Lista de horários DISPONÍVEIS (dias/horários que PODE)

#### Detalhes Confirmados:
- Professor define **lista de horários que ESTÁ DISPONÍVEL**
- Horários: Vinculados a entidade existente (ex: "Horário" com valores fixos como 08:00-10:00)
- Validade: **Semestral** (muda a cada semestre)
- Sem exceções por período (ex: não há "indisponibilidade apenas em março")
- Significado: Se professor marca 08:00-10:00 segunda-feira como disponível, pode ter aulas nesse horário

#### Exemplo:
- Professor João disponível para:
  - Segunda: 08:00-10:00, 14:00-16:00
  - Terça: 14:00-16:00, 16:00-18:00
  - Quarta: (não marcado como disponível)
- Neste caso, João NÃO pode ter aulas na quarta-feira

**Impacto na Arquitetura**:
- Relacionamento: Professor (1:N) Disponibilidade
- Disponibilidade: (professor_id, horario_id, dia_semana, periodo_letivo_id)
- `ValidarDisponibilidadeProfessorUseCase` busca registros de disponibilidade
- Ao copiar grade, manter professor só se ainda está marcado como disponível no novo período

---

### 5. COMPATIBILIDADE DISCIPLINA ↔ SALA

**✅ CONFIRMADO**: Relacionamento via TipoSala

#### Detalhes:
- Disciplina vinculada a **1 TipoSala** (1:1)
- Sala vinculada a **1 TipoSala** (1:1)
- **Regra de validação**: Disciplina só pode ocorrer em Salas do MESMO TipoSala
- Exemplos:
  - Disciplina "Programação II" → TipoSala: Laboratório
    - Pode usar: LAB-01, LAB-02, LAB-03 (todas tipo Laboratório)
    - Não pode usar: A-101 (tipo Sala Comum)
  - Disciplina "Cálculo" → TipoSala: Sala Comum
    - Pode usar: A-101, A-102, A-103 (todas tipo Sala Comum)
    - Não pode usar: LAB-01 (tipo Laboratório)

**Impacto na Arquitetura**:
- `ValidarDisciplinaTipoSalaUseCase` deve comparar tipo_sala de disciplina e sala
- Adicionar constraint no banco: `CHECK (sala.tipo_sala_id = disciplina.tipo_sala_id)`
- Testar em CRUD de alocações

---

### 6. ALOCAÇÃO DE DADOS

**❌ ANTES**: Menção vaga a "sistema acadêmico externo"  
**✅ AGORA**: **Cadastro 100% manual no GINI**

#### Dados Cadastrados Manualmente:
- ✅ Professores (nome, documento, especialidade)
- ✅ Disciplinas (nome, carga horária, tipo de sala)
- ✅ Turmas (nome, quantidade de alunos, período)
- ✅ Salas (nome, capacidade, tipo de sala)
- ✅ Cursos (nome, código)
- ✅ Períodos Letivos (data início, data fim, código ex: "2026/1")
- ✅ Horários (08:00-10:00, 10:00-12:00, etc. - geralmente fixa)
- ✅ Vínculos Professor-Disciplina

#### Não há integração com:
- ❌ SIS (Student Information System)
- ❌ Sistema RH
- ❌ Sistema Predial
- ❌ (Futuro: APIs externas)

**Impacto na Arquitetura**:
- Necessário CRUD completo para TODAS as entidades
- Endpoints de criação/edição/deleção de dados mestres
- Seed data para testes (dados iniciais)
- Considerar importação em batch (CSV/Excel) no futuro

---

### 7. AUDITORIA E HISTÓRICO

**✅ CONFIRMADO**: Rastreamento detalhado

#### Para Alocações (Detalhado):
- **Quem**: Usuário (email/ID)
- **O quê**: Criação / Edição / Deleção
- **Quando**: Timestamp preciso
- **Por quê**: **Motivo OBRIGATÓRIO** para edição/deleção
  - Exemplo: "Sala original reformada em janeiro/2025"
  - Exemplo: "Professor indisponível a partir de março"

#### Para Outras Entidades (Simples):
- Log básico: quem, o quê, quando
- Motivo: opcional

#### Acesso a Auditoria:
- **Auditoria completa**: Apenas ADMIN via API
- **Histórico de alocações**: ADMIN + COORDENADOR (apenas seu curso)

**Impacto na Arquitetura**:
- Entidade `HistoricoAlocacao` (já existe?)
- Adicionar campo `motivo` (required para UPDATE/DELETE)
- Endpoint GET para consultar histórico
- Filtros: por data, por usuário, por tipo de operação
- Soft delete com registro em histórico

---

### 8. CÓPIA DE GRADE COM LIMPEZA AUTOMÁTICA

**✅ CONFIRMADO**: Remove dados inválidos

#### Fluxo:
1. Coordenador seleciona grade anterior (ex: ADS 2025-1)
2. Sistema copia todas as alocações
3. **Limpeza automática**:
   - ❌ Remove profesores que foram inativados
   - ❌ Remove salas que não existem mais
   - ❌ Remove alocações com conflitos potenciais
   - ✅ Mantém máximo de alocações válidas possível
4. Resultado: Nova versão com menos erros

#### Exemplos de Limpeza:
- Professor X saiu da instituição → Remove alocações dele
- Sala Y foi demolida → Remove alocações nela
- Turma Z mudou de período → Valida se ainda é válida
- Horário H não existe mais → Remove alocações com esse horário

**Impacto na Arquitetura**:
- Criar `CopiarGradeUseCase`
- Iterar sobre alocações copiadas e validar cada uma
- Marcar como "válida" ou "inválida" durante cópia
- Opção: Copiar apenas válidas ou copiar todas com flag "requer_revisao"
- Endpoint: POST /grades/{id}/copiar

---

### 9. VALIDAÇÃO SEM SALVAR (GET Endpoints)

**✅ CONFIRMADO**: Endpoints GET para simular validação

#### Objetivo:
- Coordenador testa uma alocação ANTES de confirmar
- Retorna mesmos erros de validação mas **sem persistir**
- Sem modificar banco de dados

#### Exemplo de Uso:
```
GET /alocacoes/validar
  ?professorId=1
  &dia=SEGUNDA
  &horarioInicio=08:00
  &horarioFim=10:00
  &salaId=5
  &turmaId=3
  &disciplinaId=2

Response:
{
  "valido": false,
  "erros": [
    "Professor não disponível neste horário",
    "Sala não tem capacidade para turma"
  ]
}
```

#### Funciona para:
- ✅ Validação de disponibilidade de professor
- ✅ Validação de capacidade de sala
- ✅ Validação de compatibilidade disciplina-sala
- ✅ Todos os ValidarXXX UseCases

**Impacto na Arquitetura**:
- Criar controller endpoints GET para cada validação principal
- Reuse lógica dos ValidarXXXUseCase
- Sem @Transactional (leitura)
- Sem persistência
- Documentar bem no Swagger

---

### 10. TURMAS - NOMENCLATURA E CONCEITO

**✅ CONFIRMADO**: Padrão específico

#### Nomenclatura:
- Formato: `{PERIODO}{CURSO}-{ANO}`
- Exemplos:
  - `2ADS-2026` (2º período ADS em 2026)
  - `1ADS-2026` (1º período ADS em 2026)
  - `4LOG-2026` (4º período Logística em 2026)

#### Conceito:
- Turma = Grupo de alunos que estudam junto num período
- Um coordenador gerencia **múltiplas turmas** de seu curso
- Uma turma em **uma sala por horário** (não pode estar em 2 salas simultaneamente)

#### Alunos:
- Quantidade cadastrada manualmente
- Usada para validar capacidade de sala

**Impacto na Arquitetura**:
- Validação: Turma.capacidade <= Sala.capacidade
- Sem relacionamento direto com alunos individuais (agregado por quantidade)
- Formato de nome em DTO (validar pattern)

---

### 11. PERÍODO LETIVO E CALENDÁRIO

**✅ CONFIRMADO**: Semestre fixo

#### Estrutura:
- Período: Código + Data início + Data fim
- Granularidade: **Semestral** (2 por ano)
- Exemplo: "2026/1" (01/03/2026 até 30/06/2026)
- Grade vinculada a período
- Disponibilidades de professores: Semestral (muda a cada semestre)

#### Horários Padrão:
- Instituição define horários padrão (geralmente fixa)
- Exemplo: 08:00-10:00, 10:00-12:00, 14:00-16:00, 16:00-18:00
- Horários são reutilizados em cada alocação

**Impacto na Arquitetura**:
- Entidade `Periodo` com (codigo, data_inicio, data_fim)
- Entidade `Horario` (hora_inicio, hora_fim, descricao)
- Entidade `DiaSemana` (SEGUNDA, TERÇA, etc)
- Alocacao: (periodo_id, dia_semana_id, horario_id)

---

### 12. EXPORTAÇÃO PDF/EXCEL

**✅ CONFIRMADO**: Endpoints de exportação

#### Funcionalidade:
- Exportar grade em PDF e Excel
- Formato visualmente apresentável
- Pronto para imprimir/distribuir

#### Endpoints:
```
GET /grades/{id}/exportar/pdf
GET /grades/{id}/exportar/excel
```

**Impacto na Arquitetura**:
- Escolher biblioteca: iText (PDF) + Apache POI (Excel)
- Criar formatadores/geradores
- Adicionar ao controlador
- Definir layout (que colunas? Como organizar?)

---

### 13. DELEÇÃO E INATIVAÇÃO - PRECISA PLANEJAMENTO

**⚠️ COMPLEXO**: Política ainda precisa ser refinada na prática

#### Política Geral Proposta:
- **Entidades COM status**: Soft delete (marcar INATIVA)
  - Exemplo: GradeHoraria, Professor, Sala
- **Entidades SEM status**: Hard delete
  - Exemplo: Horario, DiaSemana, TipoSala

#### Proteção de Integridade:
- **Se Professor é INATIVADO e tem alocações em grade ATIVA**: ❌ EXCEÇÃO (rejeitar deleção)
- **Se Professor é INATIVADO e grade é INATIVA**: ✅ OK (permitir)
- **Mesmo comportamento para Salas**

#### O Que Ainda Precisa Ser Definido:
- ❓ Hard delete de tabelas auxiliares (Horario, DiaSemana)
- ❓ Cascade delete: Deletar Professor → deletar alocações?
- ❓ Soft delete com flag "inativo" ou coluna "data_exclusao"?
- ❓ Como manter histórico de entidades deletadas?
- ❓ Proteção contra deletar dados em uso

**Impacto na Arquitetura**:
- Criar ADR (Architecture Decision Record) especificando política
- Adicionar flag "ativo" em entidades apropriadas
- Criar ValidarCanDeletarAlocacaoUseCase
- Testar cascatas no banco
- Documentar bem as regras

---

### 14. ERROS E TRATAMENTO DE VALIDAÇÃO

**✅ CONFIRMADO**: Simples e claro

#### Nível de Detalhe:
- Mensagens **simples e claras**, não detalhadas
- Exemplo: ✅ "Professor não disponível neste horário"
- Não: ❌ "Professor ID 42 não tem disponibilidade para segunda-feira 08:00-10:00 porque disponibilidades dele são terça 14:00-16:00 e quarta 16:00-18:00"

#### Sem Sugestões:
- ❌ Não retornar alternativas (ex: "tente segunda às 14h")
- ❌ Não listar todos os conflitos (apenas primeiro/principal)

#### Status HTTP:
- 409 Conflict para violação de regra de negócio
- 400 Bad Request para validação de DTO
- 404 Not Found para recurso inexistente

**Impacto na Arquitetura**:
- ResourceExceptionHandler: Mapear exceções para status corretos
- Mensagens de erro simples e não-técnicas
- Sem revelação de detalhes do sistema

---

## 📋 CHECKLIST DE IMPLEMENTAÇÃO

Com base nessas confirmações, os seguintes pontos devem ser implementados/atualizados:

### Segurança (CRÍTICA)
- [ ] Implementar RBAC: ADMIN (100%) e COORDENADOR (por curso)
- [ ] Filtrar queries por curso para COORDENADOR
- [ ] Validar propriedade de curso em todo endpoint de escrita
- [ ] Spring Security: roles, authentication, authorization

### Modelo de Dados
- [ ] Mudar status de grade: RASCUNHO/VIGENTE/ARQUIVO → ATIVO/INATIVO
- [ ] Adicionar campo `motivo` em alocações (edição/deleção)
- [ ] Disciplina ↔ TipoSala (validar constraint)
- [ ] Professor → Disponibilidade (por dia/horário/período)
- [ ] Padrão de nomenclatura de turmas

### Validações
- [ ] Carga horária: mudar para 8h/DIA (não semana)
- [ ] Compatibilidade disciplina-sala: via TipoSala
- [ ] Disponibilidade professor: usar lista de horários permitidos
- [ ] Descanso 12h entre jornadas
- [ ] Proteção de deleção: professor/sala com alocações ativas

### Endpoints
- [ ] GET /alocacoes/validar (sem salvar)
- [ ] POST /grades/{id}/copiar (com limpeza automática)
- [ ] GET /grades/{id}/exportar/pdf
- [ ] GET /grades/{id}/exportar/excel
- [ ] GET /alocacoes/{id}/historico (com filtros)
- [ ] CRUD completo para dados mestres

### Auditoria
- [ ] Registrar motivo para edição/deleção de alocação
- [ ] Auditoria acessível apenas para ADMIN (completa) e COORDENADOR (seu curso)
- [ ] Histórico de alocações com filtros

### Testes
- [ ] Testes de RBAC (coordenador não pode ver outro curso)
- [ ] Testes de carga horária por dia
- [ ] Testes de disponibilidade semestral
- [ ] Testes de cópia de grade com limpeza
- [ ] Testes de validação sem persistência

---

## 🔗 REFERÊNCIAS CRUZADAS

Este documento deve atualizar as seguintes seções dos docs principais:

1. **OBJETIVO_DO_SISTEMA.md**:
   - Seção 4: Status da grade (ATIVO/INATIVO)
   - Seção 4: Atores (apenas ADMIN e COORDENADOR na Fase 1)
   - Seção 3: Fluxo primário (incluir GET validação, remover aprovação, adicionar motivo)
   - Seção 3: Fluxo de cópia (incluir limpeza automática)

2. **ARQUITETURA_DO_SISTEMA.md**:
   - Seção 8.1: Riscos (add: política de deleção incompleta)
   - Seção 9: Diretrizes (add: RBAC, ValidarCanDeletarUseCase)
   - Regras de validação (carga horária por dia, disponibilidade)

3. **Novos Documentos Recomendados**:
   - ADR: Política de Deleção e Soft Delete
   - ADR: RBAC e Filtragem por Curso
   - Especificação de API: Endpoints GET de validação
   - Especificação de API: Exportação PDF/Excel

---

**Última Atualização**: 2026-06-01  
**Próxima Revisão**: Após implementação inicial
