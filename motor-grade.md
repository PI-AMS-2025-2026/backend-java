# Relatório de verificação: motor de grade horária

## 1. Conclusão executiva

A funcionalidade solicitada **ainda não está implementada como dois endpoints de consulta de grade**.

O backend já possui a persistência e os relacionamentos necessários para montar a grade:

- `alocacao` relaciona turma, disciplina, sala, professor, dia, bloco horário e quadro horário;
- `quadro_horario` relaciona o curso e o período da grade, além de possuir `status`;
- `turma` possui `ano` e o curso ao qual pertence;
- `bloco_horario` possui `hora_inicio` e `hora_fim`;
- `disciplina` possui `nome`;
- `sala` possui `codigo`.

Entretanto, os endpoints existentes devolvem CRUD genérico, e não a estrutura pronta para a tela do documento AMS-ADS:

- `GET /alocacoes` retorna uma página de `AlocacaoResponse`;
- `GET /quadro-horarios` retorna uma página de `QuadroHorarioResponse`;
- não existe endpoint que selecione obrigatoriamente o quadro `ATIVO`, agrupe as alocações por turma/dia/bloco ou devolva células vazias e ocupadas da grade;
- não existe endpoint específico para consultar os horários de uma turma de um curso.

A especificação OpenAPI da aplicação em execução confirmou somente as rotas genéricas `/alocacoes` e `/quadro-horarios`, sem rotas de grade por curso ou turma.

O arquivo `AMS-ADS.pdf` não está presente neste workspace; portanto, o relatório usa a descrição fornecida e os campos existentes no modelo.

## 2. O que já está funcionando

### 2.1 Persistência

O modelo JPA representa corretamente os vínculos centrais:

- `Alocacao.quadroHorario` -> `quadro_horario.id_quadro_horario`;
- `Alocacao.turma` -> `turma.id_turma`;
- `Alocacao.disciplina` -> `disciplina.id_disciplina`;
- `Alocacao.sala` -> `sala.id_sala`;
- `Alocacao.blocoHorario` -> `bloco_horario.id_bloco_horario`;
- `Alocacao.diaSemana` -> `alocacao.dia_semana`.

As tabelas e chaves estrangeiras estão declaradas em `src/main/resources/db/migration/V1_0_0__criacao_tabelas_iniciais.sql`.

### 2.2 Consultas existentes

`AlocacaoRepository.buscarPorFiltros` já permite filtrar por turma, disciplina, sala, professor, dia, bloco, quadro e curso. Contudo:

1. a consulta retorna entidades individuais, sem agrupamento visual;
2. o filtro de curso não é um parâmetro público de `GET /alocacoes`; o serviço o substitui pelo curso autorizado;
3. não há condição `a.quadroHorario.status = ATIVO`;
4. não há ordenação por dia da semana e hora de início;
5. não há consulta específica que valide que a turma informada pertence ao curso informado.

### 2.3 Segurança existente

O serviço já possui `ValidarAutorizacaoCursoUseCase`, que limita coordenadores ao próprio curso e permite consulta administrativa conforme a configuração. A nova funcionalidade deve reutilizar esse mecanismo, e não criar uma regra paralela.

## 3. Lacunas encontradas

### 3.1 Ausência do endpoint da grade completa do curso

A necessidade é obter uma grade de um curso, com todas as turmas e divisões por ano. O CRUD atual de `QuadroHorarioResponse` não contém alocações, e o CRUD de alocações não contém uma estrutura de grade.

Deve ser criado um endpoint que:

- receba `cursoId`;
- selecione o quadro de horário `ATIVO` do curso;
- traga todas as turmas do curso que possuem ou podem possuir grade;
- ordene os blocos por `horaInicio`;
- agrupe as células por `turma.ano`, turma e dia da semana;
- preencha cada célula com disciplina, sala e intervalo de horário;
- retorne uma resposta vazia e válida quando não houver alocação, em vez de omitir silenciosamente a coluna/linha;
- rejeite curso inexistente e curso fora do escopo do usuário.

### 3.2 Ausência do endpoint de uma turma

Deve ser criado um endpoint que receba `cursoId` e `turmaId` e devolva somente os horários daquela turma no quadro ativo do curso.

O endpoint precisa impedir que uma turma de outro curso seja consultada usando um `cursoId` diferente. O vínculo deve ser validado no banco ou no serviço, não apenas confiado no cliente.

### 3.3 Seleção do quadro atual

O requisito de mostrar somente a grade atual exige uma regra explícita:

```text
quadro_horario.curso_id = cursoId
AND quadro_horario.status = ATIVO
```

Se houver mais de um quadro ativo para o mesmo curso e período, a operação deve falhar como inconsistência de dados ou a restrição de unicidade já existente deve ser reforçada. Não se deve escolher arbitrariamente o primeiro registro.

O período de atividade do quadro também deve ser considerado quando o domínio exigir uma grade ativa no período letivo corrente. O contrato deve documentar se a seleção usa apenas `status` ou `status` mais vigência de `periodo_atividade_quadro`.

## 4. Contratos recomendados

Os nomes abaixo evitam conflito com `GET /quadro-horarios/{id}` e deixam claro que são consultas derivadas para visualização.

### 4.1 Grade completa do curso

```http
GET /grades/cursos/{cursoId}
```

Resposta `200 OK`:

```json
{
  "curso": {
    "id": 1,
    "nome": "AMS (ADS)"
  },
  "quadroHorario": {
    "id": 10,
    "versao": 3,
    "status": "ATIVO"
  },
  "blocos": [
    {
      "id": 1,
      "horaInicio": "13:20:00",
      "horaFim": "14:10:00",
      "rotulo": "13h20-14h10"
    }
  ],
  "turmas": [
    {
      "id": 21,
      "codigo": "A",
      "ano": 4,
      "periodo": 1,
      "celulas": [
        {
          "diaSemana": "SEGUNDA",
          "blocoHorarioId": 1,
          "disciplina": {
            "id": 31,
            "nome": "Engenharia de Software"
          },
          "sala": {
            "id": 41,
            "codigo": "LAB INF 02"
          },
          "alocacaoId": 51
        }
      ]
    }
  ]
}
```

Recomendação: manter `blocos` e `turmas` separados da lista de células. Assim o frontend consegue montar todas as linhas e colunas, inclusive horários sem aula.

### 4.2 Horários de uma turma

```http
GET /grades/cursos/{cursoId}/turmas/{turmaId}
```

Resposta `200 OK`:

```json
{
  "curso": {
    "id": 1,
    "nome": "AMS (ADS)"
  },
  "turma": {
    "id": 21,
    "codigo": "A",
    "ano": 4,
    "periodo": 1
  },
  "quadroHorario": {
    "id": 10,
    "versao": 3,
    "status": "ATIVO"
  },
  "horarios": [
    {
      "diaSemana": "SEGUNDA",
      "blocoHorario": {
        "id": 1,
        "horaInicio": "13:20:00",
        "horaFim": "14:10:00"
      },
      "disciplina": {
        "id": 31,
        "nome": "Engenharia de Software"
      },
      "sala": {
        "id": 41,
        "codigo": "LAB INF 02"
      },
      "alocacaoId": 51
    }
  ]
}
```

Se não houver alocações para uma turma válida, retornar `200 OK` com `horarios: []`. Se a turma não existir ou não pertencer ao curso, retornar `404 Not Found` ou `400 Bad Request` conforme a convenção de erros do projeto; recomenda-se `404` para não revelar uma relação válida fora do escopo.

## 5. Alterações necessárias

### 5.1 DTOs de leitura

Criar DTOs próprios, sem reutilizar `AlocacaoResponse` como resposta final da grade. Sugestão de componentes:

- `GradeCursoResponse`;
- `GradeTurmaResponse`;
- `CelulaGradeResponse`;
- `BlocoGradeResponse`;
- `CursoGradeResumoResponse`;
- `TurmaGradeResumoResponse`.

Os DTOs devem expor somente os campos necessários à visualização: ids, nomes, ano, período, dia, horários, disciplina, sala e id da alocação. Não devem serializar entidades JPA nem coleções reversas.

### 5.2 Repositórios

Adicionar consultas read-only com projeção ou DTO, preferencialmente em `AlocacaoRepository` e, se necessário, em `TurmaRepository` e `BlocoHorarioRepository`.

Consulta conceitual da grade completa:

```jpql
SELECT a
FROM Alocacao a
JOIN FETCH a.quadroHorario q
JOIN FETCH q.curso c
JOIN FETCH a.turma t
JOIN FETCH a.disciplina d
JOIN FETCH a.sala s
JOIN FETCH a.blocoHorario b
WHERE c.id = :cursoId
  AND q.status = :statusAtivo
ORDER BY t.ano, t.periodo, t.id, a.diaSemana, b.horaInicio
```

Consulta conceitual da turma:

```jpql
SELECT a
FROM Alocacao a
JOIN FETCH a.quadroHorario q
JOIN FETCH q.curso c
JOIN FETCH a.turma t
JOIN FETCH a.disciplina d
JOIN FETCH a.sala s
JOIN FETCH a.blocoHorario b
WHERE c.id = :cursoId
  AND t.id = :turmaId
  AND t.curso.id = :cursoId
  AND q.status = :statusAtivo
ORDER BY a.diaSemana, b.horaInicio
```

Também é necessário buscar os blocos horários do eixo da grade, mesmo quando não existe alocação naquele bloco.

### 5.3 Serviço de consulta

Criar um serviço de leitura transacional, por exemplo `GradeService`, responsável por:

1. validar o acesso ao curso com `ValidarAutorizacaoCursoUseCase`;
2. localizar exatamente o quadro ativo;
3. validar a existência e o pertencimento da turma;
4. carregar blocos e alocações;
5. montar o agrupamento por ano/turma/dia/bloco;
6. ordenar dias e horários de forma determinística;
7. converter entidades/projeções em DTOs;
8. traduzir ausência de curso, turma ou quadro ativo para os erros HTTP padronizados.

O agrupamento deve usar ids como chave interna, e não nomes, pois nomes de disciplina, sala e turma podem mudar ou não ser únicos.

### 5.4 Controller

Criar um controller de consulta, por exemplo `GradeController`, com:

```java
GET /grades/cursos/{cursoId}
GET /grades/cursos/{cursoId}/turmas/{turmaId}
```

Adicionar `@Operation`, respostas documentadas para `200`, `404` e `403`, e exemplos no OpenAPI. A tabela de acesso também deve ser atualizada para ADMINISTRADOR e COORDENADOR.

### 5.5 Autorização de produção

O perfil `dev` libera todas as rotas e `application-dev.yml` desabilita a autorização de curso. Isso é aceitável somente para desenvolvimento. Em produção, os endpoints devem ficar protegidos pelas mesmas regras de `/alocacoes` e `/quadro-horarios`.

Não usar somente o `cursoId` recebido para autorização. Primeiro validar o usuário e depois aplicar o filtro do curso na consulta.

## 6. Regras de dados e casos de borda

- Nunca misturar alocações de quadros `INATIVO` com o quadro `ATIVO`.
- Não retornar alocação de uma turma cujo curso não seja o curso solicitado.
- Definir a ordem oficial de `DiaSemana`; o enum atual começa em `DOMINGO`, mas o quadro descrito começa em `SEGUNDA`.
- Ordenar os blocos por `horaInicio`, e não pelo id do bloco.
- Formatar o rótulo de tempo no backend somente se o frontend precisar desse texto; caso contrário, retornar `horaInicio` e `horaFim` como dados estruturados.
- Tratar duas alocações no mesmo par turma/dia/bloco como inconsistência, mesmo que as validações de escrita já tentem impedir isso.
- Definir o comportamento quando não existir quadro ativo: recomenda-se `404` com mensagem `Quadro horário ativo não encontrado para o curso`.
- Definir o comportamento para cursos sem turma ou sem alocação: recomenda-se `200` com listas vazias, desde que exista quadro ativo.
- Evitar `LazyInitializationException` e N+1 usando projeção, `JOIN FETCH` controlado ou uma consulta dedicada.
- Não retornar dados de professor, capacidade da sala ou metadados de auditoria se a tela não precisar deles.

## 7. Testes necessários

### Unitários

- agrupa uma alocação na célula correta;
- ordena dias e blocos corretamente;
- produz células para todas as turmas do curso;
- mantém horários vazios;
- ignora alocações de quadro inativo;
- rejeita turma pertencente a outro curso;
- retorna listas vazias para curso sem alocações;
- preserva o nome da disciplina e o código da sala.

### Integração/controller

- `GET /grades/cursos/{cursoId}` retorna o curso, quadro ativo, turmas, blocos e células;
- `GET /grades/cursos/{cursoId}/turmas/{turmaId}` retorna somente a turma solicitada;
- quadro inativo não aparece;
- curso inexistente retorna `404`;
- turma inexistente ou incompatível retorna `404`;
- coordenador não acessa curso diferente;
- administrador pode consultar curso autorizado;
- resposta não depende de paginação;
- resposta não dispara erro de serialização por entidades JPA ou relações circulares.

### Validação manual

Criar dados com pelo menos duas turmas, dois anos, dois dias, dois blocos, uma célula vazia, um quadro inativo e um quadro ativo. Conferir:

1. a grade mostra o nome do curso;
2. cada ano/turma aparece no cabeçalho correto;
3. cada célula mostra disciplina e sala;
4. os rótulos de horário estão em ordem crescente;
5. dados do quadro inativo não aparecem;
6. a consulta específica retorna exatamente os horários da turma escolhida.

## 8. Critérios de aceite

1. Existem exatamente duas rotas de consulta documentadas para os casos de uso descritos.
2. Ambas usam o quadro `ATIVO` vinculado ao curso solicitado.
3. A grade completa contém curso, quadro ativo, eixo de blocos, turmas e células.
4. A consulta de turma garante simultaneamente `turma.id = turmaId` e `turma.curso.id = cursoId`.
5. Disciplina, sala, dia e intervalo de horário são retornados sem que o frontend precise fazer novas consultas por célula.
6. O resultado é ordenado por ano/turma, dia e hora de início.
7. A autorização por curso é aplicada em produção.
8. Cursos, turmas ou quadros inexistentes têm respostas HTTP consistentes.
9. Há testes automatizados cobrindo seleção do quadro ativo, pertencimento da turma e montagem das células.
10. O OpenAPI, a tabela de acesso e exemplos de Postman são atualizados.

## 9. Ordem recomendada de implementação

1. Definir e versionar os DTOs de resposta e o contrato OpenAPI.
2. Adicionar as consultas read-only com filtro de curso, turma e `status = ATIVO`.
3. Implementar a seleção única do quadro ativo e as validações de pertencimento.
4. Implementar `GradeService` e o agrupamento das células.
5. Expor as duas rotas no controller.
6. Adicionar testes unitários e de integração.
7. Atualizar tabela de acesso, README e coleções Postman.
8. Executar a aplicação com dados representativos e comparar a resposta com o quadro AMS-ADS.

## 10. Referências do diagnóstico

- `src/main/java/com/fatec/gini/web/controller/AlocacaoController.java`: CRUD e listagem paginada de alocações.
- `src/main/java/com/fatec/gini/web/controller/QuadroHorarioController.java`: CRUD e cópia de quadros.
- `src/main/java/com/fatec/gini/infrastructure/repositories/AlocacaoRepository.java`: filtro genérico existente.
- `src/main/java/com/fatec/gini/infrastructure/repositories/QuadroHorarioRepository.java`: filtro por curso/período/status.
- `src/main/java/com/fatec/gini/dto/alocacao/AlocacaoResponse.java`: resposta plana de uma alocação.
- `src/main/java/com/fatec/gini/dto/quadroHorario/QuadroHorarioResponse.java`: resposta do quadro sem alocações.
- `src/main/java/com/fatec/gini/domain/services/usecase/read/ValidarAutorizacaoCursoUseCase.java`: autorização por curso.
- `src/main/resources/db/migration/V1_0_0__criacao_tabelas_iniciais.sql`: tabelas e chaves estrangeiras.