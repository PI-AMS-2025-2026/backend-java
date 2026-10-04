# STATUS_LOGICA_EXISTENTE.md — Auditoria da Lógica Executada e Inventário de Código Intocável

> **Objetivo:** Auditar o funcionamento real da lógica de negócio já planejada e implementada, documentar os resultados dos testes automatizados e catalogar formalmente os componentes funcionais que estão **CONGELADOS (INTOCÁVEIS)** para evitar alterações desnecessárias.  
> **Status:** Auditado e Validado via Suíte de Testes  
> **Data:** 01/10/2026  

---

## 1. Resultado da Auditoria Automatizada (`mvnw test`)

A suíte oficial de testes unitários e de integração do projeto foi executada para verificar a integridade da lógica pré-existente:

* **Total de Testes Executados:** 35
* **Testes com Sucesso:** 34 (97,1% de aprovação)
* **Falhas / Erros:** 1 falha em fixture de teste (`CursoServiceTest`)

### 1.1 Análise Detalhada dos Módulos Validados

| Classe de Teste | Qtd. Testes | Status | Lógica de Negócio Validada e Funcional |
| :--- | :---: | :---: | :--- |
| `AlocacaoServiceTest` | 10 | **APROVADO** | Criação, listagem paginada, checagem de conflito de salas, choque de turmas, choque de docente e validação da interjornada de 12 horas. |
| `ValidarCargaHorariaMaximaProfessorUseCaseTest` | 2 | **APROVADO** | Validação da carga horária máxima semanal contratual do professor. |
| `ValidarCursoObrigatorioUsuarioUseCaseTest` | 2 | **APROVADO** | Exigência e validação da obrigatoriedade de vínculo a um curso para usuários com papel de Coordenador. |
| `DisciplinaServiceTest` | 4 | **APROVADO** | Regras de unicidade de código de disciplina, validação de carga horária e período acadêmico. |
| `UsuarioServiceTest` | 3 | **APROVADO** | Criptografia de senhas com BCrypt, regras de perfis e validações de e-mail institucional. |
| `SecurityFilterTest` | 7 | **APROVADO** | Interceptação e parsing do cabeçalho `Authorization: Bearer <token>`, validação de sessão ativa no repositório de refresh tokens e injeção do principal autenticado. |
| `GiniApplicationTests` | 1 | **APROVADO** | Inicialização completa do contexto Spring Boot, EntityManager JPA, Flyway migrations e conexão com banco de dados. |
| `CursoServiceTest` | 6 | **3 APROVADOS, 1 ERRO DE DADO DE TESTE** | A falha no método `deveAtualizarCursoExistente` ocorreu porque a massa do teste definiu um curso "Anual" com duração de 8 anos. A regra de negócio em [`CursoService.validarDuracao`](file:///c:/Users/felip/Desktop/PI/backend-java/src/main/java/com/fatec/gini/domain/services/CursoService.java#L185-L191) está **correta e funcional** (cursos anuais só admitem de 2 a 4 anos; cursos semestrais admitem de 4 a 10 semestres). O ajuste é estritamente na fixture do teste. |

---

## 2. Inventário de Código Funcional e Congelado (`Forbidden Changes`)

Com a validação comprovada dos componentes acima, fica expressamente proibida a refatoração, reescrita ou modificação estrutural dos seguintes arquivos e pacotes, que atendem integralmente às regras de negócio e estão consolidados:

### 2.1 Casos de Uso de Validação e Regras de Negócio (CONGELADOS)
* `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarConflitoSalaUseCase.java` (RF05)
* `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarConflitoTurmaUseCase.java` (RF05)
* `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarDisponibilidadeDocenteUseCase.java`
* `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarCargaHorariaDocenteUseCase.java` (RF07 - 12h)
* `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarCargaHorariaMaximaProfessorUseCase.java`
* `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarCapacidadeSalaUseCase.java`
* `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarDisciplinaTipoSalaUseCase.java`
* `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarSugestaoAutomaticaUseCase.java` (RF06)
* `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarCursoObrigatorioUsuarioUseCase.java`
* `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarDuplicidadeAlocacaoUseCase.java`
* `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarReferenciasObrigatoriasAlocacaoUseCase.java`
* `src/main/java/com/fatec/gini/domain/services/usecase/write/CopiarQuadroHorarioUseCase.java` (RF08)
* `src/main/java/com/fatec/gini/domain/services/usecase/write/RegistrarHistoricoVersaoAlocacaoUseCase.java` (RF09)

### 2.2 Entidades de Domínio JPA (CONGELADAS)
As seguintes entidades de banco de dados e suas respectivas tabelas Flyway estão operacionais, consistentes e **não devem ser alteradas**:
* `src/main/java/com/fatec/gini/domain/entities/Alocacao.java`
* `src/main/java/com/fatec/gini/domain/entities/BlocoHorario.java`
* `src/main/java/com/fatec/gini/domain/entities/Curso.java`
* `src/main/java/com/fatec/gini/domain/entities/DiaSemana.java`
* `src/main/java/com/fatec/gini/domain/entities/Disciplina.java`
* `src/main/java/com/fatec/gini/domain/entities/DisponibilidadeProfessor.java`
* `src/main/java/com/fatec/gini/domain/entities/HistoricoVersaoAlocacao.java`
* `src/main/java/com/fatec/gini/domain/entities/PeriodoAtividadeQuadro.java`
* `src/main/java/com/fatec/gini/domain/entities/Professor.java`
* `src/main/java/com/fatec/gini/domain/entities/ProfessorDisciplina.java`
* `src/main/java/com/fatec/gini/domain/entities/QuadroHorario.java`
* `src/main/java/com/fatec/gini/domain/entities/Recurso.java`
* `src/main/java/com/fatec/gini/domain/entities/RecursoSala.java`
* `src/main/java/com/fatec/gini/domain/entities/Sala.java`
* `src/main/java/com/fatec/gini/domain/entities/TipoRecurso.java`
* `src/main/java/com/fatec/gini/domain/entities/TipoSala.java`
* `src/main/java/com/fatec/gini/domain/entities/Turma.java`
* `src/main/java/com/fatec/gini/domain/entities/Usuario.java`
* `src/main/resources/db/migration/V1_0_0__criacao_tabelas_iniciais.sql`
* `src/main/resources/db/migration/V1_0_1__criacao_dados_iniciais.sql`

---

## 3. Delimitação Exata do Escopo de Alterações Autorizadas

Para cumprir o princípio da menor alteração necessária (*Minimum Necessary Change*), as únicas intervenções de código permitidas no backend são as explicitamente mapeadas abaixo:

1. **`RefreshTokenService.java`:** Ajustar a criação do refresh token para substituição atômica no banco, sem colidir com a restrição única (`TASK-002`).
2. **`ValidarAutorizacaoCursoUseCase.java` e `ConfiguracaoSegurancaDev.java`:** Ajustar para evitar `AccessDeniedException` quando a autorização estiver desativada em `dev` (`TASK-001`).
3. **Serialização e Rotas (`application.properties` e Controllers):** Adicionar estratégia `snake_case` global do Jackson e padronizar URLs dos controllers para `kebab-case` plural (`TASK-003` e `TASK-004`).
4. **DTOs de Requisição e Mappers:** Padronizar campos de chave estrangeira em `AlocacaoRequest`, `DisciplinaRequest` e `RecursoSalaRequest` para o tipo `LongDTO` (`TASK-005`).
5. **Motor de Quadro Horário:** Criar `MotorQuadroController.java` e adicionar o método de consulta filtrada por turma em `MotorQuadroHorario.java` (`TASK-006`).
6. **Módulo de Exportação:** Criar serviços de exportação em `domain.services.export` para geração de PDF e XLSX (`TASK-007`).
7. **Recuperação de Senha:** Criar entidade `PasswordResetToken`, migração `V1_0_2`, serviço de e-mail e endpoints em `AutenticacaoController` (`TASK-008`).
8. **Testes:** Corrigir fixture do `CursoServiceTest` e cobrir novos fluxos (`TASK-010`).
