package com.fatec.gini.dto.grade;

import java.util.List;

public record GradeTurmaResponse(
        CursoGradeResumoResponse curso,
        TurmaGradeResumoResponse turma,
        QuadroHorarioGradeResumoResponse quadroHorario,
        List<HorarioTurmaGradeResponse> horarios
) {
}
