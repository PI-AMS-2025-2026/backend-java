package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.diaSemana.DiaSemanaRequest;
import com.fatec.gini.dto.diaSemana.DiaSemanaResponse;

public class DiaSemanaMapper {

    public static DiaSemana toEntity(DiaSemanaRequest request) {
        DiaSemana entity = new DiaSemana();
        entity.setNome(request.nome());
        return entity;
    }

    public static DiaSemanaResponse toResponse(DiaSemana entity) {
     return new DiaSemanaResponse(
            entity.getId(),
            entity.getNome()
        );
    }
}
