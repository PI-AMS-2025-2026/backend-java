package com.fatec.gini.dto.quadroHorario;

import java.time.LocalTime;

public record BlocoQuadroResponse(
        Long id,
        LocalTime horaInicio,
        LocalTime horaFim,
        String rotulo
) {
}
