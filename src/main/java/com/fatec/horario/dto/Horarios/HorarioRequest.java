package com.fatec.horario.dto.Horarios;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;

public record HorarioRequest(
        @NotNull(message = "Hora de início é obrigatória")
        LocalTime horaInicio,

        @NotNull(message = "Hora de fim é obrigatória")
        LocalTime horaFim,

        @NotNull(message = "Duração é obrigatória")
        @Positive(message = "Duração deve ser positiva")
        Integer duracao
){
} 