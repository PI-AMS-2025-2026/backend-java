-- Massa de dados ficticia para testes locais (H2)

SET REFERENTIAL_INTEGRITY FALSE;

DELETE FROM historico_alteracao;
DELETE FROM alocacao;
DELETE FROM disponibilidade_professor;
DELETE FROM professor_disciplina;
DELETE FROM recurso_sala;
DELETE FROM grade_horaria;
DELETE FROM horario;
DELETE FROM dia_semana;
DELETE FROM turma;
DELETE FROM disciplina;
DELETE FROM usuario;
DELETE FROM periodo_letivo;
DELETE FROM recurso;
DELETE FROM sala;
DELETE FROM tipo_sala;
DELETE FROM curso;
DELETE FROM tipo_usuario;

SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO tipo_usuario (id_tipo_usuario, nome) VALUES
(1, 'ADMIN'),
(2, 'COORDENADOR'),
(3, 'PROFESSOR');

INSERT INTO curso (id_curso, nome, periodicidade, status, duracao) VALUES
(1, 'Analise e Desenvolvimento de Sistemas', 'Semestral', 'ATIVO', 6),
(2, 'Logistica', 'Semestral', 'ATIVO', 4),
(3, 'Gestao Empresarial', 'Semestral', 'INATIVO', 4);

INSERT INTO tipo_sala (id_tipo_sala, nome) VALUES
(1, 'Laboratorio'),
(2, 'Sala Teorica'),
(3, 'Auditorio');

INSERT INTO sala (id_sala, codigo, capacidade, id_tipo_sala) VALUES
(1, 'LAB-01', 40, 1),
(2, 'SALA-12', 45, 2),
(3, 'AUD-01', 120, 3);

INSERT INTO recurso (id_recurso, nome, tipo) VALUES
(1, 'Projetor Epson', 'Multimidia'),
(2, 'Computador Desktop', 'Informatica'),
(3, 'Ar Condicionado 18000 BTU', 'Climatizacao');

INSERT INTO recurso_sala (id_recurso_sala, quantidade, id_sala, id_recurso) VALUES
(1, 1, 1, 1),
(2, 40, 1, 2),
(3, 1, 2, 1),
(4, 1, 3, 3);

INSERT INTO periodo_letivo (id_periodo_letivo, ano, periodo, data_inicio, data_fim, status) VALUES
(1, 2026, 1, '2026-02-02', '2026-06-30', 'ATIVO'),
(2, 2026, 2, '2026-08-03', '2026-12-18', 'ATIVO');

INSERT INTO usuario (id_usuario, nome, email, senha, cidade, status, created_at, updated_at, id_tipo_usuario, id_curso) VALUES
(1, 'Administrador Sistema', 'admin@fatec.br', '123456', 'Sao Paulo', 'ATIVO', '2026-01-10 08:00:00', '2026-01-10 08:00:00', 1, 1),
(2, 'Carlos Coordenador', 'coordenador.ads@fatec.br', '123456', 'Sao Paulo', 'ATIVO', '2026-01-11 09:00:00', '2026-01-11 09:00:00', 2, 1),
(3, 'Ana Professora', 'ana.prof@fatec.br', '123456', 'Santos', 'ATIVO', '2026-01-12 10:00:00', '2026-01-12 10:00:00', 3, 1),
(4, 'Bruno Professor', 'bruno.prof@fatec.br', '123456', 'Praia Grande', 'ATIVO', '2026-01-12 11:00:00', '2026-01-12 11:00:00', 3, 2);

INSERT INTO disciplina (id_disciplina, nome, carga_horaria, tipo_disciplina, periodo, modalidade, cod_disciplina, cor, id_curso, id_tipo_sala) VALUES
(1, 'Banco de Dados', 80, 'Obrigatoria', 3, 'Presencial', 'ADS-BD-03', '#2E86AB', 1, 1),
(2, 'Engenharia de Software', 80, 'Obrigatoria', 4, 'Presencial', 'ADS-ES-04', '#F18F01', 1, 2),
(3, 'Gestao de Estoque', 60, 'Obrigatoria', 2, 'Presencial', 'LOG-GE-02', '#C73E1D', 2, 2);

INSERT INTO turma (id_turma, codigo, periodo, ano, numero_alunos, id_curso) VALUES
(1, 'ADS-3A-2026', 3, 2026, 38, 1),
(2, 'ADS-4A-2026', 4, 2026, 36, 1),
(3, 'LOG-2A-2026', 2, 2026, 32, 2);

INSERT INTO dia_semana (id_dia_semana, nome) VALUES
(1, 'SEGUNDA'),
(2, 'TERCA'),
(3, 'QUARTA'),
(4, 'QUINTA'),
(5, 'SEXTA');

INSERT INTO horario (id_horario, hora_inicio, hora_fim, duracao) VALUES
(1, '19:00:00', '19:50:00', 50),
(2, '19:50:00', '20:40:00', 50),
(3, '20:50:00', '21:40:00', 50),
(4, '21:40:00', '22:30:00', 50);

INSERT INTO grade_horaria (id_grade_horaria, versao, data_criacao, status, id_curso, id_periodo_letivo) VALUES
(1, 1, '2026-01-20 09:30:00', 'ATIVO', 1, 1),
(2, 1, '2026-01-20 10:00:00', 'ATIVO', 2, 1);

INSERT INTO professor_disciplina (id_professor_disciplina, id_usuario, id_disciplina) VALUES
(1, 3, 1),
(2, 4, 3);

INSERT INTO disponibilidade_professor (id_disponibilidade_professor, id_usuario, id_dia_semana, id_horario) VALUES
(1, 3, 1, 1),
(2, 3, 1, 2),
(3, 3, 3, 3),
(4, 4, 2, 1),
(5, 4, 2, 2);

INSERT INTO alocacao (id_alocacao, id_turma, id_disciplina, id_sala, id_usuario, id_dia_semana, id_horario, id_grade_horaria) VALUES
(1, 1, 1, 1, 3, 1, 1, 1),
(2, 1, 1, 1, 3, 1, 2, 1),
(3, 3, 3, 2, 4, 2, 1, 2);

INSERT INTO historico_alteracao (id_historico_alteracao, data_alteracao, justificativa, campo_alterado, valor_antigo, valor_novo, id_alocacao, id_usuario) VALUES
(1, '2026-02-10', 'Ajuste de disponibilidade do professor', 'id_sala', '3', '1', 1, 2),
(2, '2026-02-12', 'Correcao de horario para evitar conflito', 'id_horario', '3', '2', 2, 2);
