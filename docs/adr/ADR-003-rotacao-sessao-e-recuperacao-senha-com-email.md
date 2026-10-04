# ADR-003: Rotação de Sessão com RefreshToken e Fluxo de Recuperação de Senha por E-mail

## Status
Aprovado

## Contexto
1. **Erro de Login Múltiplo:** O sistema impedia que um usuário logasse consecutivamente sem antes realizar logout explícito. Em `RefreshTokenService`, a chamada de exclusão do token anterior não executava flush imediato antes do salvamento da nova entidade, violando a restrição de unicidade `@OneToOne` de `id_usuario`.
2. **Recuperação de Senha Inexistente:** O sistema não possuía mecanismo de recuperação de credenciais de acesso esquecidas.

## Decisão
1. **Rotação e Sobrescrita de Sessão:** 
   * Ao invés de deletar e reinserir instâncias de `RefreshToken` na mesma transação, o serviço passa a buscar a entidade existente e atualizar o valor do token e da expiração in-place, ou executar remoção com `deleteByUsuario` acompanhada de `flush()` síncrono antes do novo `save`.
   * A estratégia adotada é de **Sessão Única com Rotação**: um novo login gera uma nova sessão e invalida o refresh token anterior, sem disparar erro para o usuário.
2. **Recuperação de Senha via E-mail:**
   * Criação da entidade `PasswordResetToken` mapeada para a tabela `password_reset_token` via migração Flyway.
   * Endpoints expostos:
     * `POST /auth/solicitar-recuperacao` (Payload: `{"email": "..."}`)
     * `POST /auth/redefinir-senha` (Payload: `{"token": "...", "nova_senha": "..."}`)
   * O token possui validade de 15 minutos (configurável via variável de ambiente `APP_SECURITY_PASSWORD_RESET_EXPIRATION_MINUTES`) e expira após o uso.
   * O e-mail contém um link dinâmico construído via `${FRONTEND_URL}/redefinir-senha?token={token}`.
   * Para ambiente local/dev, o `docker-compose.yml` inclui o serviço **Mailpit** (servidor SMTP mock com interface web para inspeção de mensagens).

## Consequências
### Positivas:
* Resolução definitiva do bloqueio de login consecutivo.
* Automação segura do processo de redefinição de senhas com expiração curta.
* Ambiente de desenvolvimento totalmente isolado e testável sem necessidade de envio de e-mails reais.

### Negativas / Mitigações:
* O ambiente de produção necessita de credenciais SMTP válidas no `.env`.
* Mitigação: Tarefa humana registrada no backlog (`TASK-011`) para fornecimento dessas credenciais.
