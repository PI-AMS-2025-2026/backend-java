package com.fatec.gini.dto.blocoHorario;

import java.time.LocalTime;

public record BlocoHorarioKey(
        LocalTime horaInicio,
        LocalTime horaFim) {

}
