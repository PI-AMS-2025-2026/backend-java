package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.DisponibilidadeProfessor;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeProfessorRequest;
import com.fatec.gini.dto.disponibilidadeProfessor.DisponibilidadeProfessorResponse;

public class DisponibilidadeProfessorMapper {

    public static DisponibilidadeProfessor toEntity(DisponibilidadeProfessorRequest request) {
        return new DisponibilidadeProfessor();
    }

    public static DisponibilidadeProfessorResponse toResponse(DisponibilidadeProfessor disponibilidade) {
        return new DisponibilidadeProfessorResponse(
                disponibilidade.getId(),
                disponibilidade.getProfessor() != null ? ProfessorMapper.toResponse(disponibilidade.getProfessor()) : null,
                disponibilidade.getDiaSemana() != null ? DiaSemanaMapper.toResponse(disponibilidade.getDiaSemana()) : null,
                disponibilidade.getBlocoHorario() != null ? BlocoHorarioMapper.toResponse(disponibilidade.getBlocoHorario()) : null);
    }
}
