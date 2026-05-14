package com.fatec.horario;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DataHorario {
    private final String turma;
    private final String data;
    private final List<String> diasSemana;
    private final List<LinhaHorario> linhas;

    public DataHorario() {
        this(
                "5º ANO",
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                List.of("SEGUNDA", "TERÇA", "QUARTA", "QUINTA", "SEXTA", "SÁBADO"),
                List.of(
                        new LinhaHorario(
                                "13h20 - 14h10",
                                List.of(
                                        new CelulaHorario("Programação Multiplataforma", "LAB INF 03", 2),
                                        new CelulaHorario("Segurança e Defesa Cibernética", "LAB INF 03", 2),
                                        new CelulaHorario("Sist. de Informação e Tecnologias Emergentes", "LAB INF 03", 2),
                                        new CelulaHorario("Programação Multiplataforma", "LAB INF 03", 2),
                                        new CelulaHorario("Estatística Aplicada", "LAB INF 03", 2),
                                        new CelulaHorario(null, null, 2))),
                        new LinhaHorario("14h10 - 15h00", List.of()),
                        new LinhaHorario(null, List.of(), true),
                        new LinhaHorario(
                                "15h10 - 16h00",
                                List.of(
                                        new CelulaHorario("Modelagem de Padrões de Projeto", "LAB INF 03", 2),
                                        new CelulaHorario("Sist. Distribuídos Aplicados à Internet das Coisas", "LAB INF 03", 2),
                                        new CelulaHorario("Business Intelligence e Big Data", "LAB INF 03", 2),
                                        new CelulaHorario("Inteligência Artificial e Aprendizagem de Máquina", "LAB INF 03", 2),
                                        new CelulaHorario("Computação em Nuvem", "LAB INF 03", 2),
                                        new CelulaHorario(null, null, 2))),
                        new LinhaHorario("16h00 - 16h50", List.of()),
                        new LinhaHorario(null, List.of(), true),
                        new LinhaHorario(
                                "17h00 - 17h50",
                                List.of(
                                        new CelulaHorario("Modelagem de Padrões de Projeto", "LAB INF 03", 2),
                                        new CelulaHorario("Língua Inglesa II", "SALA 11", 2),
                                        new CelulaHorario("Integração e Entrega Contínua (DevOps)", "LAB INF 03", 2),
                                        new CelulaHorario("Projeto Integrador II", "LAB INF 03", 2),
                                        new CelulaHorario("Projeto Integrador II", "LAB INF 03", 2),
                                        new CelulaHorario(null, null, 2))),
                        new LinhaHorario("17h50 - 18h40", List.of())));
    }

    public DataHorario(String turma, String data, List<String> diasSemana, List<LinhaHorario> linhas) {
        this.turma = turma;
        this.data = data;
        this.diasSemana = diasSemana;
        this.linhas = linhas;
    }

    public String getTurma() {
        return turma;
    }

    public String getData() {
        return data;
    }

    public List<String> getDiasSemana() {
        return diasSemana;
    }

    public List<LinhaHorario> getLinhas() {
        return linhas;
    }

    public static class LinhaHorario {
        private final String horario;
        private final List<CelulaHorario> celulas;
        private final boolean intervalo;

        public LinhaHorario(String horario, List<CelulaHorario> celulas) {
            this(horario, celulas, false);
        }

        public LinhaHorario(String horario, List<CelulaHorario> celulas, boolean intervalo) {
            this.horario = horario;
            this.celulas = celulas;
            this.intervalo = intervalo;
        }

        public String getHorario() {
            return horario;
        }

        public List<CelulaHorario> getCelulas() {
            return celulas;
        }

        public boolean isIntervalo() {
            return intervalo;
        }
    }

    public static class CelulaHorario {
        private final String disciplina;
        private final String sala;
        private final Integer rowSpan;

        public CelulaHorario(String disciplina, String sala, Integer rowSpan) {
            this.disciplina = disciplina;
            this.sala = sala;
            this.rowSpan = rowSpan;
        }

        public String getDisciplina() {
            return disciplina;
        }

        public String getSala() {
            return sala;
        }

        public Integer getRowSpan() {
            return rowSpan;
        }

        public boolean isVazia() {
            return disciplina == null || disciplina.isBlank();
        }
    }
    
}