# GINI- Backend

Backend do Projeto Integrador desenvolvido para automatizar a criação, validação e gerenciamento de grades horárias acadêmicas da Fatec Itu.

## Tecnologias Utilizadas

* Java 21
* Spring Boot 3.5.14
* Spring Web
* Spring Data JPA
* Spring Validation
* Spring Security
* PostgreSQL (produção)
* H2 Database (desenvolvimento)
* SpringDoc OpenAPI (Swagger)
* Maven

## Objetivo do Projeto

O sistema tem como objetivo auxiliar coordenadores e administradores acadêmicos no processo de montagem da grade horária, automatizando validações e reduzindo conflitos relacionados a:

* Disponibilidade de professores
* Conflitos de horários
* Capacidade de salas
* Compatibilidade entre disciplinas e salas
* Regras de descanso entre jornadas
* Controle de carga horária docente

## Funcionalidades

### Cadastros Acadêmicos

* Usuários
* Tipos de Usuário
* Cursos
* Disciplinas
* Turmas
* Professor x Disciplina

### Infraestrutura Acadêmica

* Salas
* Tipos de Sala
* Recursos
* Recursos x Sala
* Disponibilidade de Professores
* Horários
* Dias da Semana
* Períodos Letivos

### Motor da Grade Horária

* Grade Horária
* Alocação de aulas
* Validação de conflitos
* Regra de 12h entre jornadas
* Controle de carga horária
* Histórico de alterações
* Cópia de grades anteriores

## Arquitetura

O projeto segue uma arquitetura em camadas e está organizado em pacotes coerentes com responsabilidades:

```text
controller -> service/usecase -> repository -> database
```

### Pacotes principais

- `com.fatec.horario.web.controller`: endpoints REST (controllers)
- `com.fatec.horario.domain.services`: serviços e casos de uso (subpacotes `usecase.read` e `usecase.write`)
- `com.fatec.horario.domain.entities`: entidades JPA do domínio
- `com.fatec.horario.infrastructure.repositories`: repositórios Spring Data JPA
- `com.fatec.horario.infrastructure.mappers`: mappers/converters entre DTOs e entidades
- `com.fatec.horario.dto`: DTOs (separados por subpacotes por área funcional)
- `com.fatec.horario.web.config`: configurações da aplicação (ex.: segurança)
- `com.fatec.horario.web.exception`: tratamento global de exceções

Essa organização reflete os controllers, services, use cases e repositórios presentes no código.

## Como Executar o Projeto

### Pré-requisitos

* Java 21
* Maven 3.9+

### Clonar o Repositório

```bash
git clone <url-do-repositorio>
```

### Entrar na Pasta

```bash
cd horario
```

Linux / macOS:
```bash
./mvnw spring-boot:run
```

Windows (PowerShell / CMD):
```powershell
mvnw.cmd spring-boot:run
```

Ou, se preferir usar o Maven instalado globalmente:
```bash
mvn spring-boot:run
```

## Acesso Local

### API

```text
http://localhost:8080
```

### Swagger

```text
http://localhost:8080/swagger-ui.html
```

### H2 Console

```text
http://localhost:8080/h2-console
```

## Configuração do H2

Exemplo padrão:

```text
JDBC URL: jdbc:h2:mem:testdb
User Name: sa
Password:
```

## Regras de Negócio Implementadas

O sistema possui validações para:

* Disponibilidade do professor
* Conflito de horário do professor
* Conflito de sala
* Conflito de turma
* Controle de carga horária diária
* Intervalo mínimo de 12 horas
* Compatibilidade de sala
* Capacidade física da sala
* Professor apto para disciplina
* Integridade referencial
* Prevenção de duplicidade

## Segurança

O sistema utiliza Spring Security para:

* Autenticação
* Controle de acesso por perfil
* Proteção de endpoints
* Criptografia de senha

Perfis previstos:

* ADMINISTRADOR
* COORDENADOR
* PROFESSOR
* VISUALIZACAO

## Documentação da API

A documentação é gerada automaticamente utilizando SpringDoc OpenAPI.

Após iniciar a aplicação:

```text
http://localhost:8080/swagger-ui.html
```

## Banco de Dados

Durante o desenvolvimento está sendo utilizado H2 Database.

Futuramente o projeto poderá utilizar PostgreSQL.

## Padrões Utilizados

* REST API
* DTO Pattern
* Service Layer
* Repository Pattern
* Bean Validation
* Tratamento Global de Exceções

## Status do Projeto

Projeto em desenvolvimento.

## Equipe

Projeto Integrador — Fatec Itu
Análise e Desenvolvimento de Sistemas AMS 2025-2026


## Dependencias:
Lombok (Developer Tools)Spring Web (Web)Spring Boot DevTools (Developer Tools)SpringDoc OpenAPI (Web)Thymeleaf (Template Engines)Apache Freemarker (Template Engines)Spring Security (Security)Spring Data JPA (SQL)Flyway Migration (SQL)H2 Database (SQL)PostgreSQL Driver (SQL)Validation (I/O)