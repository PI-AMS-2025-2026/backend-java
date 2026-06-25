package com.fatec.gini.dto.curso;

import java.time.LocalDateTime;

import com.fatec.gini.domain.models.Status;

public record CursoResponse(
                Long id,
                String nome,
                String periodicidade,
                Status status,
                Integer duracao,
                LocalDateTime created_at,
                LocalDateTime updated_at) {
}