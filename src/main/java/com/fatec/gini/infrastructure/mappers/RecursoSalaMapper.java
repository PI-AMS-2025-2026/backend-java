package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.RecursoSala;
import com.fatec.gini.dto.recursoSala.RecursoSalaRequest;
import com.fatec.gini.dto.recursoSala.RecursoSalaResponse;


public class RecursoSalaMapper {

    public static RecursoSala toEntity(RecursoSalaRequest request) {
        RecursoSala entity = new RecursoSala();
        entity.setQuantidade(request.quantidade());
        return entity;
    }

    public static RecursoSalaResponse toResponse(RecursoSala entity) {
        return new RecursoSalaResponse(
                entity.getId(),
                entity.getSala() != null ? SalaMapper.toResponse(entity.getSala()) : null,
                entity.getRecurso() != null ? RecursoMapper.toResponse(entity.getRecurso()) : null,
                entity.getQuantidade()
        );
    }
}