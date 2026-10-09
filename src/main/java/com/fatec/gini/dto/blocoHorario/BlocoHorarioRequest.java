package com.fatec.gini.dto.blocoHorario;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record BlocoHorarioRequest(
        @NotNull(message = "Hora de início é obrigatória")
        LocalTime horaInicio,

        @NotNull(message = "Hora de fim é obrigatória")
        LocalTime horaFim

){
} 