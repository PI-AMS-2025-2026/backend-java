package com.fatec.gini.dto.disponibilidadeProfessor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.id.LongDTO;
import com.fatec.gini.dto.DiaSemanaDeserializer;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadeProfessorRequest(

    @NotNull(message = "Professor é obrigatório")
    LongDTO professor,

    @NotNull(message = "Dia da semana é obrigatório")
    @JsonDeserialize(using = DiaSemanaDeserializer.class)
    DiaSemana diaSemana,

    @NotNull(message = "Bloco Horário é obrigatório")
    LongDTO blocoHorario
) {}