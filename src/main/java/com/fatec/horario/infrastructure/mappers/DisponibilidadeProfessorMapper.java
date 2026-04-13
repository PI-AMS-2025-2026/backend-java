package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.domain.entities.DiaSemana;
import com.fatec.horario.domain.entities.Horario;
import com.fatec.horario.domain.entities.DisponibilidadeProfessor;
import com.fatec.horario.dto.DisponibilidadeProfessor.DisponibilidadeProfessorRequest;
import com.fatec.horario.dto.DisponibilidadeProfessor.DisponibilidadeProfessorResponse;

public class DisponibilidadeProfessorMapper{

public static DisponibilidadeProfessor toEntity(DisponibilidadeProfessorRequest request, Usuario usuario, DiaSemana diaSemana, Horario horario) {
        DisponibilidadeProfessor disponibilidade = new DisponibilidadeProfessor();
        disponibilidade.setUsuario(usuario);
        disponibilidade.setDiaSemana(diaSemana);
        disponibilidade.setHorario(horario);
        return disponibilidade;
    }

    public static DisponibilidadeProfessorResponse toResponse(DisponibilidadeProfessor disponibilidade) {
        return new DisponibilidadeProfessorResponse(
                disponibilidade.getId(),
                disponibilidade.getUsuario().getId(),
                disponibilidade.getDiaSemana().getId(),
                disponibilidade.getHorario().getId()
        );
    }
}
    

