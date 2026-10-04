# ADR-002: Arquitetura do Motor de Quadro Horário e Exportação Dual (PDF e Planilha)

## Status
Aprovado

## Contexto
O documento de diagnóstico `motor-grade.md` apontava que o sistema possuía a persistência de alocações e turmas, mas carecia de rotas especializadas para entregar a grade pronta para visualização. Adicionalmente, o relato #8 solicitava implementar a exportação do quadro horário, havendo um template HTML/CSS existente (`QuadroHorario.html`) otimizado para impressão horizontal (`@media print`).

## Decisão
1. **Exposição do Motor de Quadro:** Criar o `MotorQuadroController` sob o prefixo `/motor-quadro` com dois endpoints de consulta:
   * `GET /motor-quadro/cursos/{curso_id}`: Retorna a matriz completa do curso no quadro horário ativo, preenchendo explicitamente células vazias para manter a simetria da grade.
   * `GET /motor-quadro/cursos/{curso_id}/turmas/{turma_id}`: Retorna os horários filtrados para a turma solicitada, validando o pertencimento da turma ao curso (respondendo `404 Not Found` caso pertença a outro curso).
2. **Exportação Dual Exclusiva por Curso Completo:**
   * `GET /motor-quadro/cursos/{curso_id}/exportar/pdf`: O backend renderiza dinamicamente o template Thymeleaf `templates/QuadroHorario.html` e o converte para stream binário PDF via biblioteca `openhtmltopdf-pdfbox`, devolvendo o arquivo com `Content-Disposition: attachment`.
   * `GET /motor-quadro/cursos/{curso_id}/exportar/planilha`: O backend monta uma pasta de trabalho Excel (`.xlsx`) via Apache POI com formatação das células, horários e salas, retornando o arquivo formatado para download.

## Consequências
### Positivas:
* Elimina a necessidade de o frontend processar cálculos complexos de matriz ou realizar dezenas de requisições por célula.
* Reutiliza a diagramação visual já aprovada em `QuadroHorario.html`.
* Permite ao corpo acadêmico emitir e imprimir o quadro institucional oficial diretamente em PDF e planilha.

### Negativas / Mitigações:
* Adiciona dependências de manipulação de documentos (`openhtmltopdf-pdfbox`, `poi-ooxml`) ao `pom.xml`.
* Mitigação: Essas bibliotecas rodam de forma autocontida na JVM, sem dependência de binários externos do sistema operacional.
