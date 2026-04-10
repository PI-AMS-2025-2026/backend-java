package com.fatec.horario.dto.disciplina;

public record DisciplinaResponse(
    Long id,
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