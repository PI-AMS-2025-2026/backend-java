package com.fatec.gini.domain.services.export;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.grade.GradeCursoResponse;
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
                .map(turma -> criarTurma(turma, grade.blocos(), dias))
                .toList();

        return new GradeData(grade, dias, turmas);
    }

    private static TurmaData criarTurma(TurmaQuadroResponse turma, List<BlocoQuadroResponse> blocos,
            List<DiaData> dias) {
        Map<String, CelulaGradeResponse> celulas = turma.celulas().stream()
                .collect(Collectors.toMap(celula -> chave(celula.diaSemana(), celula.blocoHorarioId()),
                        Function.identity(), (primeira, ignorada) -> primeira));

        List<LinhaData> linhas = blocos.stream()
                .map(bloco -> new LinhaData(bloco, dias.stream()
                        .map(dia -> new CelulaData(celulas.get(chave(dia.valor(), bloco.id()))))
                        .toList()))
                .toList();

        return new TurmaData(turma, linhas);
    }

    private static String chave(DiaSemana dia, Long blocoId) {
        return dia + ":" + blocoId;
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

    record TurmaData(TurmaQuadroResponse turma, List<LinhaData> linhas) {
    }

    record LinhaData(BlocoQuadroResponse bloco, List<CelulaData> celulas) {
    }

    record CelulaData(CelulaGradeResponse valor) {
        public boolean preenchida() {
            return valor != null && valor.disciplina() != null;
        }
    }
}