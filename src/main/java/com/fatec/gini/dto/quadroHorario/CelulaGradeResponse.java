package com.fatec.gini.dto.quadroHorario;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.grade.DisciplinaGradeResumoResponse;
import com.fatec.gini.dto.grade.SalaGradeResumoResponse;

public record CelulaGradeResponse(
        DiaSemana diaSemana,
        Long blocoHorarioId,
        DisciplinaGradeResumoResponse disciplina,
        SalaGradeResumoResponse sala,
        Long alocacaoId
) {
}
