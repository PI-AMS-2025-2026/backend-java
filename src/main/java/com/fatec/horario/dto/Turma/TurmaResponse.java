package com.fatec.horario.dto.Turma;

public record TurmaResponse(

        Long idTurma,

        String codigo,

        Integer periodo,

        Integer ano,

        Integer numeroAlunos,

        Long idCurso

) {
}
