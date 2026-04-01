package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.DiaSemana;
import com.fatec.horario.dto.diaSemana.DiaSemanaRequest;
import com.fatec.horario.dto.diaSemana.DiaSemanaResponse;

public class DiaSemanaMapper {

    public static DiaSemana toEntity(DiaSemanaRequest request) {
        DiaSemana entity = new DiaSemana();
        entity.setNome(request.getNome());
        return entity;
    }

    public static DiaSemanaResponse toResponse(DiaSemana entity) {
        DiaSemanaResponse dto = new DiaSemanaResponse();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        return dto;
    }
}
