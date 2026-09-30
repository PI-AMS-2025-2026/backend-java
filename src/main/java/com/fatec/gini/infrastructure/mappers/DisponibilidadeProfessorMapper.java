package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.DisponibilidadeProfessor;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeProfessorRequest;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeProfessorResponse;

public class DisponibilidadeProfessorMapper {

    public static DisponibilidadeProfessor toEntity(DisponibilidadeProfessorRequest request) {
        DisponibilidadeProfessor entity = new DisponibilidadeProfessor();

        // ALTERAÇÃO: cria apenas as referências das entidades usando seus IDs.
        if (request.professor() != null) {
            Professor professor = new Professor();
            professor.setId(request.professor().id());
            entity.setProfessor(professor);
        }

        // ALTERAÇÃO: atribui diretamente o enum DiaSemana recebido no payload.
        entity.setDiaSemana(request.diaSemana());

        if (request.blocoHorario() != null) {
            BlocoHorario blocoHorario = new BlocoHorario();
            blocoHorario.setId(request.blocoHorario().id());
            entity.setBlocoHorario(blocoHorario);
        }

        return entity;
    }

    public static DisponibilidadeProfessorResponse toResponse(DisponibilidadeProfessor entity) {
        return new DisponibilidadeProfessorResponse(
                entity.getId(),
                entity.getProfessor() != null ? ProfessorMapper.toResponse(entity.getProfessor()) : null,
                entity.getDiaSemana(),
                entity.getBlocoHorario() != null ? BlocoHorarioMapper.toResponse(entity.getBlocoHorario()) : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}