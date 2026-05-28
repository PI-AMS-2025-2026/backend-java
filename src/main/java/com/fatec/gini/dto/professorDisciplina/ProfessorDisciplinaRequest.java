package com.fatec.gini.dto.professorDisciplina;

import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;

public record ProfessorDisciplinaRequest(
        @NotNull LongDTO usuario,
        @NotNull LongDTO disciplina
) {
}

