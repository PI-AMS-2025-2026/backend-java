# ADR-004: Padronização de Chaves Estrangeiras com LongDTO e Desacoplamento no Perfil Dev

## Status
Aprovado

## Contexto
1. **Divergência de DTOs:** No desenvolvimento anterior, enquanto alguns DTOs de cadastro utilizavam `LongDTO` (`{"curso": {"id": 1}}`), outros DTOs (como `AlocacaoRequest` e `DisciplinaRequest`) foram simplificados para receber IDs soltos (`Long cursoId`). O relato #3 da equipe técnica exigiu formalmente a padronização de todos os DTOs de request com `LongDTO`.
2. **Bloqueio 403 no Perfil Dev:** Desenvolvedores enfrentavam bloqueios `403 Forbidden` ao interagir com a API em ambiente de desenvolvimento local, pois validadores de curso e filtros de segurança interceptavam as requisições mesmo com a flag de desenvolvimento.

## Decisão
1. **Padronização Canônica com `LongDTO`:**
   * Todos os DTOs de requisição (`*Request`) que referenciem entidades de apoio/estrangeiras devem utilizar o tipo `com.fatec.gini.dto.id.LongDTO` (ex: `LongDTO curso`, `LongDTO tipoSala`, `LongDTO turma`).
   * Os mappers correspondentes devem extrair o identificador via `dto.campo().id()` ao associar a entidade no banco de dados.
2. **Desacoplamento Completo no Perfil `dev`:**
   * Quando o perfil ativo for `dev` (com `app.security.authorization.enabled = false`), os casos de uso de validação de curso (`ValidarAutorizacaoCursoUseCase`) não devem lançar `AccessDeniedException`.
   * Caso uma operação necessite identificar o usuário logado para fins de auditoria no perfil `dev` e nenhum token Bearer tenha sido fornecido, o sistema deve assumir o usuário administrador padrão (`id = 1` presente nas cargas iniciais).
   * Em ambientes de produção (`prod`) ou de teste com segurança ativa, a autorização e o confinamento de curso são integralmente aplicados com base na matriz `TabelaAcesso.md`.

## Consequências
### Positivas:
* Contratos de entrada previsíveis e consistentes em todos os módulos da API.
* Experiência de desenvolvimento local fluida, sem bloqueios de permissão durante a prototipagem.
* Preservação da segurança rigorosa de produção.

### Negativas / Mitigações:
* O time de frontend precisará ajustar o envio de payloads em cadastros que usavam identificadores planos.
* Mitigação: Mapeamento detalhado e exemplos de JSON fornecidos em `FRONTEND_INTEGRATION.md`.
