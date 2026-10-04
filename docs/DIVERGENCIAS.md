# DIVERGENCIAS.md — Matriz de Reconciliação e Auditoria de Divergências

> **Objetivo:** Mapear e auditar todas as divergências identificadas entre a **Documentação Legada** (`docs/documentacao/`), os **Relatos dos Desenvolvedores** (`docs/relatos/`), o **Código Existente** (`src/`) e as decisões da **Elicitação Consolidada** (`ESCOPO.md`).  
> **Status:** Auditado e Reconciliado  
> **Versão:** 1.0.0  
> **Data:** 01/10/2026  

---

## 1. Princípio da Resolução de Divergências

Conforme definido na governança do projeto, a ordem estrita de precedência adotada é:
1. **Resultado da Elicitação / ESCOPO.md (Prioridade 1 - Máxima)**
2. **Relatos dos Desenvolvedores / `docs/relatos` (Prioridade 2)**
3. **Código Existente Funcional / `src/` (Prioridade 3)**
4. **Documentação Legada / `docs/documentacao` (Prioridade 4)**

---

## 2. Matriz Comparativa de Divergências

| Item / Domínio | Documentação Legada (`docs/documentacao`) | Código Existente (`src/`) | Relatos da Equipe (`docs/relatos`) | Decisão Consolidada (`ESCOPO.md` / `TO-BE`) | Ação Necessária |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Nomenclatura: Grade vs Quadro** | Usava `GRADE_HORARIA` e *Grade Horária*. | Entidade JPA `QuadroHorario` já criada, mas DTOs ainda usavam `dto.grade.*` e referências como `/grades`. | Relato #2: Exige mudança estrita de *Grade Horária* para *Quadro Horário*. | **Quadro Horário** (`QuadroHorario`, `/quadros-horarios`, `/motor-quadro`). | Renomear pacote `dto.grade` para refletir `quadroHorario` ou `motorQuadro` e padronizar rotas. |
| **Nomenclatura: Período Letivo vs Atividade** | Usava `PERIODO_LETIVO`. | Entidade `PeriodoAtividadeQuadro`, mas controller anotado como `@RequestMapping("/periodo_atividade_quadro")`. | Relato #2: Exige *Período de Atividade do Quadro*. | **Período de Atividade do Quadro** com rota RESTful `/periodos-atividade-quadro`. | Corrigir rota do controller para `kebab-case` plural (`/periodos-atividade-quadro`). |
| **Nomenclatura: Histórico de Alterações** | Usava `HISTORICO_ALTERACAO`. | Entidade `HistoricoVersaoAlocacao`, mas controller nomeado `HistoricoAlteracaoController` em `/historicos-alteracoes`. | Relato #2: Exige *Histórico de Versão da Alocação*. | **Histórico de Versão da Alocação** com rota `/historicos-versoes-alocacoes`. | Renomear controller e rota para `/historicos-versoes-alocacoes`. |
| **Nomenclatura: Horário vs Bloco** | Usava `HORARIO`. | Entidade `BlocoHorario` e controller `/bloco-horarios`, mas `AlocacaoRequest` ainda possuía campo `horarioId`. | Relato #2: Exige *Bloco de Horário*. | **Bloco de Horário** (`bloco_horario`). | Atualizar campos de request de `horarioId` para `bloco_horario` com `LongDTO`. |
| **Motor: Grade vs Quadro** | Inexistente na documentação inicial de requisitos. | Classe `MotorQuadroHorario` criada isolada, sem controller e sem rota associada. | Relato #1 e #2: Exige motor de quadro atendendo `motor-grade.md` e renomeação para `motor-quadro`. | **Motor de Quadro** exposto formalmente em `/motor-quadro/cursos/...`. | Criar `MotorQuadroController` e expor rotas de curso e turma. |
| **Padrão de Endpoints RESTful** | Misturava plurais, singulares e underscores (`/recurso-sala`, `/periodo_atividade_quadro`). | Controllers herdaram convenções divergentes de commits anteriores. | Relato #2: "Analise o padrão e corrija os endpoints também". | Padrão estrito RESTful em **`kebab-case` plural**. | Padronizar todas as anotações `@RequestMapping` dos controllers. |
| **Serialização de JSON e Query Params** | Não especificado (gerava camelCase padrão do Spring). | Classes em camelCase, query params em camelCase (`dataInicio`, etc.). | Elicitação: Payloads e parâmetros de query devem ser obrigatoriamente `snake_case`. | **`snake_case` global** via configuração do Jackson (`PropertyNamingStrategies.SNAKE_CASE`). | Adicionar configuração global no Spring e ajustar `@RequestParam` para snake_case. |
| **Chaves Estrangeiras em Requests** | IDs planos ou mistos. | Misto: `TurmaRequest` usava `LongDTO curso`, enquanto `AlocacaoRequest` e `DisciplinaRequest` usavam IDs planos (`turmaId`, `cursoId`). | Relato #3: Exige que todos os DTOs de request utilizem `LongDTO`. | **`LongDTO` obrigatório** em todos os DTOs de request com entidades estrangeiras. | Migrar `AlocacaoRequest`, `DisciplinaRequest`, `RecursoSalaRequest`, etc., para `LongDTO`. |
| **Login Repetido / Sessão Concorrente** | Não especificado na documentação antiga. | `RefreshTokenService` falha com `DataIntegrityViolationException` ao logar mais de uma vez devido a `@OneToOne` e falta de flush no `delete`. | Relato #4: Exige permitir logar mais de uma vez sem necessidade de logout. | **Sessão Única com Rotação Atômica**: o login substitui e rotaciona o token com segurança no banco sem lançar erro. | Ajustar `RefreshTokenService.createRefreshToken` para atualização atômica do token. |
| **Bloqueio 403 Forbidden em Dev** | Não considerava perfis de ambiente. | `SecurityFilter` ativo e validadores de curso barravam requisições em dev com 403. | Relato #5: Exige garantia de que não exista bloqueio forbidden ao usar perfil dev. | **Perfil `dev` liberado**: sem checagem de autorização de curso e sem 403. | Ajustar `ValidarAutorizacaoCursoUseCase` e cadeia de filtros no perfil `dev`. |
| **Exportação de Arquivos** | Não constava nos requisitos legados. | Template estático HTML presente em `templates/QuadroHorario.html`. | Relato #8: Exige exportação através do motor de quadro. Elicitação: PDF e Planilha Excel. | **Exportação Dual**: PDF via Thymeleaf + OpenHTMLtoPDF e Excel via Apache POI. | Implementar serviços de exportação em `domain.services.export` e rotas no controller. |
| **Recuperação de Senha** | Não constava na documentação legada. | Inexistente (sem dependência de mail, sem entidade, sem rotas). | Relato #7: Exige recuperação de senha com envio de e-mail (15 min de expiração). | **Recuperação de Senha**: Endpoints `/auth/solicitar-recuperacao` e `/auth/redefinir-senha`. | Adicionar `spring-boot-starter-mail`, criar migração Flyway, entidade de token e endpoints. |
| **Infraestrutura Docker** | `docker-compose.yml` sem variáveis do Postgres e sem perfil do backend. | Backend iniciava em dev com H2 ao invés de Postgres, e sem `.dockerignore`. | Relato #6: Exige ajuste do Dockerfile e docker-compose com instruções de uso. | **Docker Prod-Ready**: Postgres 17 configurado, Mailpit para testes de SMTP em dev e Dockerfile multistage. | Corrigir `docker-compose.yml`, `.env.exemple`, criar `.dockerignore` e documentar uso. |

---

## 3. Plano de Correção e Rastreabilidade com o Backlog

Todas as divergências listadas acima foram transformadas em tarefas no arquivo [`TASKS.md`](file:///c:/Users/felip/Desktop/PI/backend-java/TASKS.md):

* **Divergência de Segurança em Dev:** Resolvida por [`TASK-001`](file:///c:/Users/felip/Desktop/PI/backend-java/TASKS.md#task-001--desbloqueio-no-perfil-dev-e-alinhamento-de-cors).
* **Divergência de Login Repetido:** Resolvida por [`TASK-002`](file:///c:/Users/felip/Desktop/PI/backend-java/TASKS.md#task-002--rotação-atômica-de-refreshtoken-para-múltiplos-logins).
* **Divergência de Serialização (snake_case):** Resolvida por [`TASK-003`](file:///c:/Users/felip/Desktop/PI/backend-java/TASKS.md#task-003--configuração-global-jackson-snake_case-em-payloads-e-query-params).
* **Divergência de Nomenclatura de Rotas:** Resolvida por [`TASK-004`](file:///c:/Users/felip/Desktop/PI/backend-java/TASKS.md#task-004--padronização-de-rotas-restful-em-kebab-case-plural).
* **Divergência de Chaves Estrangeiras (LongDTO):** Resolvida por [`TASK-005`](file:///c:/Users/felip/Desktop/PI/backend-java/TASKS.md#task-005--padronização-de-entidades-estrangeiras-nos-request-dtos-com-longdto).
* **Divergência do Motor de Quadro:** Resolvida por [`TASK-006`](file:///c:/Users/felip/Desktop/PI/backend-java/TASKS.md#task-006--exposição-dos-endpoints-do-motor-de-quadro-curso-e-turma).
* **Divergência de Exportação:** Resolvida por [`TASK-007`](file:///c:/Users/felip/Desktop/PI/backend-java/TASKS.md#task-007--módulo-de-exportação-do-motor-de-quadro-pdf-e-planilha-excel).
* **Divergência de Recuperação de Senha:** Resolvida por [`TASK-008`](file:///c:/Users/felip/Desktop/PI/backend-java/TASKS.md#task-008--implementação-do-fluxo-de-recuperação-de-senha-por-e-mail).
* **Divergência de Docker e Variáveis:** Resolvida por [`TASK-009`](file:///c:/Users/felip/Desktop/PI/backend-java/TASKS.md#task-009--adequação-do-dockerfile-docker-composeyml-e-variáveis-de-ambiente).
* **Validação Contínua de Testes:** Acompanhada por [`TASK-010`](file:///c:/Users/felip/Desktop/PI/backend-java/TASKS.md#task-010--atualização-e-ampliação-da-suíte-de-testes-automatizados).
