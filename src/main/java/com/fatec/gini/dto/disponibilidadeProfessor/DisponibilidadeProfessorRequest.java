package com.fatec.gini.dto.disponibilidadeProfessor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadeProfessorRequest(

    @NotNull(message = "Professor é obrigatório")
    LongDTO professor,

    // CORREÇÃO: obriga o Jackson a aceitar diaSemana somente como string (ex: "SEGUNDA"),
    // bloqueando o envio por número/ordinal (ex: 0), que antes era aceito silenciosamente
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "Dia da semana é obrigatório")
    DiaSemana diaSemana,

    @NotNull(message = "Bloco Horário é obrigatório")
    LongDTO blocoHorario
) {}