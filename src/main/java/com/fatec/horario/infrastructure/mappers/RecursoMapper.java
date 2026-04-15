package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Recurso;
import com.fatec.horario.dto.recurso.RecursoRequest;
import com.fatec.horario.dto.recurso.RecursoResponse;

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
                entity.getTipo());
    }
}