package com.fatec.gini.dto.alocacao;

import com.fatec.gini.domain.entities.DiaSemana;

public record AlocacaoLoteKey(
        Long turmaId,
        Long professorId,
        Long salaId,
        DiaSemana diaSemana,
        Long blocoHorarioId) {
}