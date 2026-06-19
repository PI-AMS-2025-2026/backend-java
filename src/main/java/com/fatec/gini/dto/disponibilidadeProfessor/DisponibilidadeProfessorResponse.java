package com.fatec.gini.dto.disponibilidadeProfessor;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.blocoHorario.BlocoHorarioResponse;
import com.fatec.gini.dto.professor.ProfessorResponse;

public record DisponibilidadeProfessorResponse(
        Long id,
        ProfessorResponse usuario,
        DiaSemana diaSemana,
        BlocoHorarioResponse blocoHorario) {

}
