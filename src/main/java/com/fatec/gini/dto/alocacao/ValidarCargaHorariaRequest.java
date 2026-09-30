package com.fatec.gini.dto.alocacao;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;

public record ValidarCargaHorariaRequest(

        @NotNull(message = "Professor é obrigatório")
        LongDTO professor,

        @NotNull(message = "Dia da semana é obrigatório")
        DiaSemana diaSemana,

        @NotNull(message = "Bloco horário é obrigatório")
        LongDTO horario) {
}
