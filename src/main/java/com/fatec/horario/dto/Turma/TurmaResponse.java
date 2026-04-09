package com.fatec.horario.dto.Turma;

public record TurmaResponse(
    Long id,
    String codigo,
    Integer periodo,
    Integer ano,
    Integer numeroAlunos,
    Long idCurso
) {}