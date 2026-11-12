package com.fatec.gini.dto.grade;

import com.fatec.gini.domain.entities.DiaSemana;

public record HorarioTurmaGradeResponse(
        DiaSemana diaSemana,
        BlocoGradeSimplesResponse blocoHorario,
        DisciplinaGradeResumoResponse disciplina,
        SalaGradeResumoResponse sala,
        Long alocacaoId
) {
}
