-- ============================
-- DADOS DE CATÁLOGO
-- ============================

-- Níveis de Acesso
INSERT INTO access_level (level, description) VALUES (10, 'Administrador');
INSERT INTO access_level (level, description) VALUES (5, 'Coordenador');
INSERT INTO access_level (level, description) VALUES (1, 'Professor');
INSERT INTO access_level (level, description) VALUES (1, 'Auxiliar');

-- Modalidades
INSERT INTO modality (name) VALUES ('Presencial');
INSERT INTO modality (name) VALUES ('EAD');
INSERT INTO modality (name) VALUES ('Híbrido');

-- Eixos Tecnológicos
INSERT INTO tech_axis (name) VALUES ('Informática');
INSERT INTO tech_axis (name) VALUES ('Gestão e Negócios');
INSERT INTO tech_axis (name) VALUES ('Controle e Processos Industriais');
INSERT INTO tech_axis (name) VALUES ('Infraestrutura');

-- Periodicidade
INSERT INTO periodicity (description) VALUES ('Semestral');
INSERT INTO periodicity (description) VALUES ('Anual');
INSERT INTO periodicity (description) VALUES ('Modular');

-- ============================
-- CURSOS
-- ============================

INSERT INTO course (name, description, modality_id, periodicity_id) VALUES 
('Tecnologia em Análise e Desenvolvimento de Sistemas', 'Formação de desenvolvedores de software', 1, 1);

INSERT INTO course (name, description, modality_id, periodicity_id) VALUES 
('Gestão de Tecnologia da Informação', 'Gestão de projetos e infraestrutura de TI', 1, 1);

INSERT INTO course (name, description, modality_id, periodicity_id) VALUES 
('Tecnologia em Redes de Computadores', 'Especialização em redes e telecomunicações', 1, 1);

INSERT INTO course (name, description, modality_id, periodicity_id) VALUES 
('Tecnologia em Segurança da Informação', 'Proteção e segurança de sistemas', 2, 1);

-- ============================
-- DISCIPLINAS
-- ============================

-- Disciplinas de Informática - TADS
INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Programação Orientada a Objetos', 'POO', 1, 1);

INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Banco de Dados', 'BD', 1, 1);

INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Engenharia de Software I', 'ES1', 1, 1);

INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Estruturas de Dados', 'ED', 1, 1);

INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Desenvolvimento Web', 'WEB', 1, 1);

INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Programação para Dispositivos Móveis', 'MOBILE', 1, 1);

-- Disciplinas de Gestão
INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Gestão de Projetos', 'GP', 2, 1);

INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Governança de TI', 'GTI', 2, 1);

INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Empreendedorismo Digital', 'EMPR', 2, 1);

-- Disciplinas de Redes
INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Redes de Computadores I', 'REDES1', 4, 1);

INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Administração de Sistemas Linux', 'LINUX', 4, 1);

INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Segurança de Redes', 'SECREDES', 4, 1);

-- Disciplinas de Segurança
INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Criptografia e Segurança', 'CRIPTO', 1, 2);

INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Ethical Hacking', 'HACK', 1, 2);

INSERT INTO subject (name, acronym, tech_axis_id, modality_id) VALUES 
('Auditoria de Sistemas', 'AUDIT', 2, 2);

-- ============================
-- COURSE_SUBJECT (com semester_number e practical_lessons_count)
-- ============================
-- ============================
-- COURSE_SUBJECT (com semester_number e practical_lessons_count)
-- ============================

-- TADS - 1º Semestre
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (1, 1, 1, 4);
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (1, 2, 1, 2);
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (1, 4, 1, 2);

-- TADS - 2º Semestre
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (1, 3, 2, 2);
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (1, 5, 2, 4);

-- TADS - 3º Semestre
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (1, 6, 3, 4);

-- GTI
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (2, 7, 1, 2);
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (2, 8, 2, 2);
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (2, 9, 3, 2);
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (2, 2, 1, 2);

-- Redes
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (3, 10, 1, 4);
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (3, 11, 2, 4);
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (3, 12, 3, 2);

-- Segurança
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (4, 13, 1, 2);
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (4, 14, 2, 4);
INSERT INTO course_subject (course_id, subject_id, semester_number, practical_lessons_count) VALUES (4, 15, 3, 2);

-- ============================
-- SEMESTRES ACADÊMICOS
-- ============================

INSERT INTO academic_semester (academic_year, status, course_id) VALUES (2025, 'ATIVO', 1);
INSERT INTO academic_semester (academic_year, status, course_id) VALUES (2025, 'ATIVO', 2);
INSERT INTO academic_semester (academic_year, status, course_id) VALUES (2025, 'ATIVO', 3);
INSERT INTO academic_semester (academic_year, status, course_id) VALUES (2025, 'PLANEJAMENTO', 4);
INSERT INTO academic_semester (academic_year, status, course_id) VALUES (2026, 'PLANEJAMENTO', 1);
INSERT INTO academic_semester (academic_year, status, course_id) VALUES (2026, 'PLANEJAMENTO', 2);

-- ============================
-- TURNOS E HORÁRIOS
-- ============================
-- ============================
-- TURNOS E HORÁRIOS
-- ============================


-- ============================
-- SALAS
-- ============================

INSERT INTO classroom (name, location, physical_resources, software_resources, capacity, template, practical) VALUES 
('Sala 101', 'Bloco A - 1º Andar', 'Projetor;Quadro Branco;Ar Condicionado', 'Navegador;Office', 40, false, false);

INSERT INTO classroom (name, location, physical_resources, software_resources, capacity, template, practical) VALUES 
('Laboratório 201', 'Bloco A - 2º Andar', 'Computadores;Projetor;Quadro', 'IDE;SGBD;Git', 30, false, true);

INSERT INTO classroom (name, location, physical_resources, software_resources, capacity, template, practical) VALUES 
('Laboratório 202', 'Bloco A - 2º Andar', 'Computadores;Bancadas;Projetor', 'IDE;Docker;Kubernetes', 25, false, true);

INSERT INTO classroom (name, location, physical_resources, software_resources, capacity, template, practical) VALUES 
('Sala 103', 'Bloco A - 1º Andar', 'Projetor;Sistema de Som;Quadro', 'Office;Navegador', 50, false, false);

INSERT INTO classroom (name, location, physical_resources, software_resources, capacity, template, practical) VALUES 
('Lab Redes 301', 'Bloco B - 3º Andar', 'Switches;Roteadores;Cabos;Computadores', 'Wireshark;Packet Tracer', 20, false, true);

INSERT INTO classroom (name, location, physical_resources, software_resources, capacity, template, practical) VALUES 
('Auditório Principal', 'Bloco C', 'Projetor;Microfones;Sistema de Som', 'Navegador', 100, false, false);

-- ============================
-- USUÁRIOS
-- ============================

INSERT INTO tbl_user (name, email, password, access_level_id) VALUES 
('Carlos Admin', 'admin@fatec.sp.gov.br', 'admin123', 1);

INSERT INTO tbl_user (name, email, password, access_level_id) VALUES 
('Maria Coordenadora', 'maria.coord@fatec.sp.gov.br', 'coord123', 2);

INSERT INTO tbl_user (name, email, password, access_level_id) VALUES 
('Prof. João Silva', 'joao.silva@fatec.sp.gov.br', 'prof123', 3);

INSERT INTO tbl_user (name, email, password, access_level_id) VALUES 
('Profa. Ana Santos', 'ana.santos@fatec.sp.gov.br', 'prof123', 3);

INSERT INTO tbl_user (name, email, password, access_level_id) VALUES 
('Prof. Pedro Costa', 'pedro.costa@fatec.sp.gov.br', 'prof123', 3);

INSERT INTO tbl_user (name, email, password, access_level_id) VALUES 
('Profa. Julia Lima', 'julia.lima@fatec.sp.gov.br', 'prof123', 3);

INSERT INTO tbl_user (name, email, password, access_level_id) VALUES 
('Prof. Roberto Alves', 'roberto.alves@fatec.sp.gov.br', 'prof123', 3);

INSERT INTO tbl_user (name, email, password, access_level_id) VALUES 
('Profa. Carla Souza', 'carla.souza@fatec.sp.gov.br', 'prof123', 3);

-- ============================
-- USER_SUBJECT (Professores x Disciplinas)
-- ============================

-- Prof. João ensina POO e Estruturas de Dados
INSERT INTO user_subject (user_id, subject_id) VALUES (3, 1);
INSERT INTO user_subject (user_id, subject_id) VALUES (3, 4);

-- Profa. Ana ensina Banco de Dados e Desenvolvimento Web
INSERT INTO user_subject (user_id, subject_id) VALUES (4, 2);
INSERT INTO user_subject (user_id, subject_id) VALUES (4, 5);

-- Prof. Pedro ensina Engenharia de Software e Gestão de Projetos
INSERT INTO user_subject (user_id, subject_id) VALUES (5, 3);
INSERT INTO user_subject (user_id, subject_id) VALUES (5, 7);

-- Profa. Julia ensina Dispositivos Móveis e Governança
INSERT INTO user_subject (user_id, subject_id) VALUES (6, 6);
INSERT INTO user_subject (user_id, subject_id) VALUES (6, 8);

-- Prof. Roberto ensina Redes e Linux
INSERT INTO user_subject (user_id, subject_id) VALUES (7, 10);
INSERT INTO user_subject (user_id, subject_id) VALUES (7, 11);

-- Profa. Carla ensina Segurança
INSERT INTO user_subject (user_id, subject_id) VALUES (8, 12);
INSERT INTO user_subject (user_id, subject_id) VALUES (8, 13);
INSERT INTO user_subject (user_id, subject_id) VALUES (8, 14);

-- ============================
-- COURSE_USER (Usuários x Cursos)
-- ============================

-- Coordenadora Maria coordena TADS e GTI
INSERT INTO course_user (user_id, course_id, role) VALUES (2, 1, 'COORDENADOR');
INSERT INTO course_user (user_id, course_id, role) VALUES (2, 2, 'COORDENADOR');

-- Prof. João leciona em TADS
INSERT INTO course_user (user_id, course_id, role) VALUES (3, 1, 'PROFESSOR');

-- Profa. Ana leciona em TADS
INSERT INTO course_user (user_id, course_id, role) VALUES (4, 1, 'PROFESSOR');

-- Prof. Pedro leciona em TADS e GTI
INSERT INTO course_user (user_id, course_id, role) VALUES (5, 1, 'PROFESSOR');
INSERT INTO course_user (user_id, course_id, role) VALUES (5, 2, 'PROFESSOR');

-- Profa. Julia leciona em GTI
INSERT INTO course_user (user_id, course_id, role) VALUES (6, 2, 'PROFESSOR');

-- Prof. Roberto leciona em Redes
INSERT INTO course_user (user_id, course_id, role) VALUES (7, 3, 'PROFESSOR');

-- Profa. Carla leciona em Segurança e Redes
INSERT INTO course_user (user_id, course_id, role) VALUES (8, 3, 'PROFESSOR');
INSERT INTO course_user (user_id, course_id, role) VALUES (8, 4, 'PROFESSOR');

-- ============================
-- DISPONIBILIDADES DE PROFESSORES
-- ============================

-- Prof. João - Segunda, Quarta e Sexta (manhã)
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (1, 1, 3);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (1, 2, 3);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (3, 1, 3);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (3, 2, 3);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (5, 1, 3);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (5, 2, 3);

-- Profa. Ana - Terça e Quinta (tarde)
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (2, 3, 4);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (2, 4, 4);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (4, 3, 4);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (4, 4, 4);

-- Prof. Pedro - Segunda a Sexta (noite)
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (1, 1, 5);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (2, 1, 5);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (3, 1, 5);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (4, 1, 5);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (5, 1, 5);

-- Profa. Julia - Segunda a Quarta (manhã e tarde)
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (1, 1, 6);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (1, 3, 6);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (2, 1, 6);
INSERT INTO user_availability (weekday, lesson_number, user_id) VALUES (3, 3, 6);

-- ============================
-- DIAS DA SEMANA
-- ============================

INSERT INTO weekday (id, name) VALUES (1, 'Segunda-feira');
INSERT INTO weekday (id, name) VALUES (2, 'Terça-feira');
INSERT INTO weekday (id, name) VALUES (3, 'Quarta-feira');
INSERT INTO weekday (id, name) VALUES (4, 'Quinta-feira');
INSERT INTO weekday (id, name) VALUES (5, 'Sexta-feira');
INSERT INTO weekday (id, name) VALUES (6, 'Sábado');
INSERT INTO weekday (id, name) VALUES (7, 'Domingo');