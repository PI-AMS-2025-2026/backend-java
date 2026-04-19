package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.DisponibilidadeProfessor;
import com.fatec.horario.dto.disponibilidadeProfessor.DisponibilidadeProfessorRequest;
import com.fatec.horario.dto.disponibilidadeProfessor.DisponibilidadeProfessorResponse;

public class DisponibilidadeProfessorMapper {

    public static DisponibilidadeProfessor toEntity(DisponibilidadeProfessorRequest request) {
        return new DisponibilidadeProfessor();
    }

    public static DisponibilidadeProfessorResponse toResponse(DisponibilidadeProfessor disponibilidade) {
        return new DisponibilidadeProfessorResponse(
                disponibilidade.getId(),
                disponibilidade.getUsuario() != null ? UsuarioMapper.toResponse(disponibilidade.getUsuario()) : null,
                disponibilidade.getDiaSemana() != null ? DiaSemanaMapper.toResponse(disponibilidade.getDiaSemana()) : null,
                disponibilidade.getHorario() != null ? HorarioMapper.toResponse(disponibilidade.getHorario()) : null);
    }
}
