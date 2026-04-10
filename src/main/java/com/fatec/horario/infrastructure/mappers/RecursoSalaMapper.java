package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.RecursoSala;
import com.fatec.horario.dto.RecursoSala.RecursoSalaResponse;

public class RecursoSalaMapper {

    public static RecursoSalaResponse toResponse(RecursoSala entity) {
        return new RecursoSalaResponse(
                entity.getId(),
                entity.getSala().getId(),
                entity.getRecurso().getId(),
                entity.getQuantidade()
        );
    }
}