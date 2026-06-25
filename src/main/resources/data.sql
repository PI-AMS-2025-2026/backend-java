INSERT INTO usuario (nome, email, senha, status, created_at, updated_at, tipo_usuario) VALUES
('adm', 'adm@gini.com', '$2a$10$F/m5ZnCRjPHFOf7ZorAg8.ldMFxHY2f2d0aqY4vMgTvGVl8SwS4Ky', 'ATIVO', '2026-01-10 08:00:00', '2026-01-10 08:00:00', 'ADMINISTRADOR');

INSERT INTO usuario (nome, email, senha, status, created_at, updated_at, tipo_usuario) VALUES
('coordenador', 'coordenador@gini.com', '$2a$10$F/m5ZnCRjPHFOf7ZorAg8.ldMFxHY2f2d0aqY4vMgTvGVl8SwS4Ky', 'ATIVO', '2026-01-10 08:00:00', '2026-01-10 08:00:00', 'COORDENADOR');

-- Dados de Teste

INSERT INTO  professor ( nome, email,cidade,status) VALUES 
('Carlos','carlos@cps.sp.gov.br','Sorocaba','ATIVO'),
('Roberto','roberto@cps.sp.gov.br','Itu','ATIVO'),
('Julia','Julia@cps.sp.gov.br','Itu','INATIVO');

INSERT INTO curso (nome, periodicidade, status, duracao) VALUES
('Analise e Desenvolvimento de Sistemas', 'Anual', 'ATIVO', 2),
('Gestão Tecnologia da Informação', 'Semestral', 'ATIVO', 6),
('Gestao Empresarial', 'Semestral', 'INATIVO', 4);

INSERT INTO tipo_sala (nome) VALUES
('Laboratorio'),
('Sala'),
('Auditorio'),
('Sala Maker');

INSERT INTO bloco_horario (duracao,hora_inicio,hora_fim) VALUES 
(50,'13:20:00','14:10:00'),
(50,'14:10:00','15:00:00'),

(50,'15:10:00','16:00:00'),
(50,'16:00:00','16:50:00'),

(50,'17:00:00','17:00:00'),
(50,'17:50:00','18:40:00');

INSERT INTO turma ( codigo,periodo,ano,numero_alunos,id_curso) VALUES
('2/2026','2',2026,25,2),
('1/2026','1',2026,40,1);

INSERT INTO disciplina (nome, carga_horaria, tipo_disciplina, periodo, modalidade, cod_disciplina, cor, id_curso, id_tipo_sala) VALUES
('Programação Multiplataforma', 80, 'Pratica', 1, 'Presencial', 'ISW044', '#2E86AB', 1, 1);


INSERT INTO sala ( codigo, capacidade, id_tipo_sala) VALUES
('LAB-01', 40, 1),
('SALA-12', 45, 1),
('AUD-01', 120, 2);
