package com.fatec.horario.dto.periodoLetivo;

import java.time.LocalDate;

import com.fatec.horario.domain.entities.Status;

public record PeriodoLetivoResponse(
        Long idPeriodoLetivo,
        Integer ano,
        Integer periodo,
        LocalDate dataInicio,
        LocalDate dataFim,
        Status status) {
}
