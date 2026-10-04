# 1
ajustar motor-grade para atentes os requisitos de `motor-grade.md`.


# 2
corrigir nomenclatruras

- Grade horária -> Quadro horário

- Período letivo -> período de atividade do quadro 

- Histórico de alocação -> histórico de versão da alocação 

- Horário -> bloco de horário

- motor-grade -> motor-quadro

Analise o padrão e corrija os endpoints tambem

# 3
garantir que todos os DTOS requeste com entidades estrangeiras utilize `LongDTO`

# 4 
Permitir logar mais de uma vez. atualmente esta: O sistema retorna erro ao logar mais de uma vez com o mesmo usuário. Ele apenas permite logar novamente caso realize logout. 

# 5 
Garanta que não existe bloqueio `forbidden` ao usar o perfil dev

# 6 
Ajustar `docker-compose` e `Dockerfile`. Criar explicação de uso 


# 7 
implementar recurso de recuperação de senha com envio de email.

# 8 
implementar exportaçãoa atraves do motor de quadro