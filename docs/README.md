# 📚 Documentação do Sistema GINI

Bem-vindo à documentação completa do sistema **GINI** (Grade Horária Inteligente). Esta pasta contém toda a informação necessária para entender, implementar e manter o sistema.

---

## 📖 Documentos Disponíveis

### 🎯 **Início Rápido**

#### **SUMARIO_ATUALIZACOES.md** ⭐ **COMECE AQUI**
- **Para**: Todos (100 linhas, 5 min de leitura)
- **O quê**: Resumo de todas as mudanças baseadas em entrevista com stakeholder
- **Contém**:
  - 14 mudanças críticas confirmadas
  - Prioridades de implementação (crítica/alta/média)
  - Checklist de implementação
  - Impacto em números (arquivos, usecases, entidades)

---

### 📋 **Especificações**

#### **OBJETIVO_DO_SISTEMA.md**
- **Para**: Product Manager, Stakeholder, Desenvolvedores
- **O quê**: Visão de negócio e fluxos do sistema
- **Contém**:
  - Propósito e valor entregue
  - Problemas que resolve
  - Fluxos de negócio (primário, secundário, cópia, atualização)
  - Atores e permissões (ADMIN, COORDENADOR)
  - Funcionalidades implementadas
  - Visão futura (roadmap)
  - Caso de negócio e ROI
  - Glossário de termos

**Seções Principais**:
1. Propósito Principal
2. Problemas Resolvidos
3. Fluxos de Negócio
4. Atores (ADMIN + COORDENADOR)
5. Funcionalidades (CRUD + Validações + Histórico)
6. Visão de Produto (MVP + Roadmap)
7. Contexto Operacional
8. Regras de Negócio (Validações críticas)

---

#### **ARQUITETURA_DO_SISTEMA.md**
- **Para**: Arquiteto, Desenvolvedores, Tech Lead
- **O quê**: Design técnico e padrões
- **Contém**:
  - Tecnologias principais (Spring Boot 3.5.14, Java 21, PostgreSQL)
  - Arquitetura em camadas (Controller → Service → UseCase → Repository → DB)
  - Padrões (Repository, DTO, UseCase, Service, Mapper)
  - Convenções de nomenclatura (entidades, controllers, services, usecases)
  - Tratamento de exceções
  - Validação em camadas
  - Separação de responsabilidades
  - Fluxo de comunicação entre módulos
  - Dependências críticas
  - Riscos técnicos e acoplamentos
  - Débitos técnicos
  - Diretrizes para futuras implementações
  - Estratégia de otimização

**Seções Principais**:
1. Visão Geral Arquitetural
2. Arquitetura em Camadas
3. Padrões e Convenções
4. Regras Arquiteturais
5. Separação de Responsabilidades
6. Fluxo de Comunicação
7. Dependências Críticas
8. Riscos Técnicos

---

### ⚙️ **Decisões Arquiteturais**

#### **ADR.md** ⭐ **LEIA SE FOR IMPLEMENTAR**
- **Para**: Arquiteto, Tech Lead, Desenvolvedores
- **O quê**: 14 decisões arquiteturais com justificativas
- **Contém**:
  - Contexto de cada decisão
  - Decisão tomada
  - Justificativa
  - Implicações técnicas
  - Alternativas rejeitadas
  - Código exemplo
  - Status (confirmado/precisa refino)

**ADRs**:
1. Status Grade: ATIVO/INATIVO
2. RBAC por Curso (ADMIN 100%, COORDENADOR seu curso)
3. Carga Horária: 8h/DIA (não semana)
4. Disponibilidade: Lista de Permitidos (semestral)
5. Compatibilidade: TipoSala
6. Auditoria: Motivo Obrigatório
7. Cópia de Grade: com Limpeza Automática
8. Validação: GET endpoints (sem persistência)
9. Soft/Hard Delete Policy
10. Nomenclatura Turmas: {PERIODO}{CURSO}-{ANO}
11. Descanso: 12 horas entre jornadas
12. Período: Semestral
13. Exportação: PDF + Excel
14. Cadastro: 100% Manual (sem SIS)

---

### 📝 **Referência Completa**

#### **ATUALIZACOES_ENTREVISTA.md**
- **Para**: Desenvolvedores, Tech Lead, QA
- **O quê**: Detalhes completos de cada mudança confirmada
- **Contém**:
  - 14 mudanças críticas com "ANTES vs AGORA"
  - Detalhes técnicos de cada mudança
  - Impacto na arquitetura
  - Código exemplo para mudanças complexas
  - Checklist de implementação
  - Validações necessárias
  - Testes recomendados

**Seções**:
1. Status Grade
2. Usuários e RBAC
3. Carga Horária
4. Disponibilidade
5. Compatibilidade Disciplina-Sala
6. Cadastro Manual
7. Auditoria
8. Cópia de Grade
9. Validação Sem Salvar
10. Turmas
11. Período Letivo
12. Exportação
13. Deleção/Inativação
14. Erros e Tratamento

---

### 🔌 **API**

#### **ESPECIFICACAO_API.md** ⭐ **PARA FRONTEND**
- **Para**: Frontend Developer, QA, Integrador
- **O quê**: Documentação completa de endpoints da API
- **Contém**:
  - Autenticação (login)
  - CRUD de dados mestres (Professores, Salas, Disciplinas, Turmas)
  - Grade horária (criar, copiar, listar, exportar)
  - Alocações (criar, validar, editar, deletar, histórico)
  - Auditoria (acesso ADMIN)
  - Estrutura de erros
  - Status HTTP
  - Paginação
  - Exemplo de fluxo completo

**Endpoints Principais**:
- `POST /auth/login` - Autenticar
- `POST /grades-horarias` - Criar grade
- `POST /grades-horarias/{id}/copiar` - Copiar grade
- `POST /alocacoes` - Criar alocação
- `GET /alocacoes/validar` - Validar sem salvar
- `GET /grades-horarias/{id}/exportar/pdf` - Exportar
- `GET /alocacoes/{id}/historico` - Ver histórico
- `DELETE /alocacoes/{id}?motivo=...` - Deletar com motivo

---

## 🎓 COMO USAR ESTA DOCUMENTAÇÃO

### 👨‍💼 **Se você é Product Manager**
1. Leia **SUMARIO_ATUALIZACOES.md** (5 min)
2. Leia **OBJETIVO_DO_SISTEMA.md** - Seção 1-6 (20 min)
3. Use prioridades para planejamento de sprint

### 👨‍💻 **Se você é Desenvolvedor**
1. Leia **SUMARIO_ATUALIZACOES.md** (5 min)
2. Leia **ADR.md** - ADRs críticas (001-004) (30 min)
3. Leia **ATUALIZACOES_ENTREVISTA.md** - seções relevantes (20 min)
4. Consulte **ESPECIFICACAO_API.md** para endpoints (10 min)
5. Implemente baseado em ADRs

### 🏗️ **Se você é Arquiteto**
1. Leia **SUMARIO_ATUALIZACOES.md** (5 min)
2. Leia **ADR.md** - todas as 14 decisões (1 hora)
3. Leia **ARQUITETURA_DO_SISTEMA.md** (1 hora)
4. Leia **ATUALIZACOES_ENTREVISTA.md** - impacto arquitetural (30 min)
5. Valide implementação contra ADRs

### 🧪 **Se você é QA**
1. Leia **SUMARIO_ATUALIZACOES.md** (5 min)
2. Consulte checklist de testes (30 min)
3. Leia **ESPECIFICACAO_API.md** - status HTTP e erros (15 min)
4. Crie testes baseado em cenários de OBJETIVO

### 🔌 **Se você faz Frontend**
1. Leia **SUMARIO_ATUALIZACOES.md** (5 min)
2. Leia **ESPECIFICACAO_API.md** - todos os endpoints (1 hora)
3. Use exemplos de request/response
4. Implemente autenticação (RBAC por curso)
5. Valide com GET /validar antes de POST

### 🎬 **Se você é Product/Stakeholder**
1. Leia **OBJETIVO_DO_SISTEMA.md** - Seção 1-3, 8 (30 min)
2. Leia **SUMARIO_ATUALIZACOES.md** (5 min)
3. Valide que tudo está certo

---

## 📊 ESTRUTURA DE LEITURA RECOMENDADA

### **Semana 1 - Entendimento**
- [ ] SUMARIO_ATUALIZACOES.md (TODOS)
- [ ] OBJETIVO_DO_SISTEMA.md (seções 1-6)
- [ ] ADR.md (ADRs 001-008)

### **Semana 2 - Especificação**
- [ ] ATUALIZACOES_ENTREVISTA.md (mudanças críticas)
- [ ] ESPECIFICACAO_API.md (endpoints principais)
- [ ] ARQUITETURA_DO_SISTEMA.md (camadas e padrões)

### **Semana 3 - Implementação**
- [ ] ADR.md (ADRs 009-014)
- [ ] ESPECIFICACAO_API.md (validações e erros)
- [ ] ATUALIZACOES_ENTREVISTA.md (checklist)

---

## 🚀 ROADMAP DE IMPLEMENTAÇÃO

### **Fase 1 - CRÍTICA (Sprint 1-2)**
Baseado em prioridades do SUMARIO_ATUALIZACOES.md:
- [ ] RBAC por Curso (ADR-002)
- [ ] Carga Horária por DIA (ADR-003)
- [ ] Disponibilidade Semestral (ADR-004)
- [ ] Status ATIVO/INATIVO (ADR-001)

### **Fase 2 - ALTA (Sprint 3-4)**
- [ ] Endpoints GET Validação (ADR-008)
- [ ] Auditoria com Motivo (ADR-006)
- [ ] Cópia de Grade (ADR-007)
- [ ] Soft/Hard Delete (ADR-009)

### **Fase 3 - MÉDIA (Sprint 5-6)**
- [ ] Exportação PDF/Excel (ADR-013)
- [ ] Compatibilidade TipoSala (ADR-005)
- [ ] Nomenclatura Turmas (ADR-010)
- [ ] Descanso 12h (ADR-011)

---

## 📞 REFERÊNCIA RÁPIDA

### Onde encontro informação sobre...

**Status da Grade?**
→ ADR.md (ADR-001), ATUALIZACOES_ENTREVISTA.md (seção 1)

**RBAC e Permissões?**
→ ADR.md (ADR-002), OBJETIVO_DO_SISTEMA.md (seção 4), ATUALIZACOES_ENTREVISTA.md (seção 2)

**Carga Horária?**
→ ADR.md (ADR-003), ATUALIZACOES_ENTREVISTA.md (seção 3), OBJETIVO_DO_SISTEMA.md (seção 8.1)

**Endpoints de API?**
→ ESPECIFICACAO_API.md (todas as seções)

**Fluxo de Negócio?**
→ OBJETIVO_DO_SISTEMA.md (seção 3)

**Padrões Técnicos?**
→ ARQUITETURA_DO_SISTEMA.md (seções 3-5)

**Validações?**
→ ADR.md (ADRs múltiplas), OBJETIVO_DO_SISTEMA.md (seção 8.1)

**Auditoria e Histórico?**
→ ADR.md (ADR-006), ATUALIZACOES_ENTREVISTA.md (seção 7)

**Cópia de Grade?**
→ ADR.md (ADR-007), ATUALIZACOES_ENTREVISTA.md (seção 8)

**Disponibilidade Professor?**
→ ADR.md (ADR-004), ATUALIZACOES_ENTREVISTA.md (seção 4)

---

## ✅ CHECKLIST PRÉ-IMPLEMENTAÇÃO

Antes de começar a implementar, verifique:

- [ ] Li SUMARIO_ATUALIZACOES.md
- [ ] Entendo as 14 mudanças críticas
- [ ] Tenho acesso a todos os 6 documentos principais
- [ ] Entendo o fluxo de negócio (OBJETIVO)
- [ ] Entendo os padrões técnicos (ARQUITETURA)
- [ ] Analisei as ADRs relevantes
- [ ] Entendo as prioridades de implementação
- [ ] Validei que documentação está clara com stakeholder

---

## 🔄 MANUTENÇÃO DA DOCUMENTAÇÃO

Esta documentação foi atualizada em **2026-06-01** baseado em entrevista com stakeholder.

### Quando Atualizar?
- Quando houver mudança em decisão arquitetural → Atualizar ADR.md
- Quando houver mudança em fluxo de negócio → Atualizar OBJETIVO_DO_SISTEMA.md
- Quando adicionar novo endpoint → Atualizar ESPECIFICACAO_API.md
- Quando implementar ADR → Marcar como ✅ em SUMARIO_ATUALIZACOES.md
- Quando descobrir risco novo → Atualizar ARQUITETURA_DO_SISTEMA.md seção 8

### Versionamento
- v1.0: 2026-06-01 - Versão inicial após entrevista
- v1.1: (em planejamento)

---

## 📚 DOCUMENTOS POR TAMANHO

| Documento | Linhas | Tempo Leitura | Para Quem |
|-----------|--------|---------------|-----------|
| SUMARIO_ATUALIZACOES.md | 300 | 5 min | Todos |
| OBJETIVO_DO_SISTEMA.md | 600+ | 45 min | PM, Stakeholder |
| ADR.md | 700+ | 60 min | Arquiteto, Dev |
| ESPECIFICACAO_API.md | 500+ | 45 min | Frontend, Dev |
| ATUALIZACOES_ENTREVISTA.md | 600+ | 45 min | Dev, QA |
| ARQUITETURA_DO_SISTEMA.md | 600+ | 60 min | Arquiteto, Dev |

**Total**: ~3400 linhas = ~4-5 horas de leitura completa

---

## 🎯 PRÓXIMOS PASSOS

1. **Leia SUMARIO_ATUALIZACOES.md** (seu ponto de partida)
2. **Escolha seu perfil** (PM, Dev, Arquiteto, QA, Frontend)
3. **Siga o roteiro recomendado**
4. **Implemente as mudanças** seguindo prioridades
5. **Valide com testes**
6. **Atualize documentação** conforme implementar

---

## ❓ DÚVIDAS?

Se tiver dúvidas sobre algum documento:
1. Consulte a seção "Referência Rápida" acima
2. Procure no índice do documento
3. Use Ctrl+F para buscar termos específicos

---

**Documentação Completa do Sistema GINI**  
*Última atualização: 2026-06-01*  
*Status: ✅ Pronta para implementação*
