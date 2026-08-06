package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.dto.disciplina.DisciplinaRequest;
import com.fatec.gini.dto.disciplina.DisciplinaResponse;

public class DisciplinaMapper {

    public static Disciplina toEntity(DisciplinaRequest request) {

        Disciplina entity = new Disciplina();

        entity.setNome(request.nome());
        entity.setCargaHoraria(request.cargaHoraria());
        entity.setTipoDisciplina(request.tipoDisciplina());
        entity.setPeriodo(request.periodo());
        entity.setModalidade(request.modalidade());
        entity.setCodDisciplina(request.codDisciplina());
        entity.setCor(request.cor());

        return entity;
    }

    public static DisciplinaResponse toResponse(Disciplina entity) {

        return new DisciplinaResponse(
                entity.getId(),
                entity.getNome(),
                entity.getCargaHoraria(),
                entity.getTipoDisciplina(),
                entity.getPeriodo(),
                entity.getModalidade(),
                entity.getCodDisciplina(),
                entity.getCor(),
                entity.getCurso() != null ? CursoMapper.toResponse(entity.getCurso()) : null,
                entity.getTipoSala() != null ? TipoSalaMapper.toResponse(entity.getTipoSala()) : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}