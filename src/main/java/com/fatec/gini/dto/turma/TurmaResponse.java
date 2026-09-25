package com.fatec.gini.dto.turma;

import java.time.LocalDateTime;

import com.fatec.gini.dto.curso.CursoResponse;

public record TurmaResponse(
        Long id,
        String codigo,
        Integer periodo,
        Integer ano,
        Integer numeroAlunos,
        CursoResponse curso,
        LocalDateTime created_at,
        LocalDateTime updated_at) {
}