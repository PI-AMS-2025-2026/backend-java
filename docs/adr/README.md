## Architecture Decision Records

Architecture Decision Records (ADRs) são um padrão de documentação usado em engenharia de software para registrar decisões arquiteturais significativas. Cada ADR descreve uma decisão, seu contexto, alternativas consideradas e as consequências esperadas, formando um histórico rastreável das escolhas técnicas de um sistema.

### Principais fatos

* **Origem:** Proposto por Michael Nygard em 2011
* **Formato típico:** Arquivo de texto (Markdown) numerado sequencialmente
* **Finalidade:** Registrar e comunicar decisões de arquitetura
* **Benefício principal:** Transparência e rastreabilidade de decisões técnicas

### Contexto e propósito

ADRs surgiram como uma alternativa leve a documentos de arquitetura extensos e pouco atualizados. Eles promovem a prática de registrar o “porquê” por trás de cada decisão, permitindo que futuras equipes compreendam o raciocínio original e evitem repetir discussões passadas. São amplamente usados em times ágeis e projetos de software distribuídos.

### Estrutura comum

Um ADR costuma conter seções padronizadas: **Título**, **Status**, **Contexto**, **Decisão** e **Consequências**. Alguns modelos também incluem **Alternativas Consideradas** ou **Data**. O formato em Markdown facilita versionamento em repositórios Git, integrando a documentação com o código.

Exemplo:
```markdown
    # ADR 001 - Usar PostgreSQL

    ## Status
    Aceito

    ## Contexto
    Precisamos de um banco relacional...

    ## Decisão
    Vamos usar PostgreSQL

    ## Consequências
    + Forte consistência
    - Setup mais pesado
```
