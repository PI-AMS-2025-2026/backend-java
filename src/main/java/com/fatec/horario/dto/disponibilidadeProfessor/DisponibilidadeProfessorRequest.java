package com.fatec.horario.dto.disponibilidadeProfessor;

import com.fatec.horario.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadeProfessorRequest(
    @NotNull(message = "Usuário é obrigatório")
    LongDTO usuario,

    @NotNull(message = "Dia da semana é obrigatório")
    LongDTO diaSemana,

    @NotNull(message = "Horário é obrigatório")
    LongDTO horario
){}
