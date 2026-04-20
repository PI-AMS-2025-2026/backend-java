package com.fatec.horario.dto.disciplina;

import com.fatec.horario.dto.curso.CursoResponse;
import com.fatec.horario.dto.tipoSala.TipoSalaResponse;

public record DisciplinaResponse(
    Long id,
    String nome,
    Integer cargaHoraria,
    String tipoDisciplina,
    Integer periodo,
    String modalidade,
    String codDisciplina,
    String cor,
    CursoResponse curso,
    TipoSalaResponse tipoSala
) {
}