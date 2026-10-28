package com.fatec.gini.dto.grade;

import java.time.LocalTime;

public record BlocoGradeSimplesResponse(
        Long id,
        LocalTime horaInicio,
        LocalTime horaFim
) {
}
