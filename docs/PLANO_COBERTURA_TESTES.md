# Plano de Cobertura de Testes do Backend

## Baseline

A cobertura foi medida com:

```powershell
./mvnw.cmd -Pdev verify
```

Relatorio: `target/site/jacoco/index.html`

Baseline atual:

| Medida | Cobertura |
| --- | ---: |
| Linhas | 18,68% |
| Instrucoes | 20,52% |
| Branches | 11,28% |
| Metodos | 18,90% |
| Classes | 34,83% |

## Progresso executado

### Lote 1 - Portao de autenticacao e seguranca

Status: **implementado**.

Testes adicionados:

- `AutenticacaoControllerTest`: login com payload `snake_case`, solicitacao de recuperacao, redefinicao e validacao de e-mail.
- `TokenServiceTest`: emissao, sessao, assinatura invalida e expiracao.
- `SecurityFilterTest`: sessao revogada.

Resultado apos o lote:

| Medida | Resultado |
| --- | ---: |
| Testes | 50 passando |
| Linhas | 21,05% |
| Branches | 11,65% |
| Metodos | 22,94% |

O proximo lote recomendado e o **Lote 2 - Alocacao e validadores**, por concentrar as regras de negocio com maior risco de regressao.

### Lote 2 - Alocacao e validadores

Status: **em execucao**.

Implementado ate agora:

- `CriarAlocacaoUseCaseTest`: caminho valido e falha de regra sem persistencia ou historico.
- `ValidarConflitosAlocacaoUseCaseTest`: conflitos de sala, turma e duplicidade em criacao/atualizacao.

Resultado atual: **80 testes passando**, com cobertura de linhas em **22,51%**, branches em **12,64%** e metodos em **23,67%**. Ainda faltam os cenarios individuais dos demais validadores e o caso de atualizacao antes de considerar este lote concluido.

### Lote 3 - Quadro e versionamento

Status: **em execucao**.

Implementado ate agora:

- `MotorQuadroHorarioTest`: ausencia de quadro ativo e mais de um quadro ativo.

O proximo passo e cobrir copia/versionamento e validar os bytes e headers dos exportadores PDF/XLSX.

O perfil Maven `dev` e o unico que ativa o JaCoCo. O build padrao continua sem instrumentacao; no CI, a etapa de cobertura deve usar explicitamente `./mvnw.cmd -Pdev verify`.

## Partes criticas sem cobertura suficiente

### P0 - Seguranca e autenticacao

- `AutenticacaoController`: login, refresh, logout e respostas de validacao.
- `SecurityFilter`: token ausente, token invalido, sessao revogada e refresh token inexistente.
- `TokenService`: emissao, expiracao e rejeicao de JWT.
- `ConfiguracaoSeguranca`: rotas publicas, roles ADMIN/COORDENADOR e endpoints de recuperacao.
- `PasswordResetService` e `EmailService`: fluxo de uso unico, expiracao, nao divulgacao de contas e falha de SMTP.

Risco: acesso indevido, sessao que nao pode ser revogada e recuperacao de senha quebrada.

### P0 - Alocacao e regras de negocio

- `CriarAlocacaoUseCase` e `AtualizarAlocacaoUseCase`.
- Validadores de conflito de sala, turma e professor.
- Validadores de carga horaria, disponibilidade, capacidade e tipo de sala.
- Validacao de lote e duplicidade.
- `ValidarQuadroHorarioValidoUseCase` e regras de interjornada.

Risco: grades invalidas, choque de recursos e violacao de regras academicas.

### P1 - Quadro horario

- `CopiarQuadroHorarioUseCase` e validacao de copia.
- `QuadroHorarioService` e transicoes de status/versao.
- `MotorQuadroHorario`: cenarios vazios, multiplos quadros ativos, dias/blocos e dados incompletos.
- `MotorQuadroPdfService` e `MotorQuadroExcelService`: bytes validos, headers, celulas vazias e dados acentuados.

Risco: perda de versao, exportacao incorreta e divergencia entre API e arquivo exportado.

### P1 - Persistencia e historico

- `RegistrarHistoricoVersaoAlocacaoUseCase` e `HistoricoVersaoAlocacaoService`.
- Repositorios com queries de filtros e ordenacao.
- Mappers dos recursos ainda sem teste direto: sala, recurso, turma, professor, periodo e quadro.
- Migracao `V1_0_2__tabela_password_reset_token.sql` em perfil PostgreSQL.

Risco: dados persistidos incorretamente e consultas divergentes do contrato da API.

### P2 - CRUD e contrato HTTP

- Controllers de usuario, turma, professor, sala, recurso, periodo, quadro e disciplina.
- Validacao `snake_case`, status HTTP 400/404/409/422 e payloads de erro.
- CORS e isolamento entre perfis `dev`, `test` e `prod`.

Risco: regressao de integracao com o frontend e respostas HTTP inconsistentes.

## Lotes de implementacao

### Lote 1 - Portao de autenticacao e seguranca

1. Criar testes `WebMvcTest` para autenticacao e recuperacao de senha.
2. Testar `SecurityFilter` com JWT valido, expirado, revogado e ausente.
3. Testar matriz de roles e rotas publicas.
4. Mockar `JavaMailSender` e cobrir falha de envio sem persistir estado inconsistente.

Aceite: fluxos publicos e protegidos respondem com status esperado; nenhum token revogado autentica uma requisicao.

### Lote 2 - Alocacao e validadores

1. Testar criacao e atualizacao validas.
2. Adicionar um teste por regra de rejeicao: sala, turma, professor, disponibilidade, carga, capacidade, tipo e duplicidade.
3. Cobrir lote parcialmente invalido e rollback transacional.
4. Cobrir mensagens e status das excecoes de negocio.

Aceite: cada regra critica possui caso positivo e negativo, e a suíte nao depende de ordem entre testes.

### Lote 3 - Quadro e versionamento

1. Testar copia de quadro e incremento de versao.
2. Testar bloqueio de copia invalida e quadro ativo duplicado.
3. Completar cenarios de `MotorQuadroHorario` e validar exportadores.
4. Comparar a estrutura da resposta JSON com a planilha e o PDF gerados.

Aceite: nenhuma alocacao e perdida ou duplicada durante copia/exportacao.

### Lote 4 - Persistencia e mapeamento

1. Criar testes de repositorio com H2 para filtros, ordenacao e joins principais.
2. Testar todos os mappers sem cobertura.
3. Executar migracoes Flyway em PostgreSQL de teste.
4. Validar constraints e rollback de transacoes.

Aceite: queries e migracoes executam em banco real de CI, alem do H2.

### Lote 5 - Contrato HTTP e regressao

1. Criar testes de controller para CRUD e erros padronizados.
2. Validar `snake_case` em request/response e query params.
3. Validar CORS e headers de download.
4. Adicionar smoke test dos endpoints essenciais.

Aceite: contrato documentado em `FRONTEND_INTEGRATION.md` permanece verde no CI.

## Estrategia de CI/CD

- Pull requests: executar `./mvnw.cmd test` para feedback rapido.
- Branch principal: executar `./mvnw.cmd -Pdev verify` e publicar `target/site/jacoco/index.html` como artifact.
- Depois do Lote 2: adicionar `jacoco:check` com threshold inicial de 30% de linhas e elevar gradualmente.
- Depois do Lote 4: executar uma matriz com H2 para unitarios e PostgreSQL para migracoes/repositorios.
- Nao usar cobertura como unico gate: cada lote deve manter testes de comportamento para regras criticas.
