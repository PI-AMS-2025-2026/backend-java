package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Horario;
import com.fatec.horario.dto.horarios.HorarioRequest;
import com.fatec.horario.dto.horarios.HorarioResponse;

public class HorarioMapper {

    public static Horario toEntity(HorarioRequest request) {
        Horario entity = new Horario();
        entity.setHoraInicio(request.horaInicio());
        entity.setHoraFim(request.horaFim());
        entity.setDuracao(request.duracao());
        return entity;
    }

    public static HorarioResponse toResponse(Horario entity) {
        return new HorarioResponse(
            entity.getId(),
            entity.getHoraInicio(),
            entity.getHoraFim(),
            entity.getDuracao()
        );
    }
}