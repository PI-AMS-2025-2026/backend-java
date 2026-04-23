package com.fatec.horario.dto.turma;

import com.fatec.horario.dto.curso.CursoResponse;

public record TurmaResponse(
    Long id,
    String codigo,
    Integer periodo,
    Integer ano,
    Integer numeroAlunos,
    CursoResponse curso
) {}