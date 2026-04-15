package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.dto.disponibilidadeProfessor.DisponibilidadeProfessorRequest;
import com.fatec.horario.dto.disponibilidadeProfessor.DisponibilidadeProfessorResponse;
import com.fatec.horario.domain.entities.DiaSemana;
import com.fatec.horario.domain.entities.Horario;
import com.fatec.horario.domain.entities.DisponibilidadeProfessor;

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
    

