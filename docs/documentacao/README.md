# Análise de Definições Consolidadas - Projeto GINI

Este documento resume exclusivamente as informações, regras de negócio, tecnologias e arquiteturas que já estão consolidadas e definidas como "certas" na documentação técnica do Projeto Integrador: **Grade Integrada de Navegação Institucional (GINI)**.

## 1. Informações Gerais do Projeto
*   **Nome do Sistema:** GINI (Grade Integrada de Navegação Institucional).
*   **Instituição Alvo:** Fatec Itu ("Dom Amaury Castanho").
*   **Público-Alvo (Atores):** Coordenadores e Administradores Acadêmicos.
*   **Objetivo Principal:** Centralizar e automatizar a criação, manutenção e validação dos quadros de horários acadêmicos, eliminando processos manuais, planilhas isoladas e retrabalhos.

## 2. Stack Tecnológico Decidido
*   **Back-end:** Java 21 com Spring Boot 3.5.14.
*   **Ecossistema Spring:** Spring Web, Spring Data JPA, Spring Validation e SpringDoc OpenAPI (Swagger para documentação de rotas).
*   **Front-end:** Next.js com TypeScript.
*   **Banco de Dados:** PostgreSQL (Relacional).
*   **Nuvem/Infraestrutura:** AWS (Amazon Web Services).
*   **Ferramentas de Apoio:** Figma (Prototipagem), GitHub Projects/Kanban (Gestão de Tarefas), Mermaid e PlantUML (Modelagem).

## 3. Arquitetura e Padrões de Projeto
O sistema adota uma **Arquitetura em Camadas (Layered Architecture)** estruturada da seguinte forma no Back-end (comunicação via API REST):
*   **Controllers (`web`):** Recebem as requisições HTTP e disponibilizam os recursos.
*   **Services / Use Cases (`domain.services`):** Isolam regras de negócios (cálculo de 12h, checagem de conflitos). Divididos em casos de uso de leitura e escrita.
*   **Repositories (`infrastructure.repositories`):** Acesso a dados utilizando Spring Data JPA.
*   **Entities (`domain`):** Representação dos objetos de domínio.
*   **DTOs (`dto`):** Objetos de transferência de dados para entrada/saída da API.
*   **Mappers (`infrastructure.mappers`):** Responsáveis pela conversão bidirecional entre DTOs e Entities.
*   **Exceptions (`web.exception`):** Estrutura própria e padronizada para tratamento de erros e respostas da API.

## 4. Regras de Negócio e Funcionalidades Fechadas (Requisitos Funcionais)
As seguintes regras já foram definidas como escopo obrigatório e certo para o sistema:
1.  **Prevenção de Conflitos (RF05):** O sistema é estritamente proibido de permitir alocações simultâneas para o mesmo professor, sala ou turma.
2.  **Regra de Descanso Interjornada (RF07):** É obrigatória a validação de um intervalo mínimo de 12 horas de descanso entre o fim da jornada de um professor e o início da próxima.
3.  **Sugestão de Ajustes (RF06):** O sistema deve ser capaz de identificar divergências e mostrar automaticamente horários incompatíveis.
4.  **Histórico e Auditoria (RF09):** Toda e qualquer alteração no quadro de horários gera um histórico rastreável contendo data, usuário, justificativa, valor antigo e novo valor.
5.  **Reaproveitamento de Grade (RF10):** Capacidade consolidada de copiar grades de semestres anteriores (trazendo apenas registros que seguem ativos).

## 5. Estrutura de Banco de Dados Definida
O Dicionário de Dados estabeleceu as seguintes tabelas (Entidades) e seus relacionamentos diretos:
*   **Acesso:** `TIPO_USUARIO`, `USUARIO`.
*   **Estrutura Acadêmica:** `CURSO`, `TURMA`, `DISCIPLINA`.
*   **Docentes:** `PROFESSOR_DISCIPLINA` (tabela associativa validando o que o professor pode lecionar), `DISPONIBILIDADE_PROFESSOR` (vinculada a dias e horários).
*   **Tempo:** `DIA_SEMANA`, `BLOCO_HORARIO`, `PERIODO_LETIVO`.
*   **Espaço Físico:** `TIPO_SALA`, `SALA`, `RECURSO`, `RECURSO_SALA`.
*   **Core do Sistema:** `QUADRO_HORARIO` (Container do semestre) e `ALOCACAO` (Onde aula, professor, sala, dia e horário se conectam).
*   **Auditoria:** `HISTORICO_ALTERACAO`.

## 6. Metodologia de Trabalho da Equipe
*   Divisão rígida em subequipes: Back-end, Front-end, Infraestrutura e Banco de Dados, Documentação, Testes.
*   Organização de demandas centralizada no GitHub através de *Issues* alocadas em colunas de um quadro Kanban ("A Fazer", "Em Andamento", "Concluído").


```mermaid
erDiagram
    TIPO_USUARIO {
        Long id_tipo_usuario PK
        String nome
    }
    
    USUARIO {
        Long id_usuario PK
        String nome
        String email
        String cidade
        String senha
        Boolean status
        LocalDateTime created_at
        LocalDateTime updated_at
        Long id_tipo_usuario FK
        Long id_curso FK
    }
    
    CURSO {
        Long id_curso PK
        String nome
        String periodicidade
        String status
        Integer duracao
    }
    
    TURMA {
        Long id_turma PK
        String codigo
        Integer periodo
        Integer ano
        Integer numero_alunos
        Long id_curso FK
    }
    
    DISCIPLINA {
        Long id_disciplina PK
        String nome
        Integer carga_horaria
        String tipo_disciplina
        Integer periodo
        String modalidade
        String cod_disciplina
        String cor
        Long id_curso FK
        Long id_tipo_sala FK
    }
    
    PROFESSOR_DISCIPLINA {
        Long id_professor_disciplina PK
        Long id_usuario FK
        Long id_disciplina FK
    }
    
    DISPONIBILIDADE_PROFESSOR {
        Long id_disponibilidade_professor PK
        Long id_usuario FK
        Long id_dia_semana FK
        Long id_horario FK
    }
    
    DIA_SEMANA {
        Long id_dia_semana PK
        String nome
    }
    
    HORARIO {
        Long id_horario PK
        LocalTime hora_inicio
        LocalTime hora_fim
        Integer duracao
    }
    
    PERIODO_LETIVO {
        Long id_periodo_letivo PK
        Integer ano
        Integer periodo
        LocalDate data_inicio
        LocalDate data_fim
        String status
    }
    
    QUADRO_HORARIO {
        Long id_quadro_horario PK
        Integer versao
        LocalDateTime data_criacao
        String status
        Long id_curso FK
        Long id_periodo_letivo FK
    }
    
    ALOCACAO {
        Long id_alocacao PK
        Long id_turma FK
        Long id_disciplina FK
        Long id_sala FK
        Long id_usuario FK
        Long id_dia_semana FK
        Long id_horario FK
        Long id_grade_horaria FK
    }
    
    TIPO_SALA {
        Long id_tipo_sala PK
        String nome
    }
    
    SALA {
        Long id_sala PK
        String codigo
        Integer capacidade
        Long id_tipo_sala FK
    }
    
    RECURSO {
        Long id_recurso PK
        String nome
        String tipo
    }
    
    RECURSO_SALA {
        Long id_recurso_sala PK
        Integer quantidade
        Long id_sala FK
        Long id_recurso FK
    }
    
    HISTORICO_ALTERACAO {
        Long id_historico_alteracao PK
        LocalDateTime data_alteracao
        String justificativa
        String campo_alterado
        String valor_antigo
        String valor_novo
        Long id_alocacao FK
        Long id_usuario FK
    }

    %% Relacionamentos
    TIPO_USUARIO ||--o{ USUARIO : "possui"
    CURSO ||--o{ USUARIO : "gerenciado_por"
    CURSO ||--o{ TURMA : "possui"
    CURSO ||--o{ DISCIPLINA : "possui"
    CURSO ||--o{ QUADRO_HORARIO : "possui"
    
    PERIODO_LETIVO ||--o{ QUADRO_HORARIO : "referente_a"
    TIPO_SALA ||--o{ DISCIPLINA : "requer"
    TIPO_SALA ||--o{ SALA : "classifica"
    
    SALA ||--o{ RECURSO_SALA : "possui"
    RECURSO ||--o{ RECURSO_SALA : "alocado_em"
    
    USUARIO ||--o{ PROFESSOR_DISCIPLINA : "leciona"
    DISCIPLINA ||--o{ PROFESSOR_DISCIPLINA : "ministrada_por"
    
    USUARIO ||--o{ DISPONIBILIDADE_PROFESSOR : "informa"
    DIA_SEMANA ||--o{ DISPONIBILIDADE_PROFESSOR : "ocorre_em"
    HORARIO ||--o{ DISPONIBILIDADE_PROFESSOR : "no_horario"
    
    QUADRO_HORARIO ||--o{ ALOCACAO : "contem"
    TURMA ||--o{ ALOCACAO : "frequenta"
    DISCIPLINA ||--o{ ALOCACAO : "oferecida_em"
    SALA ||--o{ ALOCACAO : "ocorre_em"
    USUARIO ||--o{ ALOCACAO : "ministrada_por"
    DIA_SEMANA ||--o{ ALOCACAO : "no_dia"
    HORARIO ||--o{ ALOCACAO : "no_bloco"
    
    ALOCACAO ||--o{ HISTORICO_ALTERACAO : "registra"
    USUARIO ||--o{ HISTORICO_ALTERACAO : "modificado_por"
```