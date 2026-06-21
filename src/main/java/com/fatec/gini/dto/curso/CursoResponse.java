package com.fatec.gini.dto.curso;

import com.fatec.gini.domain.models.Status;

public record CursoResponse(
        Long id,
        String nome,
        String periodicidade,
        Status status,
        Integer duracao) {
}