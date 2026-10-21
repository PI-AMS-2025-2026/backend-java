package com.fatec.gini.dto.disciplina;

import java.time.LocalDateTime;

import com.fatec.gini.dto.curso.CursoResponse;
import com.fatec.gini.dto.tipoSala.TipoSalaResponse;

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
        TipoSalaResponse tipoSala,

        // Alteração: padroniza o campo da resposta para camelCase.
        LocalDateTime createdAt,

        // Alteração: padroniza o campo da resposta para camelCase.
        LocalDateTime updatedAt) {

}