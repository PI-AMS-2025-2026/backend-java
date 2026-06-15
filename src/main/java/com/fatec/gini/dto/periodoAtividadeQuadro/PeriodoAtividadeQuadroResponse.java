package com.fatec.gini.dto.periodoAtividadeQuadro;

import java.time.LocalDate;

import com.fatec.gini.domain.entities.Status;

public record PeriodoAtividadeQuadroResponse(
        Long idPeriodoAtividadeQuadro,
        Integer ano,
        Integer periodo,
        LocalDate dataInicio,
        LocalDate dataFim,
        Status status) {
}
