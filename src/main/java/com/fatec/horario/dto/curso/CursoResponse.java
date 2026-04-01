package com.fatec.horario.dto.curso;

public record CursoResponse(
        Long id,
        String nome,
        String periodicidade,
        String status,
        Integer duracao) {
}