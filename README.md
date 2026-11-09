# GINI- Backend

Backend do Projeto Integrador desenvolvido para automatizar a criação, validação e gerenciamento de grades horárias acadêmicas da Fatec Itu.

## Tecnologias Utilizadas

* Java 21
* Spring Boot 4.1.0
* Spring Web
* Spring Data JPA
* Spring Validation
* Spring Security
* PostgreSQL (produção)
* H2 Database (desenvolvimento)
* Flyway (migrations)
* Spring Boot Actuator (healthcheck)
* SpringDoc OpenAPI (Swagger)
* Maven (wrapper `mvnw`)
* Docker / Docker Compose

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
* Regra de 11h entre jornadas
* Controle de carga horária
* Histórico de alterações
* Cópia de grades anteriores

## Arquitetura

O projeto segue uma arquitetura em camadas e está organizado em pacotes coerentes com responsabilidades:

```text
controller -> service/usecase -> repository -> database
```

### Pacotes principais

- `com.fatec.gini.web.controller`: endpoints REST (controllers)
- `com.fatec.gini.domain.services`: serviços e casos de uso (subpacotes `usecase.read` e `usecase.write`)
- `com.fatec.gini.domain.entities`: entidades JPA do domínio
- `com.fatec.gini.infrastructure.repositories`: repositórios Spring Data JPA
- `com.fatec.gini.infrastructure.mappers`: mappers/converters entre DTOs e entidades
- `com.fatec.gini.dto`: DTOs (separados por subpacotes por área funcional)
- `com.fatec.gini.web.config`: configurações da aplicação (ex.: segurança)
- `com.fatec.gini.web.exception`: tratamento global de exceções

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
cd backend-java
```

### Executar com H2 (perfil `dev`, sem Docker)

Este é o fluxo recomendado para o desenvolvimento individual: banco H2 em
memória, Flyway desabilitado e `data.sql` carregado automaticamente.

Linux / macOS:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Windows (PowerShell / CMD):
```powershell
mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

> O perfil `dev` é o padrão quando `SPRING_PROFILES_ACTIVE` não está definido,
> portanto `./mvnw spring-boot:run` também funciona.

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
* Intervalo mínimo de 11 horas
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

## Documentação da API nos ambientes hospedados

O Swagger UI e a especificação OpenAPI são controlados por uma única variável de
ambiente, com **default fechado**:

| `SWAGGER_ENABLED` | Swagger UI | `/v3/api-docs` | Rotas da documentação |
|---|---|---|---|
| ausente ou `false` (default) | desabilitado (404) | desabilitado (404) | exigem autenticação |
| `true` | publicado | publicado | liberadas na aplicação |

Internamente a variável alimenta a propriedade `app.swagger.enabled`, que é o
interruptor único usado por três pontos: as propriedades do springdoc, o
`OpenApiConfig` (metadados e esquema `bearerAuth`) e a cadeia de segurança
`documentacaoFilterChain` em `ConfiguracaoSeguranca`.

### Qual ambiente deve ligar

| Ambiente | `APP_ENVIRONMENT` | `SWAGGER_ENABLED` |
|---|---|---|
| Desenvolvimento | `dev` | `true` |
| Homologação (staging) | `staging` | `false` |
| Produção | `production` | **`false`** |

Ligar `SWAGGER_ENABLED=true` com `APP_ENVIRONMENT=production` **impede a
aplicação de subir**, com mensagem explícita. É uma trava contra erro de
configuração: a exposição acidental de `/v3/api-docs` em produção passa a ser
uma falha de deploy visível em vez de um vazamento silencioso.

A motivação de ligar no ambiente de desenvolvimento é permitir que o time de
frontend estude e exercite os endpoints sem subir o backend na própria máquina.

### Autenticação é responsabilidade do proxy

Quando `SWAGGER_ENABLED=true`, as rotas da documentação ficam **sem
autenticação na aplicação**. Isso é deliberado: a proteção pertence ao proxy
reverso configurado no repositório de infraestrutura, por uma destas formas
(da mais simples à mais restrita):

* **Basic Auth** no proxy, cobrindo `/swagger-ui*` e `/v3/api-docs*`;
* **allowlist de IP** (ex.: a rede da faculdade);
* **VPN** / rede privada.

Consequência operacional: com a documentação ligada, **o contêiner não deve ser
publicado diretamente na internet** — todo o acesso tem de passar pelo proxy.
O backend já envia `server.forward-headers-strategy=framework`, de modo que
respeita `X-Forwarded-Proto` e `X-Forwarded-Host` e o Swagger UI monta as URLs
com o host externo, e não com o endereço interno do contêiner.

### Por que não deixar aberto em produção

`/v3/api-docs` entrega o contrato completo em JSON — todos os endpoints,
parâmetros, DTOs e regras de validação. Mesmo sem credenciais, é um mapa pronto
da API. Em produção o default `false` deve ser mantido.

### Pontos de atenção

* **`JWT_SECRET` precisa ser diferente em cada ambiente.** Se desenvolvimento e
  produção compartilharem o segredo, um token obtido via Swagger no ambiente de
  desenvolvimento passa a valer em produção.
* **O ambiente de desenvolvimento não deve conter dados pessoais reais**
  (professores, e‑mails, usuários) — apenas dados de seed. Além do risco de
  exposição, há implicações de LGPD.
* O botão **"Authorize"** do Swagger UI espera o token JWT obtido em
  `POST /auth/login`.

## Isolamento entre ambientes

Como os três ambientes hospedados compartilham o perfil `prod`, a aplicação faz
duas verificações na inicialização para que uma configuração equivocada não
passe silenciosamente. Ambas valem **apenas no perfil `prod`** — dev e test
seguem usando segredo fraco e padrão para facilitar a execução local.

### Emissor dos tokens por ambiente

O emissor (`iss`) do JWT é `fatec-gini-${APP_ENVIRONMENT}` e **é validado na
verificação do token**. Consequência: um token emitido no ambiente de
desenvolvimento é recusado em produção **mesmo que os dois compartilhem o mesmo
`JWT_SECRET`**.

Isso não substitui ter segredos distintos por ambiente — apenas remove a
consequência mais grave de um segredo compartilhado por engano. Ao alterar
`APP_ENVIRONMENT`, os tokens já emitidos naquele ambiente deixam de valer e os
usuários precisam autenticar novamente.

### Validação do `JWT_SECRET`

A aplicação recusa subir se o segredo:

* estiver vazio;
* tiver menos de 32 caracteres;
* for um valor reconhecidamente de exemplo (`gini`, `CHANGE_ME`, `dummy`, …).

```bash
# gera um valor adequado
openssl rand -hex 32
```

Note que **nenhuma instância consegue verificar se o próprio segredo é igual ao
de outro ambiente** — isso não é observável de dentro da aplicação. Garantir
segredos distintos é responsabilidade do gerenciador de secrets (GitHub
Environments, no caso dos workflows).

### O que continua fora do alcance deste repositório

* **Dados pessoais reais no ambiente de desenvolvimento.** As migrations criam
  apenas o administrador inicial; o que mais é carregado no banco é decisão
  operacional. Se um dump de produção for copiado para o ambiente de
  desenvolvimento, nenhuma verificação daqui detecta.
* **O serviço estar acessível diretamente pela internet.** É topologia de rede,
  definida no repositório de infraestrutura.
* **A autenticação no proxy reverso** para as rotas de documentação.

## Banco de Dados

| Perfil | Banco | Flyway | Carga inicial |
|---|---|---|---|
| `dev` | H2 em memória | desabilitado | `data.sql` |
| `test` | H2 em memória | desabilitado | `data.sql` |
| `prod` | PostgreSQL 17 | habilitado | migrations `db/migration` |

Os ambientes hospedados (desenvolvimento, homologação e produção) usam o perfil
`prod` com PostgreSQL. O perfil `dev` com H2 existe apenas para a execução local
individual.

No perfil `prod` o schema é propriedade do Flyway: `spring.jpa.hibernate.ddl-auto`
é `validate` e `spring.sql.init.mode` é `never`, para que o `data.sql` não
concorra com as migrations.

### Migrations

```text
src/main/resources/db/migration/
├── V1_0_0__criacao_tabelas_iniciais.sql   -- estrutura (DDL PostgreSQL)
├── V1_0_1__criacao_dados_iniciais.sql     -- usuário administrador inicial
└── V1_0_2__padronizacao_dia_semana.sql    -- dia_semana: smallint -> varchar
```

## Executando com Docker

O `docker-compose.yml` deste repositório é destinado **apenas ao
desenvolvimento local** (backend + PostgreSQL + Mailpit). O deploy dos
ambientes hospedados é responsabilidade do repositório de infraestrutura.

O **Mailpit** captura os e-mails do fluxo de recuperação de senha: nada sai da
máquina e as mensagens ficam visíveis em <http://localhost:8025>.

### Pré-requisitos

* Docker 24+ com Docker Compose v2

### Subindo o ambiente

```bash
cp .env.example .env
# edite o .env e substitua os valores CHANGE_ME.
# JWT_SECRET precisa de 32+ caracteres: openssl rand -hex 32
docker compose up -d --build
```

> O `JWT_SECRET` é validado na inicialização: valores vazios, com menos de 32
> caracteres ou reconhecidamente de exemplo (como `CHANGE_ME`) impedem a
> aplicação de subir. Ver [Isolamento entre ambientes](#isolamento-entre-ambientes).

A primeira subida leva alguns minutos (compilação da aplicação na imagem).
Acompanhe até os dois serviços ficarem `healthy`:

```bash
docker compose ps
docker compose logs -f backend
```

O log do backend deve mostrar o Flyway aplicando as migrations `V1_0_0` e
`V1_0_1`. Em seguida:

```bash
curl -f http://localhost:8080/actuator/health
# {"status":"UP"}
```

O Swagger fica disponível em `http://localhost:8080/swagger-ui.html`, porque
este compose define `SWAGGER_ENABLED=true`. Nos ambientes hospedados o default
é fechado — ver
[Documentação da API nos ambientes hospedados](#documentação-da-api-nos-ambientes-hospedados).

### Variáveis de ambiente

Todas estão documentadas em `.env.example`. As obrigatórias usam a sintaxe
`${VAR:?mensagem}` no compose, de modo que a subida falha com erro explícito se
alguma estiver ausente.

| Variável | Descrição |
|---|---|
| `POSTGRES_DB` | Nome do banco |
| `POSTGRES_USER` | Usuário do banco |
| `POSTGRES_PASSWORD` | Senha do banco |
| `DB_PORT` | Porta publicada no host (padrão `5432`) |
| `JWT_SECRET` | Segredo de assinatura dos tokens JWT |
| `CORS_ALLOWED_ORIGINS` | Origens autorizadas (ver pendência abaixo) |
| `SWAGGER_ENABLED` | Publica o Swagger UI e `/v3/api-docs` (default da aplicação: `false`) |
| `APP_ENVIRONMENT` | Identifica o ambiente: `dev`, `staging` ou `production` (default: `production`) |
| `SMTP_HOST` / `SMTP_PORT` | Servidor SMTP (no compose, aponta para o Mailpit) |
| `SMTP_USERNAME` / `SMTP_PASSWORD` | Credenciais SMTP (vazias com o Mailpit) |
| `SMTP_AUTH` / `SMTP_STARTTLS` | Flags de autenticação e TLS do SMTP |
| `SMTP_FROM` | Remetente dos e-mails |
| `FRONTEND_URL` | Base do link enviado no e-mail de recuperação de senha |
| `PASSWORD_RESET_EXPIRATION_MINUTES` | Validade do token de recuperação (default `15`) |
| `MAILPIT_SMTP_PORT` / `MAILPIT_WEB_PORT` | Portas publicadas pelo Mailpit (`1025` e `8025`) |

Nos ambientes hospedados o backend recebe, conforme o contrato entre
repositórios: `SPRING_PROFILES_ACTIVE=prod`, `SPRING_DATASOURCE_URL`,
`SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET` e
`CORS_ALLOWED_ORIGINS`.

O fluxo de recuperação de senha acrescenta variáveis **obrigatórias** no perfil
`prod`, sem valor padrão: `SMTP_HOST`, `SMTP_USERNAME`, `SMTP_PASSWORD`,
`SMTP_FROM` e `FRONTEND_URL`. Sem elas a aplicação não sobe. São opcionais
`SMTP_PORT` (587), `SMTP_AUTH` (true), `SMTP_STARTTLS` (true) e
`PASSWORD_RESET_EXPIRATION_MINUTES` (15).

Além dessas, o backend aceita duas variáveis opcionais:
`SWAGGER_ENABLED` (default `false`) e `APP_ENVIRONMENT`
(default `production`) — ver
[Documentação da API nos ambientes hospedados](#documentação-da-api-nos-ambientes-hospedados)
e [Isolamento entre ambientes](#isolamento-entre-ambientes).

> **Pendência conhecida:** a configuração de CORS em
> `ConfiguracaoSeguranca.java` ainda usa a origem fixa `http://localhost:4200` e
> **não lê** a variável `CORS_ALLOWED_ORIGINS`. Enquanto isso não for ajustado,
> o frontend hospedado será bloqueado pelo navegador.

### Recriando o banco do zero

```bash
docker compose down -v
docker compose up -d
```

> **Vindo de uma versão anterior deste compose?** O serviço `db` usa
> PostgreSQL 17. Um volume criado por uma versão anterior do PostgreSQL faz o
> contêiner falhar na subida com:
>
> ```text
> FATAL: database files are incompatible with server
> DETAIL: The data directory was initialized by PostgreSQL version 16,
>         which is not compatible with this version 17
> ```
>
> A solução é recriar o volume com `docker compose down -v` (apaga os dados
> locais do banco, que são apenas de desenvolvimento).

### Comandos úteis

```bash
docker compose logs -f backend        # logs da aplicação
docker compose exec db psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"
docker compose down                   # para os serviços (mantém os dados)
docker compose down -v                # para e apaga o volume do banco
```

## CI/CD

A esteira usa dois workflows do GitHub Actions. **Este repositório não faz
deploy** — ele apenas testa, publica a imagem e avisa o repositório de
infraestrutura.

### `ci-tests.yml`

Executa em `pull_request` para `dev`, `staging` e `main`, e é reutilizado pelo
workflow de build via `workflow_call`.

| Job | O que faz |
|---|---|
| `unit-tests` | `./mvnw -B verify`, publica o relatório JaCoCo e os relatórios do Surefire como artefatos e escreve um resumo de cobertura no sumário da execução |
| `migrations-postgres` | Sobe um PostgreSQL 17 real, empacota e inicia o JAR com o perfil `prod`, aguarda `/actuator/health` responder `UP` e valida que o Flyway aplicou ao menos 2 migrations e que o seed populou a tabela `usuario` |

Para gerar o relatório de cobertura localmente:

```bash
./mvnw -B verify
# abra target/site/jacoco/index.html
```

#### Verificando manualmente que o job falha com uma migration quebrada

1. Crie uma cópia inválida da migration:
   ```bash
   printf 'create table quebrada (id bigint, ;\n' \
     > src/main/resources/db/migration/V1_0_3__teste_falha.sql
   ```
2. Abra um PR para `dev`. O job `migrations-postgres` deve falhar no passo
   "Aguardar health UP", e o log da aplicação (impresso no próprio passo e
   publicado no artefato `app-log-migrations`) mostrará o erro do Flyway.
3. **Remova o arquivo antes do merge.**

### `build-release-images.yml`

Executa em `push` para `dev`, `staging`, `main` e em tags `v*.*.*`.

1. `tests` — reutiliza `ci-tests.yml`;
2. `build` — publica a imagem no `ghcr.io/<owner>/<repo>` (em minúsculas);
3. `notify-infra` — **somente na branch `dev`**, envia um `repository_dispatch`
   para o repositório de infraestrutura.

#### Tags publicadas

| Tag | Quando | Mutável |
|---|---|---|
| `sha-<7 chars>` | sempre — é a tag usada nos deploys | não |
| `dev`, `staging`, `main` | push na branch correspondente | sim |
| `X.Y.Z` e `X.Y` | push de uma tag git `vX.Y.Z` | sim (`X.Y`) |
| `latest` | apenas a partir de `main` | sim |

#### Notificação ao repositório de infraestrutura

Em push na `dev`, após a publicação da imagem:

```json
{
  "event_type": "image-published",
  "client_payload": {
    "service": "backend",
    "tag": "sha-abc1234",
    "sha": "<sha completo>",
    "branch": "dev"
  }
}
```

O deploy de homologação e produção é **manual**, via `workflow_dispatch` no
repositório de infraestrutura, informando a tag da imagem.

#### Secrets e variables necessários

Configure em **Settings → Secrets and variables → Actions** deste repositório:

| Tipo | Nome | Valor |
|---|---|---|
| Secret | `INFRA_DISPATCH_TOKEN` | Token com permissão para disparar `repository_dispatch` no repositório de infraestrutura |
| Variable | `INFRA_REPOSITORY` | Repositório de infraestrutura no formato `owner/repo` |

O `INFRA_DISPATCH_TOKEN` precisa ser um **fine-grained PAT** com permissão
`Contents: write` no repositório de infraestrutura (requisito do endpoint
`repository_dispatch`), ou um token de GitHub App com a permissão equivalente.
O push da imagem para o `ghcr.io` usa o `GITHUB_TOKEN` padrão e não exige
configuração adicional.

Se o secret ou a variable estiverem ausentes, o job `notify-infra` falha com
mensagem explícita.

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
