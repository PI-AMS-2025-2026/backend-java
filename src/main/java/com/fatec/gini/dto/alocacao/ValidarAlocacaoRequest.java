package com.fatec.gini.dto.alocacao;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.DiaSemanaDeserializer;
import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ValidarAlocacaoRequest(
        @NotNull(message = "Turma é obrigatória.") @Valid LongDTO turma,
        @NotNull(message = "Disciplina é obrigatória.") @Valid LongDTO disciplina,
        @NotNull(message = "Sala é obrigatória.") @Valid LongDTO sala,
        @NotNull(message = "Professor é obrigatório.") @Valid LongDTO professor,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        @JsonDeserialize(using = DiaSemanaDeserializer.class)
        @NotNull(message = "Dia da semana é obrigatório.") DiaSemana diaSemana,
        @NotNull(message = "Bloco de horário é obrigatório.") @Valid LongDTO horario,
        @NotNull(message = "Quadro de horários é obrigatório.") @Valid LongDTO quadroHorario,
        Long alocacaoId) {
}
