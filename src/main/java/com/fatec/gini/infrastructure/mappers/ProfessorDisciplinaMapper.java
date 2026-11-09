
package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.ProfessorDisciplina;
import com.fatec.gini.dto.professorDisciplina.ProfessorDisciplinaRequest;
import com.fatec.gini.dto.professorDisciplina.ProfessorDisciplinaResponse;

public class ProfessorDisciplinaMapper {

    public static ProfessorDisciplinaResponse toResponse(ProfessorDisciplina entity) {
        if (entity == null) {
            return null;
        }

        return new ProfessorDisciplinaResponse(
                entity.getId(),
                entity.getProfessor() != null
                        ? entity.getProfessor().getId()
                        : null,
                entity.getDisciplina() != null
                        ? entity.getDisciplina().getId()
                        : null,
                entity.getProfessor() != null
                        ? ProfessorMapper.toResponse(entity.getProfessor())
                        : null,
                entity.getDisciplina() != null
                        ? DisciplinaMapper.toResponse(entity.getDisciplina())
                        : null
        );
    }

    public static ProfessorDisciplina toEntity(ProfessorDisciplinaRequest request) {
        return new ProfessorDisciplina();
    }
}
