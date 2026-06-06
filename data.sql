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

INSERT INTO tipo_usuario ( nome) VALUES
('ADMIN'),
('COORDENADOR'),
('PROFESSOR');

INSERT INTO curso (nome, periodicidade, status, duracao) VALUES
('Analise e Desenvolvimento de Sistemas', 'Semestral', 'ATIVO', 6),
('Logistica', 'Semestral', 'ATIVO', 4),
('Gestao Empresarial', 'Semestral', 'INATIVO', 4);

INSERT INTO tipo_sala ( nome) VALUES
('Laboratorio'),
('Sala Teorica'),
('Auditorio');

INSERT INTO sala ( codigo, capacidade, id_tipo_sala) VALUES
('LAB-01', 40, 1),
('SALA-12', 45, 2),
('AUD-01', 120, 3);

INSERT INTO recurso (nome, tipo) VALUES
('Projetor Epson', 'Multimidia'),
('Computador Desktop', 'Informatica'),
('Ar Condicionado 18000 BTU', 'Climatizacao');

INSERT INTO recurso_sala (quantidade, id_sala, id_recurso) VALUES
(1, 1, 1),
(40, 1, 2),
(1, 2, 1),
(1, 3, 3);

INSERT INTO periodo_letivo (ano, periodo, data_inicio, data_fim, status) VALUES
(2026, 1, '2026-02-02', '2026-06-30', 'ATIVO'),
(2026, 2, '2026-08-03', '2026-12-18', 'ATIVO');

INSERT INTO usuario (nome, email, senha, cidade, status, created_at, updated_at, id_tipo_usuario, id_curso) VALUES
('Administrador Sistema', 'admin@fatec.br', '123456', 'Sao Paulo', 'ATIVO', '2026-01-10 08:00:00', '2026-01-10 08:00:00', 1, 1),
('Carlos Coordenador', 'coordenador.ads@fatec.br', '123456', 'Sao Paulo', 'ATIVO', '2026-01-11 09:00:00', '2026-01-11 09:00:00', 2, 1),
('Ana Professora', 'ana.prof@fatec.br', '123456', 'Santos', 'ATIVO', '2026-01-12 10:00:00', '2026-01-12 10:00:00', 3, 1),
('Bruno Professor', 'bruno.prof@fatec.br', '123456', 'Praia Grande', 'ATIVO', '2026-01-12 11:00:00', '2026-01-12 11:00:00', 3, 2);

INSERT INTO disciplina (nome, carga_horaria, tipo_disciplina, periodo, modalidade, cod_disciplina, cor, id_curso, id_tipo_sala) VALUES
('Banco de Dados', 80, 'Obrigatoria', 3, 'Presencial', 'ADS-BD-03', '#2E86AB', 1, 1),
('Engenharia de Software', 80, 'Obrigatoria', 4, 'Presencial', 'ADS-ES-04', '#F18F01', 1, 2),
('Gestao de Estoque', 60, 'Obrigatoria', 2, 'Presencial', 'LOG-GE-02', '#C73E1D', 2, 2);

INSERT INTO turma (codigo, periodo, ano, numero_alunos, id_curso) VALUES
('ADS-3A-2026', 3, 2026, 38, 1),
('ADS-4A-2026', 4, 2026, 36, 1),
( 'LOG-2A-2026', 2, 2026, 32, 2);

INSERT INTO dia_semana (nome) VALUES
('SEGUNDA'),
('TERCA'),
('QUARTA'),
('QUINTA'),
('SEXTA'),
('SABADO'),
('DOMINGO');

INSERT INTO horario (hora_inicio, hora_fim, duracao) VALUES
('19:00:00', '19:50:00', 50),
('19:50:00', '20:40:00', 50),
('20:50:00', '21:40:00', 50),
('21:40:00', '22:30:00', 50);

INSERT INTO grade_horaria (versao, data_criacao, status, id_curso, id_periodo_letivo) VALUES
(1, '2026-01-20 09:30:00', 'ATIVO', 1, 1),
(1, '2026-01-20 10:00:00', 'ATIVO', 2, 1);

INSERT INTO professor_disciplina (id_usuario, id_disciplina) VALUES
(3, 1),
(4, 3);

INSERT INTO disponibilidade_professor (id_usuario, id_dia_semana, id_horario) VALUES
(3, 1, 1),
(3, 1, 2),
(3, 3, 3),
(4, 2, 1),
(4, 2, 2);

INSERT INTO alocacao (id_turma, id_disciplina, id_sala, id_usuario, id_dia_semana, id_horario, id_grade_horaria) VALUES
(1, 1, 1, 3, 1, 1, 1),
(1, 1, 1, 3, 1, 2, 1),
(3, 3, 2, 4, 2, 1, 2);

INSERT INTO historico_alteracao (data_alteracao, justificativa, campo_alterado, valor_antigo, valor_novo, id_alocacao, id_usuario) VALUES
('2026-02-10', 'Ajuste de disponibilidade do professor', 'id_sala', '3', '1', 1, 2),
('2026-02-12', 'Correcao de horario para evitar conflito', 'id_horario', '3', '2', 2, 2);

