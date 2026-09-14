package com.fatec.gini.dto.grade;

import com.fatec.gini.domain.models.Status;

public record QuadroHorarioGradeResumoResponse(
        Long id,
        Integer versao,
        Status status
) {
}
