package com.fatec.gini.dto.periodoAtividadeQuadro;

import java.time.LocalDate;

import com.fatec.gini.domain.models.Status;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PeriodoAtividadeQuadroRequest(
        @NotNull(message = "O ano é obrigatório")
        @Positive(message = "O ano deve ser um número inteiro maior que zero")
        @Min(value = 2026, message = "O ano deve ser maior ou igual a 2026")
        @Max(value = 9999, message = "O ano deve ser menor ou igual a 9999")
        Integer ano,
      
        @NotNull(message = "O período é obrigatório")
        @Positive(message = "O período deve ser um número inteiro maior que zero")
        @Min(value = 1, message = "O período deve ser maior ou igual a 1")
        @Max(value = 9, message = "O período deve ser menor ou igual a 9")
        Integer periodo,

        @NotNull(message = "A data inicial é obrigatória")
        LocalDate dataInicio,
        @NotNull(message = "A data final é obrigatória")
        LocalDate dataFim,
        @NotNull(message = "O status é obrigatório")
        Status status
        ) {

}
