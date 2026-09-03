package com.fatec.gini.dto.disponibilidadeProfessor;

import java.time.LocalDateTime;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.blocoHorario.BlocoHorarioResponse;
import com.fatec.gini.dto.professor.ProfessorResponse;

public record DisponibilidadeProfessorResponse(

        Long id,

        // ALTERAÇÃO: mantém o mesmo conceito usado no Request e na entidade.
        ProfessorResponse professor,

        DiaSemana diaSemana,

        BlocoHorarioResponse blocoHorario,

        LocalDateTime created_at,

        LocalDateTime updated_at) {

}