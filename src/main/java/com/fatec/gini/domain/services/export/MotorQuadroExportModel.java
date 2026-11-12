package com.fatec.gini.domain.services.export;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.grade.GradeCursoResponse;
import com.fatec.gini.dto.grade.CursoGradeResumoResponse;
import com.fatec.gini.dto.quadroHorario.BlocoQuadroResponse;
import com.fatec.gini.dto.quadroHorario.CelulaGradeResponse;
import com.fatec.gini.dto.quadroHorario.TurmaQuadroResponse;

final class MotorQuadroExportModel {

    private MotorQuadroExportModel() {
    }

    static GradeData from(GradeCursoResponse grade) {
        List<DiaData> dias = Arrays.stream(DiaSemana.values())
                .filter(dia -> grade.turmas().stream()
                        .flatMap(turma -> turma.celulas().stream())
                        .anyMatch(celula -> celula.diaSemana() == dia))
                .map(dia -> new DiaData(dia, formatarDia(dia)))
                .toList();

        List<TurmaData> turmas = grade.turmas().stream()
                .map(turma -> criarTurma(turma, grade.blocos(), dias, grade.curso()))
                .toList();

        return new GradeData(grade, dias, turmas);
    }

    private static TurmaData criarTurma(TurmaQuadroResponse turma, List<BlocoQuadroResponse> blocos,
            List<DiaData> dias, CursoGradeResumoResponse curso) {
        Map<String, CelulaGradeResponse> celulas = turma.celulas().stream()
                .collect(Collectors.toMap(celula -> chave(celula.diaSemana(), celula.blocoHorarioId()),
                        Function.identity(), (primeira, ignorada) -> primeira));

        // O motor do quadro devolve uma matriz global, preenchendo combinações sem
        // alocação com células vazias. No exportador, a linha deve representar um
        // bloco realmente usado pela turma; caso contrário uma turma parcial ou de
        // outro turno ganha horários que não existem em sua grade.
        List<LinhaData> linhas = blocos.stream()
                .filter(bloco -> turma.celulas().stream()
                        .anyMatch(celula -> mesmoBloco(celula, bloco) && celula.disciplina() != null))
                .map(bloco -> new LinhaData(bloco, dias.stream()
                        .map(dia -> new CelulaData(celulas.get(chave(dia.valor(), bloco.id()))))
                        .toList()))
                .toList();

        return new TurmaData(turma, linhas, formatarTituloTurma(turma, curso));
    }

    private static String formatarTituloTurma(TurmaQuadroResponse turma, CursoGradeResumoResponse curso) {
        String unidadeEtapa = "período";
        if (curso.periodicidade() != null && curso.periodicidade().equalsIgnoreCase("Anual")) {
            unidadeEtapa = "ano";
        } else if (curso.periodicidade() != null && curso.periodicidade().equalsIgnoreCase("Semestral")) {
            unidadeEtapa = "semestre";
        }
        String identificacaoTurma = turma.codigo() == null ? String.valueOf(turma.id()) : turma.codigo();
        int separadorCodigo = identificacaoTurma.indexOf('/');
        if (separadorCodigo > 0) {
            identificacaoTurma = identificacaoTurma.substring(0, separadorCodigo);
        }
        return "Turma " + identificacaoTurma + " - " + turma.ano() + " / "
                + turma.periodo() + "º " + unidadeEtapa;
    }

    private static String chave(DiaSemana dia, Long blocoId) {
        return dia + ":" + blocoId;
    }

    private static boolean mesmoBloco(CelulaGradeResponse celula, BlocoQuadroResponse bloco) {
        return celula.blocoHorarioId() != null && celula.blocoHorarioId().equals(bloco.id());
    }

    private static String formatarDia(DiaSemana dia) {
        return switch (dia) {
            case SEGUNDA -> "Segunda";
            case TERÇA -> "Terça";
            case QUARTA -> "Quarta";
            case QUINTA -> "Quinta";
            case SEXTA -> "Sexta";
            case SÁBADO -> "Sábado";
            case DOMINGO -> "Domingo";
        };
    }

    record GradeData(GradeCursoResponse grade, List<DiaData> dias, List<TurmaData> turmas) {
    }

    record DiaData(DiaSemana valor, String nome) {
    }

    record TurmaData(TurmaQuadroResponse turma, List<LinhaData> linhas, String titulo) {
    }

    record LinhaData(BlocoQuadroResponse bloco, List<CelulaData> celulas) {
    }

    record CelulaData(CelulaGradeResponse valor) {
        public boolean preenchida() {
            return valor != null && valor.disciplina() != null;
        }
    }
}
