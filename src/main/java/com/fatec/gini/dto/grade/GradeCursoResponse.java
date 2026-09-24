package com.fatec.gini.dto.grade;

import java.util.List;

import com.fatec.gini.dto.quadroHorario.BlocoQuadroResponse;
import com.fatec.gini.dto.quadroHorario.TurmaQuadroResponse;

public record GradeCursoResponse(
        CursoGradeResumoResponse curso,
        QuadroHorarioGradeResumoResponse quadroHorario,
        List<BlocoQuadroResponse> blocos,
        List<TurmaQuadroResponse> turmas
) {
}
