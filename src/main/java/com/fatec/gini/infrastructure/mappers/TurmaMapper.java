package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.dto.turma.TurmaRequest;
import com.fatec.gini.dto.turma.TurmaResponse;

public class TurmaMapper {

    public static Turma toEntity(TurmaRequest request) {
        Turma entity = new Turma();
        entity.setCodigo(request.codigo());
        entity.setPeriodo(request.periodo());
        entity.setAno(request.ano());
        entity.setNumeroAlunos(request.numeroAlunos());
        return entity;
    }

    public static TurmaResponse toResponse(Turma entity) {
        return new TurmaResponse(
            entity.getId(),
            entity.getCodigo(),
            entity.getPeriodo(),
            entity.getAno(),
            entity.getNumeroAlunos(),
            entity.getCurso() != null ? CursoMapper.toResponse(entity.getCurso()) : null
        );
    }
}