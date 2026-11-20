package com.fatec.gini.dto.quadroHorario;

import java.util.List;

import com.fatec.gini.dto.curso.CursoResponse;

public record QuadroCursoResponse(
        CursoResponse curso,
        QuadroHorarioResponse quadroHorario,
        List<BlocoQuadroResponse> blocos,
        List<TurmaQuadroResponse> turmas
) {
}
