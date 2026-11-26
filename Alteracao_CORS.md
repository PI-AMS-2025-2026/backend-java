# Relatório de alterações

## Objetivo

Registrar as mudanças recentes no frontend e no backend de forma que possam ser reimplementadas caso necessário.

##  CORS liberado para qualquer origem/IP

As configurações CORS do backend foram alteradas para usar `allowedOriginPatterns("*")` nos perfis `prod`, `dev` e `test`, com métodos e cabeçalhos liberados e credenciais habilitadas. No perfil `prod`, `FRONTEND_URL` deixou de ser usada para restringir a origem CORS; continua necessária como base do link de recuperação de senha.

O `@CrossOrigin` específico do controller de alocações foi removido para centralizar a política nos configuradores de segurança dos perfis.

No Next.js, `allowedDevOrigins` passou do IP fixo para o padrão `**.*`, permitindo carregar recursos internos do servidor de desenvolvimento quando acessado por diferentes hosts/IPs.

Arquivos:

- `backend-java/src/main/java/com/fatec/gini/web/config/ConfiguracaoSeguranca.java` — política do perfil `prod`; remove o campo `frontendUrl` usado apenas para restringir CORS.
- `backend-java/src/main/java/com/fatec/gini/web/config/ConfiguracaoSegurancaDev.java` — política do perfil `dev`.
- `backend-java/src/main/java/com/fatec/gini/web/config/ConfiguracaoSegurancaTest.java` — política do perfil `test`.
- `backend-java/src/main/java/com/fatec/gini/web/controller/AlocacaoController.java` — remove a anotação CORS local.
- `frontend-nextjs/next.config.ts` — permite hosts variados no servidor de desenvolvimento.

## Estado dos arquivos

Na criação deste relatório, os ajustes de CORS listados acima apareciam como alterações no estado de trabalho dos repositórios. Os ajustes de `/api` e a remoção do checkbox já estavam presentes no `HEAD` do frontend e, por isso, não apareciam como alterações pendentes no Git.

## Observações para reimplementação

1. Remover o checkbox de login e o import associado, preservando o link de recuperação de senha.
2. Configurar CORS em cada perfil Spring ativo. Verificar também anotações `@CrossOrigin` locais que possam manter uma política separada.
3. No Next.js, `allowedDevOrigins` afeta recursos do servidor de desenvolvimento; não substitui a configuração CORS do backend.
5. Atualizar comentários e documentação que ainda descrevam origens fixas ou associem `FRONTEND_URL` à política CORS.

