# TASKS.md — Backlog Executável de Correção e Implementação

> **Status Geral do Backlog:** Pronto para Execução  
> **Versão:** 1.0.0  
> **Data:** 24/09/2026  

---

## Resumo do Backlog

| ID | Título | Assignee | Status | Dependências |
| :---: | :--- | :---: | :---: | :---: |
| **TASK-000A** | Auditoria e Reconciliação de Divergências Documentais (`docs/DIVERGENCIAS.md`) | `AI` | `DONE` | *Nenhuma* |
| **TASK-000B** | Auditoria da Lógica Executada e Inventário de Código Intocável (`docs/STATUS_LOGICA_EXISTENTE.md`) | `AI` | `DONE` | *Nenhuma* |
| **TASK-001** | Desbloqueio no Perfil Dev e Alinhamento de CORS | `AI` | `DONE` | *Nenhuma* |
| **TASK-002** | Rotação Atômica de RefreshToken para Múltiplos Logins | `AI` | `DONE` | *Nenhuma* |
| **TASK-003** | Configuração Global Jackson (snake_case) em Payloads e Query Params | `AI` | `DONE` | *Nenhuma* |
| **TASK-004** | Padronização de Rotas RESTful em kebab-case Plural | `AI` | `DONE` | `TASK-003` |
| **TASK-005** | Padronização de Entidades Estrangeiras nos Request DTOs com LongDTO | `AI` | `DONE` | `TASK-003` |
| **TASK-006** | Exposição dos Endpoints do Motor de Quadro (Curso e Turma) | `AI` | `DONE` | `TASK-004`, `TASK-005` |
| **TASK-007** | Módulo de Exportação do Motor de Quadro (PDF e Planilha Excel) | `AI` | `DONE` | `TASK-006` |
| **TASK-008** | Implementação do Fluxo de Recuperação de Senha por E-mail | `AI` | `DONE` | `TASK-001`, `TASK-003` |
| **TASK-009** | Adequação do Dockerfile, docker-compose.yml e Variáveis de Ambiente | `AI` | `DONE` | *Nenhuma* |
| **TASK-010** | Atualização e Ampliação da Suíte de Testes Automatizados | `AI` | `DONE` | `TASK-002` a `TASK-008` |
| **TASK-011** | Configuração de Provedor SMTP e Chaves de Produção | `HUMAN` | `BLOCKED` | `TASK-008` |
| **TASK-012** | Homologação Integrada com a Equipe de Frontend Next.js | `HUMAN` | `BLOCKED` | `TASK-010` |

---

## Detalhamento das Tarefas

### `TASK-000A` — Auditoria e Reconciliação de Divergências Documentais
* **Assignee:** `AI`
* **Status:** `DONE`
* **Objetivo:**
  1. Analisar todo o projeto (código, documentação legada, relatos da equipe e requisitos).
  2. Mapear todas as divergências existentes (nomenclatura, rotas, payloads, comportamento de login, perfis de ambiente).
  3. Gerar documento formal de reconciliação e correção: `docs/DIVERGENCIAS.md`.
* **Dependências:** `DEPENDS_ON: None`
* **Artefatos Gerados:**
  * `docs/DIVERGENCIAS.md`
* **Allowed Changes:**
  * `docs/DIVERGENCIAS.md`
* **Forbidden Changes:**
  * Qualquer código funcional pré-existente.
* **Observações:** Divergências mapeadas em 13 eixos temáticos e 100% resolvidas através dos documentos de governança (`ESCOPO.md` e `FRONTEND_INTEGRATION.md`).

---

### `TASK-000B` — Auditoria da Lógica Executada e Inventário de Código Intocável
* **Assignee:** `AI`
* **Status:** `DONE`
* **Objetivo:**
  1. Executar a suíte completa de testes (`mvnw test`) para auditar o funcionamento real da lógica planejada e implementada.
  2. Identificar quais regras de negócio (interjornada de 12h, choques de sala/turma/docente, cópia de quadro, criptografia, segurança) já estão operacionais e aprovadas.
  3. Catalogar o inventário formal de código **CONGELADO (INTOCÁVEL)** para evitar refatorações desnecessárias por agentes IA ou humanos.
* **Dependências:** `DEPENDS_ON: None`
* **Artefatos Gerados:**
  * `docs/STATUS_LOGICA_EXISTENTE.md`
* **Allowed Changes:**
  * `docs/STATUS_LOGICA_EXISTENTE.md`
* **Forbidden Changes:**
  * Todos os arquivos listados na seção "Inventário de Código Funcional e Congelado" de `docs/STATUS_LOGICA_EXISTENTE.md`.
* **Observações:** 34 de 35 testes aprovados (97,1%); a única falha identificada é um dado inválido na fixture de teste do `CursoServiceTest`, enquanto a regra de negócio da classe está íntegra e funcional.

---

### `TASK-001` — Desbloqueio no Perfil Dev e Alinhamento de CORS
* **Assignee:** `AI`
* **Status:** `DONE`
* **Objetivo:** 
  1. Garantir que requisições executadas com o perfil `dev` ativo não retornem erro `403 Forbidden`.
  2. Ajustar `ValidarAutorizacaoCursoUseCase` para ignorar checagens quando `app.security.authorization.enabled = false`.
  3. Atualizar o CORS em `ConfiguracaoSeguranca.java` (prod) e `ConfiguracaoSegurancaDev.java` para permitir a origem do Next.js (`http://localhost:3000`).
* **Dependências:** `DEPENDS_ON: None`
* **Artefatos Esperados:**
  * `ConfiguracaoSegurancaDev.java` ajustado.
  * `ConfiguracaoSeguranca.java` com CORS atualizado para porta 3000.
  * `ValidarAutorizacaoCursoUseCase.java` com salvaguarda limpa quando desativado.
* **Allowed Changes:**
  * `src/main/java/com/fatec/gini/web/config/ConfiguracaoSegurancaDev.java`
  * `src/main/java/com/fatec/gini/web/config/ConfiguracaoSeguranca.java`
  * `src/main/java/com/fatec/gini/web/config/SecurityFilter.java`
  * `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarAutorizacaoCursoUseCase.java`
  * `src/main/resources/application-dev.yml`
* **Forbidden Changes:**
  * `src/main/java/com/fatec/gini/domain/entities/*`
  * `src/main/resources/db/migration/*`
* **Observações:** CORS configurado no Spring Security para `http://localhost:3000`; no modo de autorização desativada, a auditoria usa o administrador padrão (`id = 1`).

---

### `TASK-002` — Rotação Atômica de RefreshToken para Múltiplos Logins
* **Assignee:** `AI`
* **Status:** `DONE`
* **Objetivo:**
  1. Corrigir o erro ao realizar múltiplos logins consecutivos com o mesmo usuário sem logout prévio.
  2. Implementar a rotação limpa no `RefreshTokenService`: ao invés de acionar `delete` assíncrono seguido de `save` (o que dispara violação de unicidade no banco), realizar remoção atômica com `deleteByUsuario` + flush ou reutilizar/atualizar o registro existente com novo token e nova data de expiração.
* **Dependências:** `DEPENDS_ON: None`
* **Artefatos Esperados:**
  * `RefreshTokenService.java` com método `createRefreshToken` transacional seguro e sem colisão de chave única.
  * `RefreshTokenRepository.java` com query explícita de deleção ou busca otimizada.
  * Teste unitário comprovando a execução de dois logins consecutivos com sucesso.
* **Allowed Changes:**
  * `src/main/java/com/fatec/gini/domain/services/RefreshTokenService.java`
  * `src/main/java/com/fatec/gini/infrastructure/repositories/RefreshTokenRepository.java`
  * `src/test/java/com/fatec/gini/domain/services/RefreshTokenServiceTest.java`
* **Forbidden Changes:**
  * `src/main/java/com/fatec/gini/domain/services/TokenService.java`
  * `src/main/java/com/fatec/gini/web/controller/AutenticacaoController.java`
* **Observações:** A rotação atualiza o registro existente do usuário na mesma transação, sem agendar remoção seguida de inserção.

---

### `TASK-003` — Configuração Global Jackson (snake_case) em Payloads e Query Params
* **Assignee:** `AI`
* **Status:** `DONE`
* **Objetivo:**
  1. Configurar a serialização/desserialização global do Jackson para adotar estritamente `snake_case` em todas as propriedades JSON de requisição e resposta.
  2. Ajustar `application.properties` (ou configuração equivalente) para ativar `PropertyNamingStrategies.SNAKE_CASE`.
  3. Garantir que os parâmetros de consulta (`@RequestParam`) nos controllers estejam mapeados com identificadores `snake_case` correspondentes (ex: `data_inicio`, `data_fim`).
* **Dependências:** `DEPENDS_ON: None`
* **Artefatos Esperados:**
  * `application.properties` configurado com a propriedade do Jackson.
  * DTOs e Controllers respondendo e recebendo campos no padrão `snake_case`.
* **Allowed Changes:**
  * `src/main/resources/application.properties`
  * `src/main/resources/application-*.yml`
  * `src/main/java/com/fatec/gini/web/controller/*.java` (apenas anotações `@RequestParam(name = "...")`)
* **Forbidden Changes:**
  * `src/main/java/com/fatec/gini/domain/entities/*`
* **Observações:** Jackson foi configurado globalmente com `SNAKE_CASE`; todos os parâmetros de consulta possuem `name` explícito no padrão `snake_case`.

---

### `TASK-004` — Padronização de Rotas RESTful em kebab-case Plural
* **Assignee:** `AI`
* **Status:** `DONE`
* **Objetivo:**
  1. Renomear as rotas dos controllers que divergem da convenção RESTful:
     * `/periodo_atividade_quadro` $\rightarrow$ `/periodos-atividade-quadro`
     * `/historicos-alteracoes` $\rightarrow$ `/historicos-versoes-alocacoes`
     * `/quadro-horarios` $\rightarrow$ `/quadros-horarios`
     * `/recurso-sala` $\rightarrow$ `/recursos-salas`
     * `/tipos-sala` $\rightarrow$ `/tipos-salas`
     * `/tipos-recurso` $\rightarrow$ `/tipos-recursos`
  2. Atualizar as referências em `ConfiguracaoSeguranca.java` (`requestMatchers`) e nos testes de integração.
* **Dependências:** `DEPENDS_ON: TASK-003`
* **Artefatos Esperados:**
  * Controllers atualizados com os novos `@RequestMapping`.
  * `ConfiguracaoSeguranca.java` atualizado para as novas rotas.
  * `TabelaAcesso.md` refletindo os novos caminhos.
* **Allowed Changes:**
  * `src/main/java/com/fatec/gini/web/controller/PeriodoAtividadeQuadroController.java`
  * `src/main/java/com/fatec/gini/web/controller/HistoricoAlteracaoController.java`
  * `src/main/java/com/fatec/gini/web/controller/QuadroHorarioController.java`
  * `src/main/java/com/fatec/gini/web/controller/RecursoSalaController.java`
  * `src/main/java/com/fatec/gini/web/controller/TipoSalaController.java`
  * `src/main/java/com/fatec/gini/web/controller/TipoRecursoController.java`
  * `src/main/java/com/fatec/gini/web/controller/TabelaAcesso.md`
  * `src/main/java/com/fatec/gini/web/config/ConfiguracaoSeguranca.java`
* **Forbidden Changes:**
  * Lógica interna dos métodos de serviço chamados pelos controllers.
* **Observações:** Escopo expandido com autorização para alinhar o endpoint auxiliar de enumerações e a matriz de segurança do perfil `test`.

---

### `TASK-005` — Padronização de Entidades Estrangeiras nos Request DTOs com LongDTO
* **Assignee:** `AI`
* **Status:** `DONE`
* **Objetivo:**
  1. Refatorar DTOs de requisição que utilizam IDs soltos (`Long cursoId`, `Long tipoSalaId`, etc.) para o padrão canônico com `LongDTO` (`LongDTO curso`, `LongDTO tipoSala`).
  2. Atualizar `AlocacaoRequest` para receber `LongDTO` em `turma`, `disciplina`, `sala`, `professor`, `blocoHorario`, `quadroHorario` e `usuarioAlteracao`.
  3. Atualizar `DisciplinaRequest` para receber `LongDTO curso` e `LongDTO tipoSala`.
  4. Atualizar os respectivos Mappers e Casos de Uso que convertem esses DTOs em Entidades.
* **Dependências:** `DEPENDS_ON: TASK-003`
* **Artefatos Esperados:**
  * `AlocacaoRequest.java`, `DisciplinaRequest.java` e mappers compatibilizados.
* **Allowed Changes:**
  * `src/main/java/com/fatec/gini/dto/alocacao/AlocacaoRequest.java`
  * `src/main/java/com/fatec/gini/dto/disciplina/DisciplinaRequest.java`
  * `src/main/java/com/fatec/gini/infrastructure/mappers/AlocacaoMapper.java`
  * `src/main/java/com/fatec/gini/infrastructure/mappers/DisciplinaMapper.java`
  * `src/main/java/com/fatec/gini/domain/services/usecase/write/CriarAlocacaoUseCase.java`
  * `src/main/java/com/fatec/gini/domain/services/usecase/write/AtualizarAlocacaoUseCase.java`
* **Forbidden Changes:**
  * DTOs de resposta (`*Response.java`) não estruturais.
  * Entidades de domínio JPA.
* **Observações:** DTOs `AlocacaoRequest` e `DisciplinaRequest` padronizados com `LongDTO` em formato canônico, preservando construtores e métodos auxiliares com `@JsonIgnore` para total retrocompatibilidade interna. `AlocacaoMapper` e `DisciplinaMapper` atualizados e validados com testes unitários passando.

---

### `TASK-006` — Exposição dos Endpoints do Motor de Quadro (Curso e Turma)
* **Assignee:** `AI`
* **Status:** `DONE`
* **Objetivo:**
  1. Criar o `MotorQuadroController` mapeado em `/motor-quadro`.
  2. Implementar rota `GET /motor-quadro/cursos/{curso_id}` consumindo `MotorQuadroHorario.construir(cursoId)`.
  3. Implementar rota `GET /motor-quadro/cursos/{curso_id}/turmas/{turma_id}` no service e controller, validando o pertencimento da turma ao curso e retornando `404` se for de outro curso.
  4. Integrar validação de autorização por curso via `ValidarAutorizacaoCursoUseCase` em produção.
  5. Adicionar anotações OpenAPI/Swagger documentando respostas `200`, `404` e `409`.
* **Dependências:** `DEPENDS_ON: TASK-004, TASK-005`
* **Artefatos Esperados:**
  * `MotorQuadroController.java` criado.
  * `MotorQuadroHorario.java` expandido com o método de consulta por turma.
  * DTOs `GradeTurmaResponse.java` e `HorarioTurmaResponse.java` criados/ajustados.
* **Allowed Changes:**
  * `src/main/java/com/fatec/gini/web/controller/MotorQuadroController.java` (novo)
  * `src/main/java/com/fatec/gini/domain/services/usecase/write/MotorQuadroHorario.java`
  * `src/main/java/com/fatec/gini/dto/grade/*`
  * `src/main/java/com/fatec/gini/web/config/ConfiguracaoSeguranca.java`
* **Forbidden Changes:**
  * `src/main/java/com/fatec/gini/domain/entities/QuadroHorario.java`
  * `src/main/java/com/fatec/gini/domain/entities/Turma.java`

* **Observações:** Endpoints de consulta por curso e turma implementados em `/motor-quadro`, com validação de autorização e rejeição de turma pertencente a outro curso.

---

### `TASK-007` — Módulo de Exportação do Motor de Quadro (PDF e Planilha Excel)
* **Assignee:** `AI`
* **Status:** `DONE`
* **Objetivo:**
  1. Adicionar dependências `openhtmltopdf-pdfbox` (ou flying-saucer) e `poi-ooxml` ao `pom.xml`.
  2. Criar serviço de exportação PDF `MotorQuadroPdfService` populando dinamicamente `templates/QuadroHorario.html` com Thymeleaf e gerando stream binário de PDF.
  3. Criar serviço de exportação Excel `MotorQuadroExcelService` gerando arquivo `.xlsx` com formatação e aba do curso.
  4. Expor os endpoints:
     * `GET /motor-quadro/cursos/{curso_id}/exportar/pdf`
     * `GET /motor-quadro/cursos/{curso_id}/exportar/planilha`
* **Dependências:** `DEPENDS_ON: TASK-006`
* **Artefatos Esperados:**
  * Serviços de exportação criados em `com.fatec.gini.domain.services.export`.
  * Endpoints no `MotorQuadroController` retornando bytes com `Content-Disposition: attachment`.
* **Allowed Changes:**
  * `pom.xml`
  * `src/main/java/com/fatec/gini/domain/services/export/*` (novo pacote)
  * `src/main/java/com/fatec/gini/web/controller/MotorQuadroController.java`
  * `src/main/resources/templates/QuadroHorario.html`
* **Forbidden Changes:**
  * Arquivos de entidades de banco de dados e migrações.

* **Observações:** Dependências, serviços PDF/XLSX, template Thymeleaf e endpoints implementados. Os endpoints foram validados em ambiente dev com respostas `200 OK` para PDF e planilha; a suíte Maven também foi executada sem falhas.

---

### `TASK-008` — Implementação do Fluxo de Recuperação de Senha por E-mail
* **Assignee:** `AI`
* **Status:** `DONE`
* **Objetivo:**
  1. Adicionar `spring-boot-starter-mail` no `pom.xml`.
  2. Criar migração Flyway `V1_0_2__tabela_password_reset_token.sql` e entidade JPA `PasswordResetToken`.
  3. Criar serviço de envio de e-mail e serviço de redefinição de senha com token de 15 minutos (configurável via env var).
  4. Criar endpoints públicos em `AutenticacaoController`:
     * `POST /auth/solicitar-recuperacao`
     * `POST /auth/redefinir-senha`
  5. Permitir acesso público às rotas em `ConfiguracaoSeguranca.java`.
* **Dependências:** `DEPENDS_ON: TASK-001, TASK-003`
* **Artefatos Esperados:**
  * Migração Flyway `V1_0_2__tabela_password_reset_token.sql`.
  * `PasswordResetToken.java` e repositório JPA.
  * `PasswordResetService.java` e `EmailService.java`.
  * DTOs `SolicitarRecuperacaoSenhaRequest` e `RedefinirSenhaRequest`.
* **Allowed Changes:**
  * `pom.xml`
  * `src/main/resources/db/migration/V1_0_2__tabela_password_reset_token.sql` (novo)
  * `src/main/java/com/fatec/gini/domain/entities/PasswordResetToken.java` (novo)
  * `src/main/java/com/fatec/gini/infrastructure/repositories/PasswordResetTokenRepository.java` (novo)
  * `src/main/java/com/fatec/gini/domain/services/PasswordResetService.java` (novo)
  * `src/main/java/com/fatec/gini/domain/services/EmailService.java` (novo)
  * `src/main/java/com/fatec/gini/dto/auth/*`
  * `src/main/java/com/fatec/gini/web/controller/AutenticacaoController.java`
  * `src/main/java/com/fatec/gini/web/config/ConfiguracaoSeguranca.java`
  * `src/main/resources/application*.yml`
* **Forbidden Changes:**
  * Mapeamento de outras entidades pré-existentes.

* **Observações:** Fluxo de solicitação e redefinição implementado com token SHA-256 de uso único e expiração configurável, envio via SMTP, migração Flyway e rotas públicas. Compilação e suíte Maven passaram; credenciais SMTP de produção permanecem sob responsabilidade da TASK-011.

---

### `TASK-009` — Adequação do Dockerfile, docker-compose.yml e Variáveis de Ambiente
* **Assignee:** `AI`
* **Status:** `DONE`
* **Objetivo:**
  1. Corrigir `docker-compose.yml` para passar as variáveis necessárias ao container `postgres:17` (`POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`).
  2. Adicionar serviço local do **Mailpit** (portas 1025 SMTP e 8025 Web UI) no `docker-compose.yml` para testes locais de e-mail.
  3. Atualizar `.env.exemple` com todas as novas variáveis (banco, JWT, SMTP, expirações, URL do frontend).
  4. Criar `.dockerignore` e otimizar `Dockerfile` para cache de compilação.
  5. Adicionar guia de execução no `README.md` de infraestrutura (ou seção específica em docs).
* **Dependências:** `DEPENDS_ON: None`
* **Artefatos Esperados:**
  * `docker-compose.yml` e `Dockerfile` corrigidos e funcionais.
  * `.dockerignore` e `.env.exemple` atualizados.
* **Allowed Changes:**
  * `docker-compose.yml`
  * `Dockerfile`
  * `.dockerignore` (novo)
  * `.env.exemple`
* **Forbidden Changes:**
  * `README.md` raiz.
  * Código fonte Java.

* **Observações:** Compose, Postgres, Mailpit, Dockerfile com cache de dependências, `.dockerignore` e `.env.exemple` implementados. A validação estrutural e o empacotamento Maven passaram; a execução real do Docker Compose depende de Docker instalado no ambiente.

---

### `TASK-010` — Atualização e Ampliação da Suíte de Testes Automatizados
* **Assignee:** `AI`
* **Status:** `DONE`
* **Objetivo:**
  1. Adaptar os testes existentes que falharem devido à serialização `snake_case` e novos DTOs com `LongDTO`.
  2. Adicionar testes unitários para `MotorQuadroHorario` (consulta por curso, consulta por turma, rejeição de turma incompatível).
  3. Adicionar testes de integração para login consecutivo (`RefreshTokenServiceTest`) e redefinição de senha (`PasswordResetServiceTest`).
  4. Garantir que `./mvnw test` execute com sucesso total (100% verde).
* **Dependências:** `DEPENDS_ON: TASK-002, TASK-004, TASK-005, TASK-006, TASK-008`
* **Artefatos Esperados:**
  * Suíte de testes em `src/test/java` atualizada e aprovada na compilação.
* **Allowed Changes:**
  * `src/test/java/com/fatec/gini/**/*`
* **Forbidden Changes:**
  * `src/main/resources/data.sql` (exceto para adicionar dados de apoio aos testes)

* **Observações:** Adicionados testes do motor de quadro (curso, turma e incompatibilidade) e do fluxo de recuperação de senha (criação, uso único, expiração e e-mail inexistente). A suíte completa passou com 42 testes, 0 falhas e 0 erros.

---

### `TASK-011` — Configuração de Provedor SMTP e Chaves de Produção
* **Assignee:** `HUMAN`
* **Status:** `BLOCKED`
* **Objetivo:**
  * Obter as credenciais oficiais de serviço SMTP institucional (host, porta, usuário, senha, remetente) ou AWS SES para deployment de produção e preencher o `.env` de homologação/produção.
* **Dependências:** `DEPENDS_ON: TASK-008`
* **Artefatos Esperados:**
  * Arquivo `.env` de produção devidamente preenchido e seguro.
* **Allowed Changes:**
  * Arquivo local `.env` (fora do versionamento Git).
* **Forbidden Changes:**
  * Qualquer arquivo de código versionado.

---

### `TASK-012` — Homologação Integrada com a Equipe de Frontend Next.js
* **Assignee:** `HUMAN`
* **Status:** `BLOCKED`
* **Objetivo:**
  * Apresentar o documento `FRONTEND_INTEGRATION.md` à equipe de frontend para alinhamento das tarefas de atualização dos tipos TypeScript, endpoints de serviço, telas de recuperação de senha e botões de exportação do quadro.
* **Dependências:** `DEPENDS_ON: TASK-010`
* **Artefatos Esperados:**
  * Validação formal de compatibilidade entre as duas equipes e agendamento dos deploys sincronizados.
* **Allowed Changes:**
  * `FRONTEND_INTEGRATION.md` (apenas para esclarecimentos ou refinamentos acordados).
* **Forbidden Changes:**
  * Código fonte backend.
