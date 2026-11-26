INSERT INTO curso (id_curso, nome, periodicidade, duracao, status)
VALUES (100, 'Curso Migração PI', 'Semestral', 6, 'ATIVO');

INSERT INTO tipo_sala (id_tipo_sala, nome)
VALUES (100, 'Sala Migração PI');

INSERT INTO professor (id_professor, nome, email, status)
VALUES (100, 'Professor Migração PI', 'professor-migracao@gini.local', 'ATIVO');

INSERT INTO bloco_horario (id_bloco_horario, duracao, hora_inicio, hora_fim)
VALUES (100, 60, '07:00', '08:00');

INSERT INTO sala (id_sala, codigo, capacidade, id_tipo_sala)
VALUES (100, 'SALA-MIGRACAO', 30, 100);

INSERT INTO periodo_atividade_quadro (
    id_periodo_atividade_quadro, ano, periodo, data_inicio, data_fim, status,
    tipo_periodo_atividade_quadro
)
VALUES (100, 2026, 1, '2026-01-01', '2026-06-30', 'ATIVO', 0);

INSERT INTO turma (id_turma, codigo, periodo, ano, numero_alunos, id_curso, status)
VALUES
    (100, '1/2026', 1, 2026, 30, 100, 'ATIVO'),
    (101, '2/2026', 2, 2026, 30, 100, 'ATIVO');

INSERT INTO disciplina (
    id_disciplina, nome, carga_horaria, tipo_disciplina, periodo, cod_disciplina,
    cor, id_curso, id_tipo_sala
)
VALUES (100, 'Disciplina Migração PI', 60, 'TEORICA', 1, 'MIG-100', '#123456', 100, 100);

-- Dois quadros com alocações em turmas distintas e um quadro legado vazio.
INSERT INTO quadro_horario (
    id_quadro_horario, versao, data_criacao, status, id_curso,
    id_periodo_atividade_quadro
)
VALUES
    (100, 1, '2026-01-02 08:00:00', 'INATIVO', 100, 100),
    (101, 2, '2026-02-02 08:00:00', 'ATIVO', 100, 100),
    (102, 3, '2026-03-02 08:00:00', 'INATIVO', 100, 100);

INSERT INTO alocacao (
    id_alocacao, dia_semana, id_bloco_horario, id_disciplina, id_professor,
    id_quadro_horario, id_sala, id_turma
)
VALUES
    (200, 'SEGUNDA', 100, 100, 100, 100, 100, 100),
    (201, 'TERÇA', 100, 100, 100, 101, 100, 100),
    (202, 'QUARTA', 100, 100, 100, 101, 100, 101);

INSERT INTO historico_versao_alocacao (
    id_historico_versao_alocacaoo, data_alteracao, campo_alterado,
    valor_antigo, valor_novo, id_alocacao, id_usuario
)
VALUES (200, '2026-02-02', 'sala', '1', '2', 201, 1);
