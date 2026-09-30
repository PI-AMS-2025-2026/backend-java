package com.fatec.gini.dto.blocoHorario;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record BlocoHorarioResponse(
        Long id,
        LocalTime horaInicio,
        LocalTime horaFim,
        Integer duracao,

        // Alteração: padroniza os nomes dos campos da resposta para camelCase.
        LocalDateTime createdAt,

        // Alteração: padroniza os nomes dos campos da resposta para camelCase.
        LocalDateTime updatedAt) {
}