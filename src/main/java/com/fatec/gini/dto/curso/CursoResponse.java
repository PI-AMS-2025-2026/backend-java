package com.fatec.gini.dto.curso;

import com.fatec.gini.domain.entities.Status;

public record CursoResponse(
        Long id,
        String nome,
        String periodicidade,
        Status status,
        Integer duracao) {
}