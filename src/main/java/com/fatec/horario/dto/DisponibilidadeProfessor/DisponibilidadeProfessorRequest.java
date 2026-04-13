package com.fatec.horario.dto.DisponibilidadeProfessor;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadeProfessorRequest(
    @NotNull(message = "Usuário é obrigatório")
    Long idUsuario,

    @NotNull(message = "Dia da semana é obrigatório")
    Long idDiaSemana,

    @NotNull(message = "Horário é obrigatório")
    Long idHorario
){}
