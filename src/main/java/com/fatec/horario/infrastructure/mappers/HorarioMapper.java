package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Horario;
import com.fatec.horario.dto.Horarios.HorarioRequest;
import com.fatec.horario.dto.Horarios.HorarioResponse;

public class HorarioMapper {

    public static Horario toEntity(HorarioRequest request) {
        Horario entity = new Horario();
        entity.setHoraInicio(request.getHoraInicio());
        entity.setHoraFim(request.getHoraFim());
        entity.setDuracao(request.getDuracao());
        return entity;
    }

    public static HorarioResponse toResponse(Horario entity) {
        HorarioResponse response = new HorarioResponse();
        response.setId(entity.getId());
        response.setHoraInicio(entity.getHoraInicio());
        response.setHoraFim(entity.getHoraFim());
        response.setDuracao(entity.getDuracao());
        return response;
    }
}