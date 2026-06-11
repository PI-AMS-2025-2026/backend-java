package com.fatec.gini.dto.recurso;

import java.time.LocalDateTime;

public record RecursoResponse(
    Long id,
    String nome,
    String tipo,
    LocalDateTime created_at,
    LocalDateTime updated_at
) {
}

