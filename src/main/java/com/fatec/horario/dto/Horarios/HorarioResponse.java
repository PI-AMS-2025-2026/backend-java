package com.fatec.horario.dto.Horarios;

import java.time.LocalTime;

public record HorarioResponse(
        Long id,
        LocalTime horaInicio,
        LocalTime horaFim,
        Integer duracao
) {
} 