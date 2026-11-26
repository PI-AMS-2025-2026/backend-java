package com.fatec.gini.dto.grade;

import java.util.List;

import com.fatec.gini.dto.quadroHorario.BlocoQuadroResponse;
import com.fatec.gini.dto.quadroHorario.TurmaQuadroResponse;
import com.fatec.gini.domain.entities.DiaSemana;

public record GradeCursoResponse(
        CursoGradeResumoResponse curso,
        QuadroHorarioGradeResumoResponse quadroHorario,
        List<BlocoQuadroResponse> blocos,
        List<TurmaQuadroResponse> turmas,
        List<DiaSemana> dias
) {
    public GradeCursoResponse(CursoGradeResumoResponse curso, QuadroHorarioGradeResumoResponse quadroHorario,
            List<BlocoQuadroResponse> blocos, List<TurmaQuadroResponse> turmas) {
        this(curso, quadroHorario, blocos, turmas, List.of());
    }
}
