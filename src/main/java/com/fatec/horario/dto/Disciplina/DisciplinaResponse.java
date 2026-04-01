package com.fatec.horario.dto.Disciplina;

public record DisciplinaResponse(
    Long idDisciplina,
    String nome,
    Integer cargaHoraria,
    String tipoDisciplina,
    Integer periodo,
    String modalidade,
    String codDisciplina,
    String cor,
    Long idCurso,
    Long idTipoSala
) {
}