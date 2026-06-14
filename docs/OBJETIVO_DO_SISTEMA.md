# Objetivo do Sistema GINI

## 1. Propósito Principal

O sistema **GINI** (Grade Horária Inteligente) é uma solução backend de gerenciamento acadêmico desenvolvida para **automatizar, validar e otimizar a criação e gestão de grades horárias** em instituições de ensino superior.

Seu propósito central é:
> **Reduzir drasticamente o tempo e esforço manual de coordenadores acadêmicos na montagem de grades horárias, eliminando conflitos, validando automaticamente regras de negócio complexas e garantindo a consistência das alocações de aulas.**

### Valor Entregue
- ✅ **Automatização**: validações complexas executadas em segundos
- ✅ **Confiabilidade**: eliminação de erros humanos em conflitos
- ✅ **Velocidade**: criação de grades em minutos ao invés de horas
- ✅ **Rastreabilidade**: histórico completo de todas as operações
- ✅ **Escalabilidade**: gerenciamento de múltiplos cursos simultaneamente

---

## 2. Problemas que o Sistema Resolve

### 2.1 Problemas Operacionais

| Problema | Antes (Manual) | Depois (GINI) |
|----------|----------------|---------------|
| **Conflito de horários de professor** | Detectado após montagem (retrabalhamento) | Validado em tempo real durante entrada |
| **Conflito de horários de sala** | Detectado após montagem | Validado em tempo real |
| **Sobrecarga de turma em sala** | Descoberto no dia da aula | Validado antes de salvar alocação |
| **Professor não apto para disciplina** | Detectado por coordenador experiente | Validado automaticamente |
| **Disciplina incompatível com tipo de sala** | Alocação inválida no semestre | Prevenido antes da persistência |
| **Descanso insuficiente entre jornadas** | Conflito de fadiga docente | Garantido com regra de 12h |
| **Carga horária máxima superada** | Excesso de trabalho para professor | Limitado pelo sistema |

### 2.2 Problemas de Negócio

1. **Indisponibilidade de coordenador**: quando o responsável está em férias ou licença, ninguém consegue montar grades
   - **Solução**: qualquer usuário com acesso pode usar o sistema

2. **Retrabalho frequente**: erros descobertos tarde resultam em remontagem completa
   - **Solução**: validação imediata garante primeira versão confiável

3. **Falta de histórico**: impossível rastrear quem fez mudança e por quê
   - **Solução**: registro de todas as operações com usuário, timestamp e justificativa

4. **Reutilização de grades**: impossível copiar estrutura de semestre anterior
   - **Solução**: funcionalidade de cópia com validação automática

5. **Falta de controle de versão**: só existe "última" versão da grade
   - **Solução**: versionamento de grades por período letivo

---

## 3. Principais Fluxos de Negócio

### 3.1 Fluxo Primário: Montagem de Grade Horária

```
┌──────────────────────────────────────────────────────────────────────┐
│ ATOR: Coordenador Acadêmico ou Administrador                        │
└──────────────────────────────────────────────────────────────────────┘

1. PREPARAÇÃO (Pré-requisitos cadastrados)
   ├─ Cursos registrados no sistema
   ├─ Disciplinas de cada curso cadastradas
   ├─ Turmas do semestre criadas
   ├─ Professores e suas disciplinas vinculados
   ├─ Disponibilidades de professores registradas
   ├─ Salas e seus tipos/capacidades cadastradas
   └─ Horários padrão da instituição configurados

2. CRIAÇÃO DA GRADE HORÁRIA
   ├─ Criar grade para: Curso X + Período Letivo Y
   ├─ Status inicial: RASCUNHO
   └─ Sistema cria versão 1

3. ALOCAÇÃO DE AULAS (Iterativo)
   └─ Coordenador insere uma alocação:
       ├─ Turma: ADS-2A
       ├─ Disciplina: Programação II
       ├─ Professor: João da Silva
       ├─ Sala: LAB-01
       ├─ Dia: Segunda-feira
       ├─ Horário: 08:00-10:00
       └─ Sistema VALIDA imediatamente:
           ├─ Professor está disponível neste dia/horário?
           ├─ Sala está disponível neste dia/horário?
           ├─ Professor não ultrapassará carga horária máxima?
           ├─ Turma não tem conflito neste dia/horário?
           ├─ Sala tem capacidade para turma?
           ├─ Disciplina é compatível com tipo de sala?
           └─ Se TUDO OK → salva com sucesso (201)
           └─ Se algo FALHA → retorna erro detalhado (409)

4. ITERAÇÃO
   ├─ Coordenador visualiza grade parcial
   ├─ Continua adicionando alocações
   └─ Cada alocação é validada independentemente

5. REVISÃO E AJUSTES
   ├─ Coordenador consulta grade completa
   ├─ Se necessário, edita alocações (com justificativa)
   ├─ Ou deleta alocações problemáticas
   └─ Sistema mantém histórico de todas as mudanças

6. APROVAÇÃO
   ├─ Coordenador marca grade como APROVADA
   ├─ Status muda para VIGENTE
   └─ Grade está pronta para implementação

7. COMUNICAÇÃO (Fora do sistema)
   ├─ Grades impressas/exportadas para salas
   ├─ Notificações enviadas para professores
   └─ Turmas informadas de suas alocações
```

### 3.2 Fluxo Secundário: Cópia de Grade de Período Anterior

```
PRECONDIÇÃO: Existe grade vigente de período anterior (ex: 2024-1)

1. Coordenador acessa funcionalidade "Copiar Grade"
2. Seleciona:
   ├─ Grade origem: ADS 2024-1 (VIGENTE)
   └─ Curso destino: ADS 2025-1 (RASCUNHO)

3. Sistema:
   ├─ Copia todas as alocações da grade origem
   ├─ VALIDA cada alocação copiada:
   │  ├─ Professor ainda vinculado à disciplina?
   │  ├─ Sala ainda existe?
   │  ├─ Turma ainda existe?
   │  └─ Se alguma validação FALHA:
   │     └─ Marca alocação como "PENDENTE REVISÃO"
   │
   └─ Cria grade versão 1 com alocações herdadas

4. Coordenador revisa alocações pendentes:
   ├─ Remove se professor já não está
   ├─ Substitui sala se não existe
   ├─ Adiciona novas alocações
   └─ Salva como de costume

5. Grade copiada pronta para ajustes manuais
```

### 3.3 Fluxo de Atualização de Alocação (com Histórico)

```
PRECONDIÇÃO: Existe alocação já salva

1. Coordenador acessa alocação existente
2. Clica em "Editar"
3. Modifica campos (ex: troca sala)
4. Fornece JUSTIFICATIVA DA MUDANÇA:
   └─ Ex: "Sala original reformada em jan/2025"

5. Sistema:
   ├─ Valida nova alocação (mesmos critérios)
   ├─ Se OK: salva com status ATUALIZADA
   ├─ Registra histórico com:
   │  ├─ Tipo: ATUALIZAÇÃO
   │  ├─ Usuário: joao@fatec.edu.br
   │  ├─ Data/Hora: 2025-01-15 10:30
   │  ├─ Justificativa: "Sala original reformada em jan/2025"
   │  └─ Campos alterados: [sala_anterior, sala_nova]
   └─ Retorna 200 OK

6. Coordenador pode consultar histórico completo:
   └─ "Esta alocação foi alterada 3 vezes"
   └─ Ver cada mudança com justificativa
```

### 3.4 Fluxo de Consulta e Filtros

```
1. Coordenador acessa "Visualizar Alocações"
2. Pode filtrar por:
   ├─ Turma (ex: ADS-2A)
   ├─ Professor (ex: João)
   ├─ Sala (ex: LAB-01)
   ├─ Dia da Semana (ex: segunda-feira)
   ├─ Horário (ex: 08:00-10:00)
   ├─ Período Letivo (ex: 2025-1)
   └─ Combinações dos anteriores

3. Sistema retorna lista paginada:
   ├─ 10 alocações por página (customizável)
   ├─ Cada alocação mostra: turma, disciplina, professor, sala, dia, horário
   └─ Links para editar/deletar/ver histórico

4. Coordenador pode exportar/imprimir resultado
```

---

## 4. Atores do Sistema

### 4.1 Usuários Principais (MVP - Fase 1)

#### 👤 **ADMIN** (Administrador Acadêmico)
- **Descrição**: Gerencia dados cadastrais, configurações e auditoria
- **Responsabilidades**:
  - Cadastrar cursos, disciplinas, turmas, professores, salas
  - Registrar vínculos professor-disciplina
  - Configurar períodos letivos e horários padrão
  - Atualizar disponibilidades de professores
  - Criar/gerenciar usuários COORDENADOR
  - Acessar auditoria completa do sistema
- **Acesso**: ✅ **100% - todas as operações e dados**
- **Frequência de uso**: Conforme necessidade (semanal)
- **Filtros de Dados**: Nenhum (acesso global)

#### 👤 **COORDENADOR** (Coordenador Acadêmico)
- **Descrição**: Responsável pela montagem e gestão da grade horária do seu curso
- **Responsabilidades**:
  - Criar nova grade para semestre (do seu curso apenas)
  - Adicionar/editar/remover alocações
  - Resolver conflitos identificados pelo sistema
  - Consultar histórico de mudanças
  - Copiar grades de períodos anteriores
  - Exportar/imprimir grades (PDF/Excel)
  - Validar alocações antes de salvar (endpoints GET)
- **Acesso**: ✅ **Limitado ao seu curso** (RBAC por departamento)
- **Frequência de uso**: Diária durante período de montagem (2-3 semanas)
- **Filtros de Dados**: Apenas cursos que gerencia
- **Auditoria**: Visualiza histórico do próprio curso

#### 👤 Professor (Futuro - Fase 2+)
- **Descrição**: Visualiza sua própria alocação
- **Responsabilidades**:
  - Consultar seus horários
  - Informar indisponibilidades (futura integração)
- **Status Atual**: ❌ Não é usuário do sistema na Fase 1
- **Acesso**: N/A
- **Privilégios**: N/A

---

### 4.2 Matriz de Permissões (RBAC)

| Operação | ADMIN | COORDENADOR |
|----------|:-----:|:-----------:|
| **Cadastros** | | |
| - Ver cursos | ✅ | ✅ seu curso |
| - Criar/editar cursos | ✅ | ❌ |
| - Ver disciplinas | ✅ | ✅ seu curso |
| - Criar/editar disciplinas | ✅ | ❌ |
| - Ver turmas | ✅ | ✅ seu curso |
| - Criar/editar turmas | ✅ | ❌ |
| - Ver professores | ✅ | ✅ |
| - Criar/editar professores | ✅ | ❌ |
| - Ver salas | ✅ | ✅ |
| - Criar/editar salas | ✅ | ❌ |
| **Grades** | | |
| - Criar grade | ✅ | ✅ seu curso |
| - Copiar grade | ✅ | ✅ seu curso |
| - Visualizar grade | ✅ | ✅ seu curso |
| **Alocações** | | |
| - Criar alocação | ✅ | ✅ seu curso |
| - Editar alocação | ✅ | ✅ seu curso |
| - Deletar alocação | ✅ | ✅ seu curso |
| - Validar (GET) | ✅ | ✅ seu curso |
| **Auditoria** | | |
| - Ver auditoria completa | ✅ | ❌ |
| - Ver histórico alocações | ✅ | ✅ seu curso |
| **Exportação** | | |
| - Exportar PDF/Excel | ✅ | ✅ seu curso |

---

## 5. Funcionalidades Centrais

### 5.1 Funcionalidades Implementadas

#### 1️⃣ Gerenciamento de Cadastros Acadêmicos
```
├─ Usuários e Tipos de Usuário
├─ Cursos
├─ Disciplinas com carga horária e tipo de sala
├─ Turmas com capacidade
├─ Vínculos Professor x Disciplina
└─ Períodos Letivos (ativo/inativo)
```

#### 2️⃣ Gerenciamento de Infraestrutura
```
├─ Salas com capacidade
├─ Tipos de Sala (laboratório, teórica, auditório)
├─ Recursos (projetor, computador, etc)
├─ Alocação de Recursos por Sala
└─ Disponibilidade de Professores (dias/horários)
```

#### 3️⃣ Gerenciamento de Grades Horárias
```
├─ Criar nova grade por (Curso + Período Letivo)
├─ Versionamento automático (v1, v2, v3...)
├─ Status (RASCUNHO, VIGENTE)
├─ Cópia de grades anteriores
└─ Visualização com paginação
```

#### 4️⃣ Gerenciamento de Alocações (Núcleo)
```
├─ Criar alocação (turma + disciplina + sala + professor + horário)
├─ Editar alocação com justificativa
├─ Deletar alocação
├─ Listar com filtros avançados
└─ Visualizar detalhes
```

#### 5️⃣ Validações Automáticas de Alocação
```
├─ Validação de referências obrigatórias
├─ Compatibilidade disciplina ↔ tipo de sala
├─ Carga horária máxima de disciplina
├─ Carga horária máxima diária de professor
├─ Disponibilidade do professor (dia + horário)
├─ Conflito de horário de professor
├─ Conflito de horário de sala
├─ Conflito de horário de turma
├─ Capacidade física da sala
├─ Vínculo professor apto para disciplina
├─ Duplicidade de alocação
├─ Grade ativa
└─ Coerência entre cursos
```

#### 6️⃣ Rastreamento com Histórico
```
├─ Registro automático de CRIAÇÃO de alocação
├─ Registro de ATUALIZAÇÃO com justificativa
├─ Registro de DELEÇÃO com motivo
├─ Visualização de histórico por alocação
├─ Filtros por data, usuário, tipo de mudança
└─ Auditoria completa das operações
```

#### 7️⃣ Documentação API (Swagger)
```
├─ Auto-geração do OpenAPI 3.0
├─ Visualização em http://localhost:8080/swagger-ui.html
├─ Teste de endpoints direto do navegador
└─ Download de especificação em JSON/YAML
```

---

## 6. Visão de Produto

### 6.1 Visão Atual (MVP - Minimum Viable Product)
O GINI entrega hoje:
- ✅ Cadastros acadêmicos e de infraestrutura
- ✅ Criação/edição de grades horárias
- ✅ Validações automáticas de conflitos
- ✅ Rastreamento de histórico
- ✅ API REST documentada

### 6.2 Visão Futura (Roadmap)

#### **Fase 1 (Próximo semestre)**
- [ ] Autenticação e autorização por perfil (atualmente TODO)
- [ ] Exportação de grades em PDF/Excel
- [ ] Interface web frontend
- [ ] Notificações de grade aprovada
- [ ] Relatórios de ocupação de salas

#### **Fase 2 (2 semestres à frente)**
- [ ] Integração com sistema acadêmico legado
- [ ] App mobile para consulta de horário
- [ ] Dashboard de planejamento de períodos
- [ ] Algoritmo automático de alocação (schedule optimization)
- [ ] Suporte a grades multi-campus

#### **Fase 3 (Visão de longo prazo)**
- [ ] Machine Learning para predição de conflitos
- [ ] Análise de utilização de salas (heatmaps)
- [ ] Integração com sistemas de videoconferência
- [ ] Suporte a aulas assíncronas/híbridas
- [ ] Marketplace de professores (para instituições múltiplas)

### 6.3 Métricas de Sucesso
| Métrica | Baseline | Meta |
|---------|----------|------|
| Tempo de montagem de grade | 8 horas manual | 30 minutos com sistema |
| Taxa de conflitos residuais | 15-20% | 0% |
| Tempo de resolução de erro | 2 horas | < 5 minutos |
| Satisfação do coordenador | - | 8+/10 |
| Taxa de retrabalho | 50% | < 5% |

---

## 7. Contexto Operacional do Sistema

### 7.1 Ambiente de Execução

```
┌────────────────────────────────────────────────────────────────┐
│                    Cliente (Frontend)                          │
│                                                                │
│  Browser ou App Mobile fazendo requisições HTTP               │
└────────────────────────────────────────────────────────────────┘
                              ↓ HTTP/HTTPS
┌────────────────────────────────────────────────────────────────┐
│                 GINI Backend (Spring Boot 3.5.14)              │
│                                                                │
│  ├─ Server: Tomcat embedded na porta 8080                     │
│  ├─ JVM: Java 21                                              │
│  ├─ Memory: 512MB a 2GB (configurável)                        │
│  └─ Load Balancer: Nginx (em produção)                        │
└────────────────────────────────────────────────────────────────┘
                              ↓ JDBC
┌────────────────────────────────────────────────────────────────┐
│                    PostgreSQL (Produção)                       │
│                    H2 em Memória (Desenvolvimento)             │
│                                                                │
│  ├─ Dados: usuários, grades, alocações, histórico            │
│  ├─ Conexão: pool 10-20 conexões                              │
│  └─ Backup: diário em produção                                │
└────────────────────────────────────────────────────────────────┘
```

### 7.2 Ciclo de Operação Típico

```
PERÍODO LETIVO (4 meses de duração)

├─ MÊS 1: Montagem de Grade (2-3 semanas)
│  ├─ Dia 1-3: Cadastro de dados (cursos, professores, salas)
│  ├─ Dia 4-10: Criação de alocações via GINI (iterativo)
│  ├─ Dia 11-14: Revisão e ajustes
│  └─ Dia 15: Aprovação e publicação
│
├─ MÊS 2-4: Execução e Ajustes (conforme necessidade)
│  ├─ Ajustes em caso de: professores ausentes, salas reformadas
│  ├─ Versão 2, 3... da grade criadas
│  └─ Histórico consulta durante todo período
│
└─ FINAL DO PERÍODO: Encerramento
   └─ Grade arquivada, pronta para cópia no próximo período
```

### 7.3 Integração com Outros Sistemas

```
GINI não é sistema isolado:

Entrada de dados (Manuais ou APIs futuras):
├─ [SISTEMA ACADÊMICO] → Cursos, Disciplinas, Turmas
├─ [SISTEMA RH] → Professores e Vinculações
└─ [SISTEMA PREDIAL] → Salas e Recursos

Saída de dados (Para outros sistemas):
├─ [PORTAL ACADÊMICO] ← Horário de aulas
├─ [SISTEMA DE PRESENÇA] ← Turmas + Horários
├─ [EMAIL] ← Notificações de aprovação
└─ [RELATÓRIOS] ← Ocupação de salas, carga docente

Sincronização:
└─ Atualmente: Manual (copiar/colar de emails/PDFs)
└─ Futura: APIs de integração com sistemas legados
```

### 7.4 Requisitos Não-Funcionais

| Requisito | Alvo |
|-----------|------|
| **Disponibilidade** | 99% (durante período de montagem) |
| **Tempo de resposta** | < 200ms para 95% das requisições |
| **Throughput** | 100 requisições/segundo |
| **Armazenamento** | 10GB por período letivo (estimado) |
| **Backup** | Diário, com retenção de 30 dias |
| **Disaster Recovery** | RTO < 1 hora, RPO < 5 minutos |
| **Segurança** | HTTPS obrigatório, criptografia de senhas |
| **Conformidade** | LGPD (Lei de Proteção de Dados) |

---

## 8. Regras e Políticas de Negócio

### 8.1 Regras de Validação Críticas

```
1. COMPATIBILIDADE DISCIPLINA-SALA
   ├─ Programação → Laboratório (obrigatório)
   ├─ Cálculo → Qualquer sala
   ├─ Educação Física → Quadra/Ginásio
   └─ Se incompatível → Erro 409 Conflict

2. DISPONIBILIDADE DO PROFESSOR
   ├─ Deve estar marcado como disponível no dia
   ├─ Caso contrário → Erro 409
   └─ Pode ser alterado após confirmação em reunião

3. REGRA DE 12 HORAS ENTRE JORNADAS
   ├─ Professor não pode ter menos de 12h entre última aula e primeira
   ├─ Ex: Se tem aula até 17:00, próxima só pode ser no dia seguinte após 05:00
   └─ Garantir qualidade de vida docente

4. CARGA HORÁRIA MÁXIMA
   ├─ Professor: máx 20h/semana (configurável por instituição)
   ├─ Disciplina: máx 4h/semana (conforme currículo)
   └─ Se ultrapassado → Erro 409

5. CAPACIDADE DE SALA
   ├─ Turma de 40 alunos não cabe em sala de 35 lugares
   └─ Se ultrapassado → Erro 409

6. NÃO-DUPLICIDADE
   ├─ Não pode ter mesma alocação 2x na grade
   ├─ (Turma + Disciplina + Professor + Dia + Horário)
   └─ Se duplicado → Erro 409

7. COERÊNCIA DE CURSOS
   ├─ Turma ADS-2A deve estar no grade de ADS
   ├─ Não pode haver mistura (ADS + Logística na mesma grade)
   └─ Se violado → Erro 409

8. PERÍODO LETIVO ATIVO
   ├─ Só pode criar alocação em grade de período ATIVO
   ├─ Períodos finalizados ou futuros não podem ser editados
   └─ Se violado → Erro 409
```

### 8.2 Estados e Transições

```
ESTADO DA GRADE HORÁRIA:

RASCUNHO ──[Editar/Criar Alocações]──→ RASCUNHO
   ↓                                        ↓
   └──────────[Aprovar]─────────────→ VIGENTE
                                         ↓
                              [Versionamento: v1 → v2]
                                         ↓
                                   ARQUIVO (fim período)

Regras:
├─ RASCUNHO: Pode criar/editar/deletar alocações livremente
├─ VIGENTE: Apenas edições com justificativa (cria versão nova)
└─ ARQUIVO: Apenas consulta (leitura)
```

### 8.3 Permissões por Perfil

```
COORDENADOR:
  ├─ Leitura: Cursos, Disciplinas, Turmas, Professores, Salas
  ├─ Escrita: Alocações (criar/editar/deletar)
  ├─ Consulta: Histórico, Relatórios
  └─ Operações: Criar grade, Aprovar grade, Copiar grade

ADMINISTRADOR:
  ├─ Leitura: Tudo
  ├─ Escrita: Todos os cadastros + Alocações
  └─ Operações: Todas

CONSULTOR:
  ├─ Leitura: Cursos, Disciplinas, Turmas, Salas, Grades, Alocações
  └─ Escrita: Nenhuma

PROFESSOR:
  ├─ Leitura: Seu próprio horário
  └─ Escrita: Nenhuma
```

---

## 9. Caso de Negócio e Justificativa

### 9.1 Problema Resolvido
Em instituições de ensino, a montagem de grade horária é um processo complexo, demorado e sujeito a erros. Coordenadores passam dias (às vezes semanas) resolvendo conflitos manualmente:
- "Professora X não pode estar em 2 salas ao mesmo tempo"
- "Turma Y tem 45 alunos mas sala só tem 40 lugares"
- "Professor Z trabalha 25h/semana, limite é 20h"

Isso resulta em:
- ⏱️ Tempo desperdiçado em operações repetitivas
- ❌ Erros descobertos tardiamente
- 🔄 Retrabalho frequente
- 😤 Frustração de coordenadores

### 9.2 Solução GINI
Automatizar validações, acelerando processo e garantindo qualidade:

```
ANTES (Manual)             DEPOIS (GINI)
┌─────────────┐           ┌─────────────┐
│ 40h de      │           │ 2h de       │
│ trabalho    │           │ trabalho    │
├─────────────┤           ├─────────────┤
│ 20% erro    │           │ 0% erro     │
│ residual    │           │ residual    │
├─────────────┤           ├─────────────┤
│ 2-3 rodadas │           │ 1 rodada    │
│ de ajuste   │           │ com sistema │
└─────────────┘           └─────────────┘
```

### 9.3 ROI (Return on Investment)

Investimento necessário:
- Desenvolvimento: 200h de desenvolvedores
- Testes: 40h
- Implantação: 20h
- **Total: 260 horas ≈ R$ 52.000**

Retorno por ano (assumindo 2 períodos/ano):
- Economia de tempo: 80h coordenador/ano × R$ 100/h = **R$ 8.000**
- Redução de erros: economia de 30h retrabalho × R$ 100/h = **R$ 3.000**
- Melhoria de qualidade: valor indireto significativo
- **Total por ano: ≈ R$ 11.000+**

**Payback: 5 anos** (conservador, considerando valor indireto maior)

---

## 10. Conclusão

O GINI é um sistema essencial para instituições de ensino que buscam **modernizar a gestão acadêmica, reduzir trabalho manual e garantir qualidade nas operações de ensino-aprendizagem**. 

Com validações automáticas rigorosas, rastreamento completo e interface intuitiva, o sistema permite que coordenadores se focusem em decisões estratégicas ao invés de tarefas operacionais repetitivas.

**Próximas ações recomendadas**:
1. Implementar autenticação e autorização (segurança)
2. Desenvolver interface web para acesso
3. Integrar com sistema acadêmico existente
4. Treinar coordenadores na nova ferramenta
5. Implementar feedback loop com usuários

---

## Apêndice: Glossário de Termos

| Termo | Definição |
|-------|-----------|
| **Grade Horária** | Estrutura que mapeia todas as aulas de um curso para um período letivo |
| **Alocação** | Atribuição de uma aula específica (turma + disciplina + professor + sala + dia + horário) |
| **Período Letivo** | Semestre ou bimestre acadêmico (ex: 2025-1 = 1º semestre 2025) |
| **Turma** | Grupo de alunos que cursam disciplinas juntos (ex: ADS-2A) |
| **Carga Horária** | Número total de horas por semana para disciplina ou professor |
| **Tipo de Sala** | Categoria de sala (laboratório, teórica, auditório, etc) |
| **Vínculo Professor-Disciplina** | Relação que indica que um professor pode ministrar certa disciplina |
| **Disponibilidade do Professor** | Dias e horários em que professor está disponível para aulas |
| **Compatibilidade** | Relação entre disciplina e tipo de sala (ex: programação precisa lab) |
| **Histórico** | Rastreamento de todas as criações e edições de alocações |
| **Versão da Grade** | Iteração de uma grade (v1, v2, v3...) durante montagem |
