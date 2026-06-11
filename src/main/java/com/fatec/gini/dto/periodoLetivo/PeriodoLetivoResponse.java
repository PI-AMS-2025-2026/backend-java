package com.fatec.gini.dto.periodoLetivo;

import java.time.LocalDate;

import com.fatec.gini.domain.entities.Status;

public record PeriodoLetivoResponse(
        Long idPeriodoLetivo,
        Integer ano,
        Integer periodo,
        LocalDate dataInicio,
        LocalDate dataFim,
        Status status) {
}
