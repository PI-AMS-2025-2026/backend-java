package com.fatec.gini.dto.blocoHorario;

import java.time.LocalTime;

public record BlocoHorarioResponse(
        Long id,
        LocalTime horaInicio,
        LocalTime horaFim,
        Integer duracao
) {
} 