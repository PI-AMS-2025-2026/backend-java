package com.fatec.gini.dto.periodoAtividadeQuadro;

import java.time.LocalDate;

import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoPeridoAtividadeQuadro;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PeriodoAtividadeQuadroRequest(
        // Ano letivo ao qual pertence o período de vigência da grade.
        @NotNull(message = "Ano é obrigatório.")
        @Positive(message = "Ano deve ser um número inteiro maior que zero.")
        @Min(value = 2008, message = "Ano deve ser maior ou igual a 2008.") 
        @Max(value = 9999, message = "Ano deve ser menor ou igual a 9999.")
        Integer ano,

        // Posição do período dentro do ano: 1 ou 2 para semestral; 1 para anual.
        @NotNull(message = "Período é obrigatório.")
        @Positive(message = "Período deve ser um número inteiro maior que zero.")
        @Min(value = 1, message = "Período deve ser maior ou igual a 1.")
        @Max(value = 2, message = "Período deve ser menor ou igual a 2.")
        Integer periodo,

        // Tipo da vigência do quadro; define como interpretar o campo periodo.
        @NotNull(message = "Tipo do período é obrigatório.")
        TipoPeridoAtividadeQuadro tipoPeridoAtividadeQuadro,

        // Primeiro dia em que o quadro pode ser usado; deve pertencer ao ano informado.
        @NotNull(message = "Data inicial é obrigatória.")
        LocalDate dataInicio,

        // Último dia em que o quadro pode ser usado; deve pertencer ao ano informado.
        @NotNull(message = "Data final é obrigatória.")
        LocalDate dataFim,

        // Estado administrativo do período de vigência.
        @NotNull(message = "Status é obrigatório.")
        Status status
        ) {

}
