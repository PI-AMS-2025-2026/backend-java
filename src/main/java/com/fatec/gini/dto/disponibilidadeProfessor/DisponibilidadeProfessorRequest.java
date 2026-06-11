package com.fatec.gini.dto.disponibilidadeProfessor;

import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadeProfessorRequest(
    @NotNull(message = "Professor é obrigatório")
    LongDTO professor,

    @NotNull(message = "Dia da semana é obrigatório")
    LongDTO diaSemana,

    @NotNull(message = "Bloco Horário é obrigatório")
    LongDTO blocoHorario
){}
