package com.fatec.gini.dto.disponibilidadeProfessor;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadeProfessorRequest(

    @NotNull(message = "Professor é obrigatório")
    LongDTO professor,

    // ALTERAÇÃO: DiaSemana é um enum, não uma referência por ID.
    @NotNull(message = "Dia da semana é obrigatório")
    DiaSemana diaSemana,

    @NotNull(message = "Bloco Horário é obrigatório")
    LongDTO blocoHorario
) {}