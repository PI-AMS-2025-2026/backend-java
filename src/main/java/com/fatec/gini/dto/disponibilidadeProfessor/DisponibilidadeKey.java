package com.fatec.gini.dto.disponibilidadeProfessor;

import com.fatec.gini.domain.entities.DiaSemana;

public record DisponibilidadeKey(
        Long professorId,
        DiaSemana diaSemana,
        Long blocoHorarioId) {

}
