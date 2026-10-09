package com.fatec.gini.dto.professorDisciplina;

public record ProfessorDisciplinaResponse(
        Long id,
        // Retorna apenas o ID do professor para simplificar o payload.
        Long professorId,
        // Retorna apenas o ID da disciplina para simplificar o payload.
        Long disciplinaId
        ) {

}
