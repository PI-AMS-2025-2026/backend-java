package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.Horario;
import com.fatec.gini.dto.horarios.HorarioRequest;
import com.fatec.gini.dto.horarios.HorarioResponse;

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