INSERT INTO usuario (nome, email, senha, status, created_at, updated_at, tipo_usuario) VALUES
('adm', 'adm@gini.com', '$2a$10$oHzz0j333M3RjQSQoRneK.RB0AxUr9T308Tt10fkhJposh9Mqhx.a', 'ATIVO', '2026-01-10 08:00:00', '2026-01-10 08:00:00', 'ADMINISTRADOR');

INSERT INTO usuario (nome, email, senha, status, created_at, updated_at, tipo_usuario) VALUES
('coordenador', 'coordenador@gini.com', '$2a$10$oHzz0j333M3RjQSQoRneK.RB0AxUr9T308Tt10fkhJposh9Mqhx.a', 'ATIVO', '2026-01-10 08:00:00', '2026-01-10 08:00:00', 'COORDENADOR');

-- Dados de Teste

INSERT INTO  professor ( nome, email,cidade,status) VALUES 
('Carlos','carlos@cps.sp.gov.br','Sorocaba','ATIVO'),
('Roberto','roberto@cps.sp.gov.br','Itu','ATIVO'),
('Julia','Julia@cps.sp.gov.br','Itu','INATIVO');

INSERT INTO professor (nome, email, cidade, status) VALUES
('Mariana', 'mariana@cps.sp.gov.br', 'Sorocaba', 'ATIVO'),
('Felipe', 'felipe@cps.sp.gov.br', 'Votorantim', 'ATIVO'),
('Patricia', 'patricia@cps.sp.gov.br', 'Salto', 'ATIVO'),
('Diego', 'diego@cps.sp.gov.br', 'Itu', 'INATIVO');

INSERT INTO curso (nome, periodicidade, status, duracao) VALUES
('Analise e Desenvolvimento de Sistemas', 'Anual', 'ATIVO', 2),
('Gestão Tecnologia da Informação', 'Semestral', 'ATIVO', 6),
('Gestao Empresarial', 'Semestral', 'INATIVO', 4);

INSERT INTO curso (nome, periodicidade, status, duracao) VALUES
('Logistica', 'Semestral', 'ATIVO', 4),
('Eletronica Automotiva', 'Anual', 'ATIVO', 3),
('Projetos Mecânicos', 'Semestral', 'INATIVO', 4);

INSERT INTO tipo_sala (nome) VALUES
('Laboratorio'),
('Sala'),
('Auditorio'),
('Sala Maker');

INSERT INTO tipo_sala (nome) VALUES
('Sala de Reuniao'),
('Laboratorio de Informatica');

INSERT INTO bloco_horario (duracao,hora_inicio,hora_fim) VALUES 
(50,'13:20:00','14:10:00'),
(50,'14:10:00','15:00:00'),

(50,'15:10:00','16:00:00'),
(50,'16:00:00','16:50:00'),

(50,'17:00:00','17:00:00'),
(50,'17:50:00','18:40:00');

INSERT INTO bloco_horario (duracao, hora_inicio, hora_fim) VALUES
(50, '18:50:00', '19:40:00'),
(50, '19:40:00', '20:30:00'),
(50, '20:40:00', '21:30:00'),
(50, '21:30:00', '22:20:00');

INSERT INTO turma ( codigo,periodo,ano,numero_alunos,id_curso) VALUES
('2/2026','2',2026,25,2),
('1/2026','1',2026,40,1);

INSERT INTO turma (codigo, periodo, ano, numero_alunos, id_curso) VALUES
('1/2026-LOG', 1, 2026, 32, (SELECT id_curso FROM curso WHERE nome = 'Logistica')),
('2/2026-ADS', 2, 2026, 38, (SELECT id_curso FROM curso WHERE nome = 'Analise e Desenvolvimento de Sistemas')),
('1/2027-GTI', 1, 2027, 28, (SELECT id_curso FROM curso WHERE nome = 'Gestão Tecnologia da Informação'));

INSERT INTO disciplina (nome, carga_horaria, tipo_disciplina, periodo, modalidade, cod_disciplina, cor, id_curso, id_tipo_sala) VALUES
('Programação Multiplataforma', 80, 'Pratica', 1, 'Presencial', 'ISW044', '#2E86AB', 1, 1);

INSERT INTO disciplina (nome, carga_horaria, tipo_disciplina, periodo, modalidade, cod_disciplina, cor, id_curso, id_tipo_sala) VALUES
('Banco de Dados', 80, 'Teorica', 1, 'Presencial', 'BD001', '#F18F01',
 (SELECT id_curso FROM curso WHERE nome = 'Analise e Desenvolvimento de Sistemas'),
 (SELECT id_tipo_sala FROM tipo_sala WHERE nome = 'Laboratorio')),
('Engenharia de Software', 80, 'Teorica', 2, 'Presencial', 'ES002', '#C73E1D',
 (SELECT id_curso FROM curso WHERE nome = 'Analise e Desenvolvimento de Sistemas'),
 (SELECT id_tipo_sala FROM tipo_sala WHERE nome = 'Sala')),
('Gestao de Projetos', 40, 'Teorica', 1, 'Hibrida', 'GP101', '#6A994E',
 (SELECT id_curso FROM curso WHERE nome = 'Gestão Tecnologia da Informação'),
 (SELECT id_tipo_sala FROM tipo_sala WHERE nome = 'Sala')),
('Fundamentos de Logistica', 60, 'Teorica', 1, 'Presencial', 'LOG010', '#8338EC',
 (SELECT id_curso FROM curso WHERE nome = 'Logistica'),
 (SELECT id_tipo_sala FROM tipo_sala WHERE nome = 'Sala')),
('Automacao Industrial', 80, 'Pratica', 2, 'Presencial', 'AUT020', '#3A86FF',
 (SELECT id_curso FROM curso WHERE nome = 'Eletronica Automotiva'),
 (SELECT id_tipo_sala FROM tipo_sala WHERE nome = 'Sala Maker'));


INSERT INTO sala ( codigo, capacidade, id_tipo_sala) VALUES
('LAB-01', 40, 1),
('SALA-12', 45, 1),
('AUD-01', 120, 2);

INSERT INTO sala (codigo, capacidade, id_tipo_sala) VALUES
('LAB-02', 35, (SELECT id_tipo_sala FROM tipo_sala WHERE nome = 'Laboratorio')),
('SALA-21', 50, (SELECT id_tipo_sala FROM tipo_sala WHERE nome = 'Sala')),
('MAKER-01', 30, (SELECT id_tipo_sala FROM tipo_sala WHERE nome = 'Sala Maker')),
('REUN-01', 12, (SELECT id_tipo_sala FROM tipo_sala WHERE nome = 'Sala de Reuniao'));

INSERT INTO tipo_recurso (nome) VALUES
('Equipamento'),
('Mobiliario'),
('Software'),
('Material Didatico');

INSERT INTO recurso (nome, id_tipo_recurso) VALUES
('Projetor multimidia', (SELECT id_tipo_recurso FROM tipo_recurso WHERE nome = 'Equipamento')),
('Computador desktop', (SELECT id_tipo_recurso FROM tipo_recurso WHERE nome = 'Equipamento')),
('Kit Arduino', (SELECT id_tipo_recurso FROM tipo_recurso WHERE nome = 'Material Didatico')),
('Mesa de reuniao', (SELECT id_tipo_recurso FROM tipo_recurso WHERE nome = 'Mobiliario')),
('Licenca IDE', (SELECT id_tipo_recurso FROM tipo_recurso WHERE nome = 'Software'));

INSERT INTO periodo_atividade_quadro (ano, data_inicio, data_fim, periodo, status) VALUES
(2026, '2026-02-02', '2026-06-30', 1, 'ATIVO'),
(2026, '2026-08-03', '2026-12-18', 2, 'ATIVO'),
(2025, '2025-08-04', '2025-12-19', 2, 'INATIVO');

INSERT INTO quadro_horario (versao, data_criacao, status, id_curso, id_periodo_atividade_quadro) VALUES
(1, '2026-01-15 09:00:00', 'ATIVO',
 (SELECT id_curso FROM curso WHERE nome = 'Analise e Desenvolvimento de Sistemas'),
 (SELECT id_periodo_atividade_quadro FROM periodo_atividade_quadro WHERE ano = 2026 AND periodo = 1)),
(1, '2026-01-15 09:30:00', 'ATIVO',
 (SELECT id_curso FROM curso WHERE nome = 'Gestão Tecnologia da Informação'),
 (SELECT id_periodo_atividade_quadro FROM periodo_atividade_quadro WHERE ano = 2026 AND periodo = 1)),
(2, '2026-07-15 10:00:00', 'INATIVO',
 (SELECT id_curso FROM curso WHERE nome = 'Analise e Desenvolvimento de Sistemas'),
 (SELECT id_periodo_atividade_quadro FROM periodo_atividade_quadro WHERE ano = 2025 AND periodo = 2));

INSERT INTO professor_disciplina (id_professor, id_disciplina) VALUES
((SELECT id_professor FROM professor WHERE email = 'carlos@cps.sp.gov.br'), (SELECT id_disciplina FROM disciplina WHERE cod_disciplina = 'BD001')),
((SELECT id_professor FROM professor WHERE email = 'carlos@cps.sp.gov.br'), (SELECT id_disciplina FROM disciplina WHERE cod_disciplina = 'ISW044')),
((SELECT id_professor FROM professor WHERE email = 'mariana@cps.sp.gov.br'), (SELECT id_disciplina FROM disciplina WHERE cod_disciplina = 'ES002')),
((SELECT id_professor FROM professor WHERE email = 'felipe@cps.sp.gov.br'), (SELECT id_disciplina FROM disciplina WHERE cod_disciplina = 'GP101')),
((SELECT id_professor FROM professor WHERE email = 'patricia@cps.sp.gov.br'), (SELECT id_disciplina FROM disciplina WHERE cod_disciplina = 'LOG010')),
((SELECT id_professor FROM professor WHERE email = 'diego@cps.sp.gov.br'), (SELECT id_disciplina FROM disciplina WHERE cod_disciplina = 'AUT020'));

INSERT INTO disponibilidade_professor (dia_semana, id_bloco_horario, id_professor) VALUES
(1, (SELECT id_bloco_horario FROM bloco_horario WHERE hora_inicio = '13:20:00'), (SELECT id_professor FROM professor WHERE email = 'carlos@cps.sp.gov.br')),
(2, (SELECT id_bloco_horario FROM bloco_horario WHERE hora_inicio = '14:10:00'), (SELECT id_professor FROM professor WHERE email = 'mariana@cps.sp.gov.br')),
(3, (SELECT id_bloco_horario FROM bloco_horario WHERE hora_inicio = '15:10:00'), (SELECT id_professor FROM professor WHERE email = 'felipe@cps.sp.gov.br')),
(4, (SELECT id_bloco_horario FROM bloco_horario WHERE hora_inicio = '18:50:00'), (SELECT id_professor FROM professor WHERE email = 'patricia@cps.sp.gov.br')),
(5, (SELECT id_bloco_horario FROM bloco_horario WHERE hora_inicio = '19:40:00'), (SELECT id_professor FROM professor WHERE email = 'roberto@cps.sp.gov.br'));

INSERT INTO recurso_sala (quantidade, id_recurso, id_sala) VALUES
(1, (SELECT id_recurso FROM recurso WHERE nome = 'Projetor multimidia'), (SELECT id_sala FROM sala WHERE codigo = 'LAB-01')),
(40, (SELECT id_recurso FROM recurso WHERE nome = 'Computador desktop'), (SELECT id_sala FROM sala WHERE codigo = 'LAB-01')),
(1, (SELECT id_recurso FROM recurso WHERE nome = 'Projetor multimidia'), (SELECT id_sala FROM sala WHERE codigo = 'SALA-12')),
(12, (SELECT id_recurso FROM recurso WHERE nome = 'Mesa de reuniao'), (SELECT id_sala FROM sala WHERE codigo = 'REUN-01')),
(10, (SELECT id_recurso FROM recurso WHERE nome = 'Kit Arduino'), (SELECT id_sala FROM sala WHERE codigo = 'MAKER-01'));

INSERT INTO alocacao (dia_semana, id_bloco_horario, id_disciplina, id_professor, id_quadro_horario, id_sala, id_turma, created_at, updated_at) VALUES
(1,
 (SELECT id_bloco_horario FROM bloco_horario WHERE hora_inicio = '13:20:00'),
 (SELECT id_disciplina FROM disciplina WHERE cod_disciplina = 'ISW044'),
 (SELECT id_professor FROM professor WHERE email = 'carlos@cps.sp.gov.br'),
 (SELECT id_quadro_horario FROM quadro_horario WHERE versao = 1 AND id_curso = (SELECT id_curso FROM curso WHERE nome = 'Analise e Desenvolvimento de Sistemas')),
 (SELECT id_sala FROM sala WHERE codigo = 'LAB-01'),
 (SELECT id_turma FROM turma WHERE codigo = '1/2026'), '2026-01-20 08:00:00', '2026-01-20 08:00:00'),
(2,
 (SELECT id_bloco_horario FROM bloco_horario WHERE hora_inicio = '14:10:00'),
 (SELECT id_disciplina FROM disciplina WHERE cod_disciplina = 'BD001'),
 (SELECT id_professor FROM professor WHERE email = 'carlos@cps.sp.gov.br'),
 (SELECT id_quadro_horario FROM quadro_horario WHERE versao = 1 AND id_curso = (SELECT id_curso FROM curso WHERE nome = 'Analise e Desenvolvimento de Sistemas')),
 (SELECT id_sala FROM sala WHERE codigo = 'LAB-02'),
 (SELECT id_turma FROM turma WHERE codigo = '2/2026-ADS'), '2026-01-20 08:05:00', '2026-01-20 08:05:00'),
(3,
 (SELECT id_bloco_horario FROM bloco_horario WHERE hora_inicio = '15:10:00'),
 (SELECT id_disciplina FROM disciplina WHERE cod_disciplina = 'GP101'),
 (SELECT id_professor FROM professor WHERE email = 'felipe@cps.sp.gov.br'),
 (SELECT id_quadro_horario FROM quadro_horario WHERE versao = 1 AND id_curso = (SELECT id_curso FROM curso WHERE nome = 'Gestão Tecnologia da Informação')),
 (SELECT id_sala FROM sala WHERE codigo = 'SALA-21'),
 (SELECT id_turma FROM turma WHERE codigo = '2/2026'), '2026-01-20 08:10:00', '2026-01-20 08:10:00'),
(4,
 (SELECT id_bloco_horario FROM bloco_horario WHERE hora_inicio = '18:50:00'),
 (SELECT id_disciplina FROM disciplina WHERE cod_disciplina = 'LOG010'),
 (SELECT id_professor FROM professor WHERE email = 'patricia@cps.sp.gov.br'),
 (SELECT id_quadro_horario FROM quadro_horario WHERE versao = 1 AND id_curso = (SELECT id_curso FROM curso WHERE nome = 'Analise e Desenvolvimento de Sistemas')),
 (SELECT id_sala FROM sala WHERE codigo = 'SALA-12'),
 (SELECT id_turma FROM turma WHERE codigo = '1/2026-LOG'), '2026-01-20 08:15:00', '2026-01-20 08:15:00');


INSERT INTO usuario (nome, email, senha, status, created_at, updated_at, tipo_usuario, id_curso) VALUES
('coordenadora.ads', 'coordenadora.ads@gini.com', '$2a$10$oHzz0j333M3RjQSQoRneK.RB0AxUr9T308Tt10fkhJposh9Mqhx.a', 'ATIVO', '2026-01-10 08:30:00', '2026-01-10 08:30:00', 'COORDENADOR', (SELECT id_curso FROM curso WHERE nome = 'Analise e Desenvolvimento de Sistemas')),
('coordenador.gti', 'coordenador.gti@gini.com', '$2a$10$oHzz0j333M3RjQSQoRneK.RB0AxUr9T308Tt10fkhJposh9Mqhx.a', 'ATIVO', '2026-01-10 08:30:00', '2026-01-10 08:30:00', 'COORDENADOR', (SELECT id_curso FROM curso WHERE nome = 'Gestão Tecnologia da Informação')),
('usuario.inativo', 'usuario.inativo@gini.com', '$2a$10$oHzz0j333M3RjQSQoRneK.RB0AxUr9T308Tt10fkhJposh9Mqhx.a', 'INATIVO', '2026-01-10 08:30:00', '2026-01-10 08:30:00', 'COORDENADOR', (SELECT id_curso FROM curso WHERE nome = 'Logistica'));

