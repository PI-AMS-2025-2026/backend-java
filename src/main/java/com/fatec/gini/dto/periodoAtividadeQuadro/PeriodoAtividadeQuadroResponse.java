package com.fatec.gini.dto.periodoAtividadeQuadro;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fatec.gini.domain.models.Status;

public record PeriodoAtividadeQuadroResponse(
        Long id,
        Integer ano,
        Integer periodo,
        LocalDate dataInicio,
        LocalDate dataFim,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {

}
