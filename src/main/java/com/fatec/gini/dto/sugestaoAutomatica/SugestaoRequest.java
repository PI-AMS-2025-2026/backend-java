package com.fatec.gini.dto.sugestaoAutomatica;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;

public record SugestaoRequest(

        @NotNull(message = "Turma é obrigatória")
        LongDTO turma,

        @NotNull(message = "Disciplina é obrigatória")
        LongDTO disciplina,

        @NotNull(message = "Sala é obrigatória")
        LongDTO sala,

        @NotNull(message = "Professor é obrigatório")
        LongDTO professor,

        @NotNull(message = "Dia da semana é obrigatório")
        DiaSemana diaSemana,

        @NotNull(message = "Horário é obrigatório")
        LongDTO horario,

        @NotNull(message = "Quadro horário é obrigatório")
        LongDTO quadroHorario,

        @NotNull(message = "Usuário responsável pela alteração é obrigatório")
        LongDTO usuarioAlteracao

) {
}