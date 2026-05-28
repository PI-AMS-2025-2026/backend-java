package com.fatec.gini.dto.horarios;

import java.time.LocalTime;

public record HorarioResponse(
        Long id,
        LocalTime horaInicio,
        LocalTime horaFim,
        Integer duracao
) {
} 