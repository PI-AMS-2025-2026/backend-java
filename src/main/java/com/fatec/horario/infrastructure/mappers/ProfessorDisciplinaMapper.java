package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.ProfessorDisciplina;
import com.fatec.horario.dto.professorDisciplina.ProfessorDisciplinaRequest;
import com.fatec.horario.dto.professorDisciplina.ProfessorDisciplinaResponse;

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
