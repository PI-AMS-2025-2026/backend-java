# FREEZE-GUARD — Protocolo Obrigatório de Verificação de Congelamento

> **Escopo:** Backend GINI — `c:\Users\felip\Desktop\PI\backend-java`  
> **Prioridade:** CRÍTICA — Esta regra é de observância obrigatória e sobrepõe qualquer outra instrução de tarefa.  
> **Versão:** 1.0.0

---

## INVENTÁRIO DE ARQUIVOS CONGELADOS (INTOCÁVEIS)

Os arquivos abaixo estão **CONGELADOS** conforme `docs/STATUS_LOGICA_EXISTENTE.md`.
Qualquer tentativa de edição exige **aprovação explícita do usuário** antes de qualquer ação.

### 🔒 Casos de Uso de Validação (CONGELADOS)
- `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarConflitoSalaUseCase.java`
- `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarConflitoTurmaUseCase.java`
- `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarDisponibilidadeDocenteUseCase.java`
- `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarCargaHorariaDocenteUseCase.java`
- `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarCargaHorariaMaximaProfessorUseCase.java`
- `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarCapacidadeSalaUseCase.java`
- `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarDisciplinaTipoSalaUseCase.java`
- `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarSugestaoAutomaticaUseCase.java`
- `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarCursoObrigatorioUsuarioUseCase.java`
- `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarDuplicidadeAlocacaoUseCase.java`
- `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarReferenciasObrigatoriasAlocacaoUseCase.java`
- `src/main/java/com/fatec/gini/domain/services/usecase/write/CopiarQuadroHorarioUseCase.java`
- `src/main/java/com/fatec/gini/domain/services/usecase/write/RegistrarHistoricoVersaoAlocacaoUseCase.java`

### 🔒 Entidades de Domínio JPA (CONGELADAS)
- `src/main/java/com/fatec/gini/domain/entities/Alocacao.java`
- `src/main/java/com/fatec/gini/domain/entities/BlocoHorario.java`
- `src/main/java/com/fatec/gini/domain/entities/Curso.java`
- `src/main/java/com/fatec/gini/domain/entities/DiaSemana.java`
- `src/main/java/com/fatec/gini/domain/entities/Disciplina.java`
- `src/main/java/com/fatec/gini/domain/entities/DisponibilidadeProfessor.java`
- `src/main/java/com/fatec/gini/domain/entities/HistoricoVersaoAlocacao.java`
- `src/main/java/com/fatec/gini/domain/entities/PeriodoAtividadeQuadro.java`
- `src/main/java/com/fatec/gini/domain/entities/Professor.java`
- `src/main/java/com/fatec/gini/domain/entities/ProfessorDisciplina.java`
- `src/main/java/com/fatec/gini/domain/entities/QuadroHorario.java`
- `src/main/java/com/fatec/gini/domain/entities/Recurso.java`
- `src/main/java/com/fatec/gini/domain/entities/RecursoSala.java`
- `src/main/java/com/fatec/gini/domain/entities/Sala.java`
- `src/main/java/com/fatec/gini/domain/entities/TipoRecurso.java`
- `src/main/java/com/fatec/gini/domain/entities/TipoSala.java`
- `src/main/java/com/fatec/gini/domain/entities/Turma.java`
- `src/main/java/com/fatec/gini/domain/entities/Usuario.java`

### 🔒 Migrações Flyway (CONGELADAS)
- `src/main/resources/db/migration/V1_0_0__criacao_tabelas_iniciais.sql`
- `src/main/resources/db/migration/V1_0_1__criacao_dados_iniciais.sql`

### 🔒 Documentação e Contrato (NUNCA MODIFICAR)
- `README.md`

---

## PROTOCOLO OBRIGATÓRIO DE VERIFICAÇÃO (PRE-EDIT CHECKLIST)

**ANTES de usar `replace_file_content` ou `write_to_file` (modo Overwrite) em qualquer arquivo do projeto backend, execute obrigatoriamente os seguintes passos na ordem:**

### PASSO 1 — Verificar se o arquivo está CONGELADO

Compare o caminho do arquivo alvo com o inventário acima.

**Se o arquivo ESTÁ na lista de congelados:**

> ⛔ **PARAR IMEDIATAMENTE.** Não fazer nenhuma edição.  
> Emitir a seguinte mensagem obrigatória ao usuário:
>
> ---
> **🔒 ARQUIVO CONGELADO — APROVAÇÃO NECESSÁRIA**
>
> O arquivo `[NOME_DO_ARQUIVO]` está listado como **CONGELADO** em `docs/STATUS_LOGICA_EXISTENTE.md`.
>
> **Motivo da alteração solicitada:** [descrever o motivo técnico detalhado]
>
> **Impacto estimado:** [listar quais testes, serviços ou fluxos podem ser afetados]
>
> **Risco:** [BAIXO / MÉDIO / ALTO]
>
> ❓ **Você autoriza explicitamente esta alteração?**  
> Responda **SIM, pode alterar** para prosseguir, ou **NÃO** para cancelar.
>
> ---
>
> Só prosseguir com a edição após receber confirmação textual explícita do usuário.

**Se o arquivo NÃO está na lista de congelados:**

Continuar para o PASSO 2.

---

### PASSO 2 — Verificar a TASK correspondente em `TASKS.md`

- Identificar a `TASK-xxx` que autoriza a alteração.
- Confirmar que o arquivo está listado em `Allowed Changes` da TASK.
- Se o arquivo não estiver em `Allowed Changes` e a alteração for necessária para destravar compilação ou teste, **parar e justificar** ao usuário antes de prosseguir.

---

### PASSO 3 — Aplicar Mínima Alteração Necessária

- Editar **somente** o trecho estritamente necessário para cumprir o objetivo da TASK.
- Não reformatar, não reorganizar imports, não alterar estilo.
- Não remover comentários ou Javadoc existentes.

---

## REGRAS ABSOLUTAS

| ❌ PROIBIDO | ✅ PERMITIDO |
|:---|:---|
| Editar arquivos congelados sem aprovação explícita | Editar arquivos fora do inventário de congelados com TASK válida |
| Refatorar por "melhoria de estilo" ou "limpeza" | Criação de novos arquivos dentro do escopo da TASK |
| Modificar migrações Flyway existentes | Criar novas migrações com versão maior (ex: `V1_0_2__...`) |
| Alterar entidades JPA congeladas | Criar novas entidades para novas funcionalidades |
| Sobrescrever `README.md` | Criar novos arquivos de documentação |
| Alterar contratos de API sem atualizar `FRONTEND_INTEGRATION.md` | Atualizar `FRONTEND_INTEGRATION.md` junto com o controller |

---

## EXEMPLO DE FLUXO CORRETO

```
Tarefa: Adicionar campo X em AlocacaoRequest.java

1. Verificar: AlocacaoRequest.java está congelado? → NÃO (é um DTO de request)
2. Verificar TASKS.md: TASK-005 lista AlocacaoRequest em Allowed Changes? → SIM
3. Aplicar alteração mínima: adicionar apenas o campo X com LongDTO
4. Não reformatar o restante do arquivo
5. ✅ Prosseguir
```

```
Tarefa: Otimizar ValidarConflitoSalaUseCase.java

1. Verificar: ValidarConflitoSalaUseCase.java está congelado? → SIM 🔒
2. PARAR. Emitir pedido de aprovação ao usuário.
3. Aguardar resposta explícita: "SIM, pode alterar"
4. Só então prosseguir (se aprovado)
```
