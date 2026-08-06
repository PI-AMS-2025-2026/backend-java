package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.DisponibilidadeProfessor;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeProfessorRequest;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeProfessorResponse;

public class DisponibilidadeProfessorMapper {

    public static DisponibilidadeProfessor toEntity(DisponibilidadeProfessorRequest request) {
        return new DisponibilidadeProfessor();
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
