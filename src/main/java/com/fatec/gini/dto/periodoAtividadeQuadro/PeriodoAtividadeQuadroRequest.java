package com.fatec.gini.dto.periodoAtividadeQuadro;

import java.time.LocalDate;

import com.fatec.gini.domain.models.Status;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PeriodoAtividadeQuadroRequest(
        @NotNull(message = "O ano é obrigatório")
        @Positive(message = "O ano deve ser um número inteiro maior que zero")
        Integer ano,
        @NotNull(message = "O período é obrigatório")
        @Positive(message = "O período deve ser um número inteiro maior que zero")
        Integer periodo,
        @NotNull(message = "A data inicial é obrigatória")
        LocalDate dataInicio,
        @NotNull(message = "A data final é obrigatória")
        LocalDate dataFim,
        @NotNull(message = "O status é obrigatório")
        Status status
        ) {

}
