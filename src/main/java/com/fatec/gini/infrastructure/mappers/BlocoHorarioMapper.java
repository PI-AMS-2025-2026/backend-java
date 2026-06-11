package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.dto.blocoHorario.BlocoHorarioRequest;
import com.fatec.gini.dto.blocoHorario.BlocoHorarioResponse;

public class BlocoHorarioMapper {

    public static BlocoHorario toEntity(BlocoHorarioRequest request) {
        BlocoHorario entity = new BlocoHorario();
        entity.setHoraInicio(request.horaInicio());
        entity.setHoraFim(request.horaFim());
        entity.setDuracao(request.duracao());
        return entity;
    }

    public static BlocoHorarioResponse toResponse(BlocoHorario entity) {
        return new BlocoHorarioResponse(
            entity.getId(),
            entity.getHoraInicio(),
            entity.getHoraFim(),
            entity.getDuracao()
        );
    }
}