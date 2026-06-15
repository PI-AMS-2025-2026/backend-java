# 📋 Backlog de Pendências e Melhorias - Sistema GINI

Este arquivo detalha todas as tarefas pendentes, refatorações necessárias e melhorias de infraestrutura identificadas no projeto, organizadas por prioridade e contexto técnico.

---

## 1. 🔒 Segurança e Controle de Acesso (RBAC) - Alta Prioridade
No arquivo [ConfiguracaoSeguranca.java](file:///c:/Users/felip/Desktop/backend-java/src/main/java/com/fatec/gini/web/config/ConfiguracaoSeguranca.java), a segurança atual é muito permissiva (`.anyRequest().authenticated()`).
- [ ] **Configurar Níveis de Acesso**:
  - **ADMIN**: Permissão irrestrita (leitura/escrita) em todas as rotas e dados de todos os cursos.
  - **COORDENADOR**: Restrito estritamente a endpoints relacionados ao seu **Curso** específico (cada coordenador gerencia apenas um curso).
- [ ] **Filtro de Escopo por Curso (Coordenador)**:
  - Implementar lógica de interceptação/filtro no Spring Security ou Service Layer para garantir que requisições vindas de um `COORDENADOR` filtrem dados e restrinjam a criação/alteração/visualização apenas de entidades (Grades, Turmas, Alocações, etc.) vinculadas ao seu respectivo curso.
- [ ] **Refatorar JWT & Refresh Token**:
  - Implementar fluxo completo de **Refresh Token** para renovar sessões expiradas sem necessidade de relogin.

---

## 2. ⚙️ Refatorações de Modelo e Regras de Negócio
Ajustar o backend para refletir as decisões registradas na documentação de atualizações:
- [ ] **Mapeamento de Status da Grade**:
  - Alterar o enum de transição e fluxos para usar estritamente `ATIVO` e `INATIVO` em vez do fluxo antigo (`RASCUNHO`, `APROVADA`, `VIGENTE`).
- [ ] **Disponibilidade Semestral do Professor**:
  - Adaptar a entidade `DisponibilidadeProfessor` para armazenar uma lista de horários disponíveis (onde ele *pode* dar aula) vinculada a um `PeriodoLetivo` (semestre).
- [ ] **Compatibilidade Disciplina ↔ Sala**:
  - Garantir a validação que impede alocar disciplinas indevidamente em salas cujo `TipoSala` seja incompatível (ex: disciplina teórica em laboratório químico, ou disciplina prática fora de laboratórios).
- [ ] **Nomenclatura de Turmas**:
  - Implementar validação padrão para forçar o formato `{PERIODO}{CURSO}-{ANO}` (ex: `2ADS-2026`).

---

## 3. 🚀 Novos Casos de Uso e Endpoints
- [ ] **Cópia de Grade com Limpeza Automática**:
  - Criar `CopiarGradeUseCase` para copiar alocações de um período para o outro, removendo automaticamente referências a professores inativados, salas indisponíveis ou geradores de conflitos no novo período.
- [ ] **Simulador de Validação (Endpoint GET)**:
  - Adicionar o endpoint `GET /alocacoes/validar` permitindo simular se uma alocação será bem-sucedida ou apontará erros de colisão, capacidade ou indisponibilidade, sem persistir no banco.
- [ ] **Exportação de Grade Horária**:
  - Desenvolver endpoints para exportar a grade em PDF (ex: usando iText) e Excel (ex: usando Apache POI).
- [ ] **Registro de Auditoria**:
  - Tornar o campo `motivo` obrigatório em requisições de alteração/exclusão em Alocações e salvar no histórico.
- [ ] **Workflow CI/CD integrado na AWS**
  - Desenvolver e planejar todo workflow nescessario para implementar automaticamente a branch main.

---

## 4. 🐳 Infraestrutura e DevOps
- [x] **Configuração do PostgreSQL & Docker**:
  - Desenvolver o `docker-compose.yml` para subir a aplicação integrada com o banco PostgreSQL.
- [ ] **Migração do Banco com Flyway**:
  - Substituir o arquivo manual [data.sql](file:///c:/Users/felip/Desktop/backend-java/data.sql) por migrations do Flyway configuradas em `src/main/resources/db/migration`.
- [ ] **Sistema de Logs**:
  - Configurar logs estruturados em arquivos separados por severidade (`info`, `error`, `audit`).

---

## 5. 🛠️ Qualidade de Código, Validações e Testes
- [ ] **Corrigir todos os TODOs do código**:
  - [ ] Resolver a validação de local pendente em [BlocoHorarioService.java:L80](file:///c:/Users/felip/Desktop/backend-java/src/main/java/com/fatec/gini/domain/services/BlocoHorarioService.java#L80).
  - [ ] Mover a validação de DTO pendente em [PeriodoAtividadeQuadroRequest.java:L22](file:///c:/Users/felip/Desktop/backend-java/src/main/java/com/fatec/gini/dto/periodoAtividadeQuadro/PeriodoAtividadeQuadroRequest.java#L22) para a Service correspondente.
- [ ] **Documentação no Swagger**:
  - Enriquecer as descrições dos endpoints nos controllers para facilitar o entendimento do time de frontend.
- [ ] **Testes de Cobertura**:
  - Implementar testes unitários e de integração para validações de carga horária máxima diária de 8 horas e intervalo interjornada de 12 horas.