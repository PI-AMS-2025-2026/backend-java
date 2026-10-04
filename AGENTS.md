# AGENTS.md — Contrato Operacional e Diretrizes de Execução para Agentes IA

> **Destinatário:** Agentes Autônomos de Codificação (Codex, Antigravity, Subagentes) e Desenvolvedores de Software  
> **Finalidade:** Estabelecer as regras de governança, políticas de preservação, limites de refatoração e protocolo de execução para o repositório backend GINI.  
> **Versão:** 1.0.0  
> **Data:** 24/09/2026  

---

## 1. Princípios Operacionais Fundamentais

Todo agente ou desenvolvedor que atuar neste repositório deve obedecer estritamente aos seguintes princípios:

### 1.1 Princípio da Preservação de Código Funcional
O projeto **já existe, compila e possui testes passando**.
* Se um módulo, entidade, serviço ou validador está funcional, atende às regras de negócio consolidadas em `ESCOPO.md` e não possui dívida técnica registrada nos relatos, ele é considerado **INTOCÁVEL**.
* É expressamente proibido reescrever classes funcionais sob o pretexto de "melhorar estilo", "modernizar sintaxe" ou "reorganizar arquitetura" sem uma `TASK` que autorize explicitamente tal ação.

### 1.2 Princípio da Menor Alteração Necessária (*Minimum Necessary Change*)
* Aplique estritamente o menor conjunto de alterações contíguas necessárias para cumprir o objetivo da tarefa.
* Não introduza dependências desnecessárias no `pom.xml`.
* Ao alterar um DTO, mapper ou repositório, certifique-se de que a alteração seja cirúrgica e preservada em compatibilidade reversa com o restante do domínio.

### 1.3 Ordem de Prioridade (Fontes da Verdade)
Em caso de ambiguidade durante a execução:
1. **Decisões Registradas na Entrevista de Elicitação** (incorporadas em `ESCOPO.md`);
2. **Relatos dos Desenvolvedores** (`docs/relatos/README.md`);
3. **Código Existente** (`src/main/java`);
4. **Documentação Legada** (`docs/documentacao/`).

---

## 2. Scope Guard (Guardas de Escopo)

Cada tarefa no `TASKS.md` define dois conjuntos obrigatórios de caminhos:
* **Allowed Changes:** Lista explícita de diretórios ou arquivos que podem ser alterados ou criados.
* **Forbidden Changes:** Arquivos que estão funcionando e que **JAMAIS** devem ser modificados no escopo daquela tarefa. O inventário definitivo de classes e validadores intocáveis está catalogado em [`docs/STATUS_LOGICA_EXISTENTE.md`](file:///c:/Users/felip/Desktop/PI/backend-java/docs/STATUS_LOGICA_EXISTENTE.md).

### Regras de Imposição do Scope Guard:
1. **Verificação Prévia:** Antes de abrir um arquivo para edição (`replace_file_content` ou `write_to_file`), o agente deve consultar a `TASK` correspondente em `TASKS.md` e verificar se o arquivo consta na lista de `Allowed Changes`.
2. **Bloqueio Automático:** Se o arquivo pretendido estiver listado em `Forbidden Changes` ou no inventário de [`docs/STATUS_LOGICA_EXISTENTE.md`](file:///c:/Users/felip/Desktop/PI/backend-java/docs/STATUS_LOGICA_EXISTENTE.md), a edição está **terminantemente vetada**.
3. **Resolução de Divergências:** Em caso de conflito entre legados e o código, o agente deve consultar [`docs/DIVERGENCIAS.md`](file:///c:/Users/felip/Desktop/PI/backend-java/docs/DIVERGENCIAS.md) antes de propor qualquer intervenção.
4. **Alterações Não Documentadas:** Se uma alteração imprevista for estritamente necessária em um arquivo fora do `Allowed Changes` para destravar um teste ou compilação, o agente deve interromper o processo, justificar a necessidade e registrar a alteração no backlog antes de prosseguir.
5. **🔒 Freeze Guard (OBRIGATÓRIO):** Antes de qualquer `replace_file_content` ou `write_to_file` (Overwrite), o agente DEVE executar o protocolo completo definido em [`.agents/rules/freeze-guard.md`](file:///c:/Users/felip/Desktop/PI/backend-java/.agents/rules/freeze-guard.md). Se o arquivo-alvo estiver no inventário de congelados, a edição está **proibida até que o usuário conceda aprovação explícita e textual**. Não há exceção a esta regra.

---

## 3. Diretrizes Técnicas Específicas para o GINI

### 3.1 Padronização de Rotas e Endpoints
* Todas as rotas RESTful nos Controllers devem seguir o padrão `kebab-case` plural (ex: `@RequestMapping("/periodos-atividade-quadro")`, `@RequestMapping("/quadros-horarios")`).
* Rotas com underscore (como `/periodo_atividade_quadro`) ou no singular (como `/recurso-sala`) são proibidas.

### 3.2 Serialização e Nomenclatura de Propriedades
* A configuração global do Jackson (`PropertyNamingStrategies.SNAKE_CASE`) rege a API.
* Não utilize anotações `@JsonProperty("...")` individuais a menos que haja um caso de exceção comprovado.
* Todos os parâmetros de `@RequestParam` em controllers devem ser nomeados explicitamente em `snake_case` (ex: `@RequestParam(name = "data_inicio", required = false) LocalDate dataInicio`).

### 3.3 Entidades Estrangeiras em DTOs de Request
* Todo DTO de requisição (`*Request`) que referencie outra entidade deve utilizar o tipo canônico `com.fatec.gini.dto.id.LongDTO` (ex: `LongDTO curso`, `LongDTO tipoSala`).
* IDs soltos/planos (ex: `Long cursoId`) em corpos de requisição `POST` ou `PUT` são proibidos.

### 3.4 Gerenciamento de Sessão e Tokens
* O `RefreshTokenService` deve realizar a limpeza/atualização atômica do token anterior do usuário, garantindo flush transacional para nunca violar a restrição única `@OneToOne` de `id_usuario`.
* O token de recuperação de senha gerado em `PasswordResetService` deve possuir expiração curta (15 minutos por padrão), persistência de auditoria e invalidação de uso único.

### 3.5 Segurança e Perfis de Execução
* No perfil `dev`:
  * `SecurityFilterChain` não deve aplicar bloqueios `403 Forbidden`.
  * `ValidarAutorizacaoCursoUseCase` não deve disparar `AccessDeniedException` quando a autorização estiver desativada (`app.security.authorization.enabled = false`).
* No perfil `prod`:
  * Acesso estritamente validado com base na matriz de `TabelaAcesso.md`.
  * Coordenadores confinados ao seu curso autorizado.

---

## 4. Checklist Obrigatório de Execução de Tarefas

Ao iniciar e finalizar qualquer `TASK`, o agente deve executar o seguinte checklist:

```text
[ ] 1. Identificar o ID da tarefa (TASK-xxx) e confirmar que o ASSIGNEE é AI.
[ ] 2. Checar os caminhos em Allowed Changes e certificar-se de não violar Forbidden Changes.
[ ] 3. Executar apenas a alteração mínima necessária para atingir o objetivo da tarefa.
[ ] 4. Rodar os testes unitários e de integração afetados pelo comando Maven.
[ ] 5. Verificar se a compilação do projeto permanece íntegra (mvn test-compile).
[ ] 6. Atualizar o status da tarefa no TASKS.md para DONE e registrar eventuais observações.
```

---

## 5. Proibições Expressas

* **NUNCA** modifique ou sobrescreva o arquivo `README.md` original do projeto.
* **NUNCA** faça commits diretos na branch principal (`main`/`master`) sem validação prévia da suíte de testes.
* **NUNCA** insira segredos, senhas ou tokens reais hardcoded no código; utilize sempre o arquivo de configuração `application-*.yml` e variáveis de ambiente com valores de fallback seguros para desenvolvimento.
* **NUNCA** altere contratos de API sem atualizar o arquivo `FRONTEND_INTEGRATION.md`.
