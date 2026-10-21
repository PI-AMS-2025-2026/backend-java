package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.ProfessorDisciplina;
import com.fatec.gini.dto.professorDisciplina.ProfessorDisciplinaRequest;
import com.fatec.gini.dto.professorDisciplina.ProfessorDisciplinaResponse;

public class ProfessorDisciplinaMapper {

    public static ProfessorDisciplinaResponse toResponse(ProfessorDisciplina entity) {
        return new ProfessorDisciplinaResponse(
                entity.getId(),
                // O payload retorna somente o ID do professor relacionado.
                entity.getProfessor() != null ? entity.getProfessor().getId() : null,
                // O payload retorna somente o ID da disciplina relacionada.
                entity.getDisciplina() != null ? entity.getDisciplina().getId() : null
        );
    }

    public static ProfessorDisciplina toEntity(ProfessorDisciplinaRequest request) {
        return new ProfessorDisciplina();
    }
}
