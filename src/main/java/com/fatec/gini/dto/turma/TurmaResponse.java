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

    // Data de criação
    LocalDateTime createdAt,

    // Data da última atualização
    LocalDateTime updatedAt
) {}