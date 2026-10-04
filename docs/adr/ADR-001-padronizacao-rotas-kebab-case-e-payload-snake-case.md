# ADR-001: Padronização de Rotas RESTful em kebab-case e Payloads em snake_case

## Status
Aprovado

## Contexto
O backend apresentava divergências nas convenções de nomenclatura de endpoints HTTP (por exemplo, `/periodo_atividade_quadro` com underscore e no singular, `/recurso-sala` no singular, enquanto `/bloco-horarios` e `/tipos-sala` utilizavam kebab-case). Além disso, havia incompatibilidade de serialização de propriedades entre o backend (que adotava camelCase padrão do Spring Boot) e os relatos da equipe técnica e consumidores externos.

## Decisão
1. **Convenção de Rotas RESTful:** Todas as rotas de controladores devem adotar estritamente o padrão `kebab-case` plural (ex: `/periodos-atividade-quadro`, `/historicos-versoes-alocacoes`, `/quadros-horarios`, `/recursos-salas`, `/tipos-salas`, `/tipos-recursos`, `/motor-quadro`).
2. **Serialização e Desserialização JSON:** Configuração global do Jackson para utilizar a estratégia `PropertyNamingStrategies.SNAKE_CASE` em todos os payloads de requisição e resposta.
3. **Parâmetros de URL:** Todos os parâmetros de query (`@RequestParam`) devem ser nomeados explicitamente em `snake_case` (ex: `data_inicio`, `data_fim`).

## Consequências
### Positivas:
* Uniformidade arquitetural e previsibilidade em toda a API REST.
* Alinhamento direto com o padrão de comunicação exigido pela equipe.
* Eliminação de erros de mapeamento entre frontends e microserviços.

### Negativas / Mitigações:
* Quebra de contrato com o cliente HTTP atual do Next.js.
* Mitigação: Criação do documento `FRONTEND_INTEGRATION.md` contendo a tabela completa de mapeamento de endpoints e campos para rápida adequação da equipe de interface.
