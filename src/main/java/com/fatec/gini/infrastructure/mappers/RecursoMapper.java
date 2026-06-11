package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.Recurso;
import com.fatec.gini.dto.recurso.RecursoRequest;
import com.fatec.gini.dto.recurso.RecursoResponse;

public class RecursoMapper {

    public static Recurso toEntity(RecursoRequest request) {
        Recurso entity = new Recurso();
        entity.setNome(request.nome());
        entity.setTipo(request.tipo());
        return entity;
    }

    public static RecursoResponse toResponse(Recurso entity) {
        return new RecursoResponse(
                entity.getId(),
                entity.getNome(),
                entity.getTipo(),
                entity.getCreated_at(),
                entity.getUpdated_at());
    }
}