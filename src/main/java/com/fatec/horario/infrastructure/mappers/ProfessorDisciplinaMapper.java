package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.*;
import com.fatec.horario.dto.ProfessorDisciplina.ProfessorDisciplinaRequest;
import com.fatec.horario.dto.ProfessorDisciplina.ProfessorDisciplinaResponse;

public class ProfessorDisciplinaMapper {

    public static ProfessorDisciplinaResponse toResponse(ProfessorDisciplina entity) {
        return new ProfessorDisciplinaResponse(
                entity.getIdProfessorDisciplina(),
                entity.getUsuario().getUsuarioId(),
                entity.getDisciplina().getIdDisciplina()
        );
    }

    public static ProfessorDisciplina toEntity(
            ProfessorDisciplinaRequest request,
            Usuario usuario,
            Disciplina disciplina) {

        return new ProfessorDisciplina(usuario, disciplina);
    }
}
