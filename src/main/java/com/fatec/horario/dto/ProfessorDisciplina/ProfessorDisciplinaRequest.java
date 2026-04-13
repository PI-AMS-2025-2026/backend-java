package com.fatec.horario.dto.professorDisciplina;

import jakarta.validation.constraints.NotNull;

public record ProfessorDisciplinaRequest(
        @NotNull Long usuarioId,
        @NotNull Long disciplinaId
) {
}

