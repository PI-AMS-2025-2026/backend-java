package com.fatec.horario.dto.periodoLetivo;

import java.time.LocalDate;

import com.fatec.horario.domain.entities.Status;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PeriodoLetivoRequest(

        @NotNull(message = "O ano é obrigatório") @Positive(message = "O ano deve ser um número inteiro maior que zero") Integer ano,

        @NotNull(message = "O período é obrigatório") @Positive(message = "O período deve ser um número inteiro maior que zero") Integer periodo,

        @NotNull(message = "A data inicial é obrigatória") LocalDate dataInicio,

        @NotNull(message = "A data final é obrigatória") LocalDate dataFim,

        @NotNull(message = "O status é obrigatório") Status status) {
    // TODO:retirar validação e colocar em service correto
    @AssertTrue(message = "A data final deve ser maior que a data inicial")
    public boolean isDataFimValida() {
        if (dataInicio == null || dataFim == null) {
            return true;
        }
        return dataFim.isAfter(dataInicio);
    }
}
