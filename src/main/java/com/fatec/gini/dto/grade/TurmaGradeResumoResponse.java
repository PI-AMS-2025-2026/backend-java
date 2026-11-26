package com.fatec.gini.dto.grade;

public record TurmaGradeResumoResponse(
        Long id,
        String codigo,
        Integer ano,
        Integer periodo
) {
}
