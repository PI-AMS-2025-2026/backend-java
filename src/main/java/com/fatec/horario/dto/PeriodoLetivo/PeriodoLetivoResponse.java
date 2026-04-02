package com.fatec.horario.dto.PeriodoLetivo;

import java.time.LocalDate;

public record PeriodoLetivoResponse(
        Long idPeriodoLetivo,
        Integer ano,
        Integer periodo,
        LocalDate dataInicio,
        LocalDate dataFim,
        String status) {
}
