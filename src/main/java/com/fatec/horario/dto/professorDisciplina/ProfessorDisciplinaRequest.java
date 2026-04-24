package com.fatec.horario.dto.professorDisciplina;

import com.fatec.horario.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;

public record ProfessorDisciplinaRequest(
        @NotNull LongDTO usuario,
        @NotNull LongDTO disciplina
) {
}

