package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.RecursoSala;
import com.fatec.horario.dto.recursoSala.RecursoSalaRequest;
import com.fatec.horario.dto.recursoSala.RecursoSalaResponse;;


public class RecursoSalaMapper {

    public static RecursoSala toEntity(RecursoSalaRequest request) {
        RecursoSala entity = new RecursoSala();
        entity.setQuantidade(request.quantidade());
        return entity;
    }

    public static RecursoSalaResponse toResponse(RecursoSala entity) {
        return new RecursoSalaResponse(
                entity.getId(),
                entity.getSala().getId(),
                entity.getRecurso().getId(),
                entity.getQuantidade()
        );
    }
}