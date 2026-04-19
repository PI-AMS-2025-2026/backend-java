package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.*;
import com.fatec.horario.dto.professorDisciplina.ProfessorDisciplinaRequest;
import com.fatec.horario.dto.professorDisciplina.ProfessorDisciplinaResponse;

public class ProfessorDisciplinaMapper {

    public static ProfessorDisciplinaResponse toResponse(ProfessorDisciplina entity) {
        return new ProfessorDisciplinaResponse(         
        );
    }

    public static ProfessorDisciplina toEntity(ProfessorDisciplinaRequest request) {
        return new ProfessorDisciplina();
    }
}
