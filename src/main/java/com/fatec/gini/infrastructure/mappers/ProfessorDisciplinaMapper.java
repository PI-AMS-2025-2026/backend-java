package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.ProfessorDisciplina;
import com.fatec.gini.dto.professorDisciplina.ProfessorDisciplinaRequest;
import com.fatec.gini.dto.professorDisciplina.ProfessorDisciplinaResponse;

public class ProfessorDisciplinaMapper {

    public static ProfessorDisciplinaResponse toResponse(ProfessorDisciplina entity) {
        return new ProfessorDisciplinaResponse(
                entity.getId(),
                entity.getUsuario() != null ? UsuarioMapper.toResponse(entity.getUsuario()) : null,
                entity.getDisciplina() != null ? DisciplinaMapper.toResponse(entity.getDisciplina()) : null
        );
    }

    public static ProfessorDisciplina toEntity(ProfessorDisciplinaRequest request) {
        return new ProfessorDisciplina();
    }
}
