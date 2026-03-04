-- ============================
-- DADOS INICIAIS COMPATÍVEIS COM AS ENTIDADES JPA
-- ============================

-- Níveis de acesso
INSERT INTO access_level (level, description) VALUES (10, 'Administrador');
INSERT INTO access_level (level, description) VALUES (5, 'Coordenador');
INSERT INTO access_level (level, description) VALUES (1, 'Professor');

-- Disciplinas
INSERT INTO subject (name, acronym) VALUES ('Programação Orientada a Objetos', 'POO');
INSERT INTO subject (name, acronym) VALUES ('Banco de Dados', 'BD');
INSERT INTO subject (name, acronym) VALUES ('Engenharia de Software I', 'ES1');
INSERT INTO subject (name, acronym) VALUES ('Estruturas de Dados', 'ED');

-- Cursos
INSERT INTO course (name, description) VALUES ('Tecnologia em Análise e Desenvolvimento de Sistemas', 'Formação de desenvolvedores de software');
INSERT INTO course (name, description) VALUES ('Gestão de Tecnologia da Informação', 'Gestão de projetos e infraestrutura de TI');

-- Relacionamento Curso x Disciplina
INSERT INTO course_subject (course_id, subject_id) VALUES (1, 1);
INSERT INTO course_subject (course_id, subject_id) VALUES (1, 2);
INSERT INTO course_subject (course_id, subject_id) VALUES (1, 3);
INSERT INTO course_subject (course_id, subject_id) VALUES (2, 2);
INSERT INTO course_subject (course_id, subject_id) VALUES (2, 4);

-- Semestres acadêmicos
INSERT INTO academic_semester (academic_year, status, course_id) VALUES (2026, 'ATIVO', 1);
INSERT INTO academic_semester (academic_year, status, course_id) VALUES (2026, 'PLANEJAMENTO', 2);

-- Salas
INSERT INTO classroom (name, location, physical_resources, software_resources, capacity, template, practical) VALUES
('Sala 101', 'Bloco A - 1º Andar', 'Projetor;Quadro Branco', 'Navegador;Office', 40, FALSE, FALSE);

INSERT INTO classroom (name, location, physical_resources, software_resources, capacity, template, practical) VALUES
('Laboratório 201', 'Bloco A - 2º Andar', 'Computadores;Projetor', 'IDE;SGBD;Git', 30, FALSE, TRUE);

-- Usuários
INSERT INTO tbl_user (name, email, password, access_level_id) VALUES
('Carlos Admin', 'admin@fatec.sp.gov.br', 'admin123', 1);

INSERT INTO tbl_user (name, email, password, access_level_id) VALUES
('Maria Coordenadora', 'maria.coord@fatec.sp.gov.br', 'coord123', 2);

INSERT INTO tbl_user (name, email, password, access_level_id) VALUES
('Prof. João Silva', 'joao.silva@fatec.sp.gov.br', 'prof123', 3);

-- Relacionamento Professor x Disciplina
INSERT INTO user_subject (user_id, subject_id) VALUES (3, 1);
INSERT INTO user_subject (user_id, subject_id) VALUES (3, 2);

-- Turmas
INSERT INTO class_group (student_count) VALUES (35);
INSERT INTO class_group (student_count) VALUES (28);

-- Disponibilidade de usuário
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (1, 1, 3);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (3, 2, 3);
