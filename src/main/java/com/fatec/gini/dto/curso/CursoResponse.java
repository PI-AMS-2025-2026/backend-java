package com.fatec.gini.dto.curso;

import java.time.LocalDateTime;

import com.fatec.gini.domain.entities.Status;

public record CursoResponse(
        Long id,
        String nome,
        String periodicidade,
        Status status,
        Integer duracao,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}