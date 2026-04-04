package com.fatec.horario.dto.curso;

import com.fatec.horario.domain.entities.Status;

public record CursoResponse(
        Long id,
        String nome,
        String periodicidade,
        Status status,
        Integer duracao) {
}