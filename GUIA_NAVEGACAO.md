# 🗺️ Guia de Navegação - Documentação Técnica GINI

## 🎯 Comece Aqui

Este documento ajuda você a navegar pela documentação completa do sistema GINI de forma rápida e eficiente.

---

## 📍 Mapa de Documentos

```
Você está aqui: 📍 Guia de Navegação (este arquivo)

Documentação Disponível:
├── 📄 SUMARIO_EXECUTIVO.md ← ⭐ Comece aqui se tiver pouco tempo
├── 📄 ARQUITETURA_DO_SISTEMA.md ← Visão técnica completa (leitura técnica)
├── 📄 OBJETIVO_DO_SISTEMA.md ← Visão de negócio (leitura funcional)
├── 📄 INDICE_DOCUMENTACAO.md ← Índice completo com metadados
│
└── Documentação de Módulos (em src/):
    ├── src/main/java/com/fatec/gini/domain/services/README.md
    ├── src/main/java/com/fatec/gini/dto/README.md
    ├── src/main/java/com/fatec/gini/infrastructure/README.md
    └── src/main/java/com/fatec/gini/web/controller/README.md
```

---

## ⚡ Guias Rápidos por Perfil

### 👨‍💼 Gerente/Product Manager
**Objetivo**: Entender visão geral e roadmap

```
1️⃣ Leia: SUMARIO_EXECUTIVO.md (5 min)
   └─ O que foi documentado, estatísticas gerais

2️⃣ Leia: OBJETIVO_DO_SISTEMA.md (15 min)
   ├─ Propósito do sistema
   ├─ Funcionalidades centrais
   ├─ Fluxos de negócio
   └─ Visão de produto

3️⃣ Consulte: INDICE_DOCUMENTACAO.md (5 min)
   ├─ Próximas ações recomendadas
   ├─ Métricas de qualidade
   └─ Débitos identificados
```

---

### 🏗️ Arquiteto/Tech Lead
**Objetivo**: Entender arquitetura, riscos e decisões

```
1️⃣ Leia: SUMARIO_EXECUTIVO.md (5 min)
   └─ Visão geral e estatísticas

2️⃣ Leia: ARQUITETURA_DO_SISTEMA.md (30 min)
   ├─ Camadas arquiteturais
   ├─ Padrões utilizados
   ├─ Separação de responsabilidades
   ├─ Dependências e acoplamentos
   └─ Riscos técnicos e débitos

3️⃣ Revise: Seção "Diretrizes para Futuras Implementações"
   ├─ Princípios SOLID
   ├─ Estratégias de otimização
   └─ Checklist de pull requests

4️⃣ Consulte READMEs específicos conforme necessário
```

---

### 👨‍💻 Desenvolvedor Backend
**Objetivo**: Implementar features seguindo padrões

```
1️⃣ Contexto: OBJETIVO_DO_SISTEMA.md (10 min)
   └─ Entenda o que o sistema faz

2️⃣ Referência: ARQUITETURA_DO_SISTEMA.md (leitura rápida)
   └─ Matriz de Referência Rápida (último capítulo)

3️⃣ Seu Módulo: Leia README específico
   ├─ Alocações? → src/.../domain/services/README.md
   ├─ Controllers? → src/.../web/controller/README.md
   ├─ DTOs? → src/.../dto/README.md
   ├─ Banco? → src/.../infrastructure/README.md
   └─ Services? → src/.../domain/services/README.md

4️⃣ Implementar: Siga seção "Como Adicionar" no README
   └─ Checklists de desenvolvimento

5️⃣ Antes de Push: Revise "Boas Práticas" + "Checklists"
```

---

### 🧪 QA/Tester
**Objetivo**: Testar funcionalidades e fluxos

```
1️⃣ Entenda: OBJETIVO_DO_SISTEMA.md (15 min)
   ├─ Principais fluxos de negócio
   ├─ Atores do sistema
   └─ Validações automáticas

2️⃣ Casos de Teste: README do módulo Alocações
   └─ 13+ validações = 13+ cenários de teste

3️⃣ Fluxos: OBJETIVO_DO_SISTEMA.md
   ├─ Fluxo de Criação de Grade
   ├─ Fluxo de Atualização
   ├─ Fluxo de Cópia
   └─ Fluxo de Erro

4️⃣ Endpoints: src/.../web/controller/README.md
   └─ 23 endpoints × testes positivos/negativos
```

---

### 🔒 DevOps/SRE
**Objetivo**: Deploy, segurança, performance

```
1️⃣ Contexto: OBJETIVO_DO_SISTEMA.md
   └─ Contexto Operacional do Sistema

2️⃣ Requisitos: ARQUITETURA_DO_SISTEMA.md
   └─ Capítulo 8: "Requisitos Não-Funcionais"

3️⃣ Dependências: ARQUITETURA_DO_SISTEMA.md
   └─ Capítulo 7: "Dependências Críticas"

4️⃣ Riscos: INDICE_DOCUMENTACAO.md
   └─ Seção "Débitos Técnicos Identificados"
```

---

### 📊 Analista de Negócio
**Objetivo**: Entender regras e contexto operacional

```
1️⃣ Visão Geral: OBJETIVO_DO_SISTEMA.md
   ├─ Propósito principal
   ├─ Problemas resolvidos
   ├─ Atores envolvidos
   └─ Funcionalidades centrais

2️⃣ Regras: OBJETIVO_DO_SISTEMA.md
   └─ Capítulo 8: "Regras e Políticas de Negócio"

3️⃣ Fluxos: OBJETIVO_DO_SISTEMA.md
   └─ Capítulo 3: "Principais Fluxos de Negócio"

4️⃣ Casos de Uso: README do módulo Alocações
   └─ Entender como validações garantem regras
```

---

## 🔍 Pesquisa Rápida

### "Quero entender como funciona a criação de alocação"
```
Leia: src/.../domain/services/README.md
├─ Seção 6.1: Fluxo de Criação (Happy Path)
├─ Seção 6.2: Fluxo de Erro
└─ Seção 3: Funcionalidades Existentes (13+ validações)
```

### "Quero adicionar uma nova validação"
```
Leia: src/.../domain/services/README.md
└─ Seção 10: Como Adicionar Nova Validação
```

### "Quero entender a arquitetura geral"
```
Leia: ARQUITETURA_DO_SISTEMA.md
├─ Seção 2: Arquitetura em Camadas
├─ Seção 3: Padrões e Convenções
└─ Seção 6: Fluxo de Comunicação entre Módulos
```

### "Quero listar todos os endpoints"
```
Leia: src/.../web/controller/README.md
├─ Seção 3: Mapeamento de Requisições HTTP
└─ Seção 5: Controllers Principais
```

### "Quais são os débitos técnicos?"
```
Leia: INDICE_DOCUMENTACAO.md
└─ Seção: Débitos Técnicos Identificados

Ou: ARQUITETURA_DO_SISTEMA.md
└─ Seção 8: Riscos Técnicos e Acoplamentos
```

### "Quais são as validações de alocação?"
```
Leia: src/.../domain/services/README.md
├─ Seção 2.2: Validações Automáticas
└─ Tabela com 13 validações descritas
```

### "Como funciona o histórico de alterações?"
```
Leia: src/.../domain/services/README.md
└─ Seção 2.3: Histórico de Alterações

Ou: OBJETIVO_DO_SISTEMA.md
└─ Seção 6.2: Rastreamento com Histórico
```

### "Quais as próximas ações?"
```
Leia: INDICE_DOCUMENTACAO.md
└─ Próximas Ações Recomendadas

Ou: ARQUITETURA_DO_SISTEMA.md
└─ Seção 9: Diretrizes para Futuras Implementações
```

---

## 📚 Leitura Recomendada por Tempo Disponível

### ⏱️ 5 minutos
```
SUMARIO_EXECUTIVO.md
```

### ⏱️ 15 minutos
```
SUMARIO_EXECUTIVO.md
+ OBJETIVO_DO_SISTEMA.md (resumo)
```

### ⏱️ 30 minutos
```
SUMARIO_EXECUTIVO.md
+ OBJETIVO_DO_SISTEMA.md (completo)
+ INDICE_DOCUMENTACAO.md (referência rápida)
```

### ⏱️ 1 hora
```
SUMARIO_EXECUTIVO.md
+ OBJETIVO_DO_SISTEMA.md
+ ARQUITETURA_DO_SISTEMA.md (sem detalhes)
```

### ⏱️ 2-3 horas
```
Todos os documentos acima
+ 1 README de módulo (seu módulo)
```

### ⏱️ 4+ horas
```
Leitura completa:
- SUMARIO_EXECUTIVO.md
- OBJETIVO_DO_SISTEMA.md
- ARQUITETURA_DO_SISTEMA.md
- INDICE_DOCUMENTACAO.md
- Todos os 4 READMEs de módulos
```

---

## 🔗 Índice de Seções Cruzadas

### Por Tópico

#### "Arquitetura"
- ARQUITETURA_DO_SISTEMA.md → Seção 2-6
- INDICE_DOCUMENTACAO.md → Análise de Cobertura

#### "Segurança"
- ARQUITETURA_DO_SISTEMA.md → Seção 4.5 e 9.6
- OBJETIVO_DO_SISTEMA.md → Seção 8.3
- Módulo Controllers → Débitos Técnicos

#### "Performance"
- INDICE_DOCUMENTACAO.md → Débitos (N+1, Cache)
- Módulo Infraestrutura → Seção 10 (Riscos)
- ARQUITETURA_DO_SISTEMA.md → Seção 9.5

#### "Testes"
- Cada módulo → Seção de Testes
- OBJETIVO_DO_SISTEMA.md → Fluxos (cenários)
- ARQUITETURA_DO_SISTEMA.md → Checklist PR

#### "Integração"
- OBJETIVO_DO_SISTEMA.md → Seção 7.3 (Integrações)
- ARQUITETURA_DO_SISTEMA.md → Seção 7.3

#### "Fluxos de Negócio"
- OBJETIVO_DO_SISTEMA.md → Seção 3 (4 fluxos)
- Módulo Alocações → Seção 6 (fluxos técnicos)

---

## 📞 Suporte e Questões

### "Não entendo um termo/conceito"
→ Consulte: OBJETIVO_DO_SISTEMA.md → Apêndice: Glossário de Termos

### "Preciso de um exemplo de código"
→ Consulte cada README → Seção "Como Adicionar XXX"

### "Quero saber por que fiz assim"
→ Consulte: ARQUITETURA_DO_SISTEMA.md → Seção 3 (Padrões)

### "Preciso entender um módulo específico"
→ Consulte o README desse módulo em src/...

### "Preciso de checklist de development"
→ Consulte cada README → Seção final "Checklist"

---

## 🎓 Caminho de Aprendizado Sugerido

```
Semana 1: Compreensão Geral
  Dia 1: SUMARIO_EXECUTIVO.md + OBJETIVO_DO_SISTEMA.md
  Dia 2: ARQUITETURA_DO_SISTEMA.md (leitura rápida)
  Dia 3: INDICE_DOCUMENTACAO.md + Overview

Semana 2: Especialização por Módulo
  Leia README do seu módulo
  Estude código-fonte
  Entenda dependências

Semana 3+: Implementação
  Siga checklists
  Revise padrões
  Implemente features
```

---

## ✅ Checklist de Orientação

- [ ] Li SUMARIO_EXECUTIVO.md (entendi o escopo)
- [ ] Li OBJETIVO_DO_SISTEMA.md (entendi por que)
- [ ] Li ARQUITETURA_DO_SISTEMA.md (entendi como)
- [ ] Identifiquei meu perfil (dev, tester, tech lead, etc)
- [ ] Li README do meu módulo
- [ ] Entendi fluxos principais
- [ ] Identifiquei débitos técnicos que me afetam
- [ ] Revisei checklists relevantes
- [ ] Pronto para trabalhar!

---

## 📝 Notas Finais

1. **Docum. é Living**: Quando implementar, atualize os READMEs
2. **Referência**: Use como referência diária, não memorize
3. **Compartilhado**: Envie links para documentação ao invés de explicar
4. **Feedback**: Se achar imprecisão, abra issue/PR
5. **Evolução**: A documentação evolui com o sistema

---

## 🚀 Pronto para Começar?

### Se você é:
- **Novo na empresa**: Siga "Caminho de Aprendizado Sugerido"
- **Desenvolvedor novo**: Leia seu módulo + seção "Como Adicionar"
- **Tester**: Leia OBJETIVO_DO_SISTEMA.md + Fluxos
- **Arquiteto**: Leia ARQUITETURA_DO_SISTEMA.md + Riscos
- **Manager**: Leia OBJETIVO_DO_SISTEMA.md + Roadmap

### Próximo passo:
👉 Abra o arquivo apropriado para seu perfil

---

**Documentação do Sistema GINI**  
**Versão**: 1.0.0  
**Status**: ✅ Completa e Navegável
