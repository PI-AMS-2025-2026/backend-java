package com.fatec.gini.dto.professorDisciplina;

import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;

public record ProfessorDisciplinaRequest(
        @NotNull(message = "Professor é obrigatório")
        LongDTO professor,
        @NotNull(message = "Disciplina é obrigatória")
        LongDTO disciplina
        ) {

}
