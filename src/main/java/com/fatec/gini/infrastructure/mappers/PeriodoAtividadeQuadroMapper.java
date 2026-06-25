package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.PeriodoAtividadeQuadro;
import com.fatec.gini.dto.periodoAtividadeQuadro.PeriodoAtividadeQuadroRequest;
import com.fatec.gini.dto.periodoAtividadeQuadro.PeriodoAtividadeQuadroResponse;

public class PeriodoAtividadeQuadroMapper {

    public static PeriodoAtividadeQuadro toEntity(PeriodoAtividadeQuadroRequest request) {
        PeriodoAtividadeQuadro entity = new PeriodoAtividadeQuadro();
        entity.setAno(request.ano());
        entity.setPeriodo(request.periodo());
        entity.setDataInicio(request.dataInicio());
        entity.setDataFim(request.dataFim());
        entity.setStatus(request.status());
        return entity;

    }

    public static PeriodoAtividadeQuadroResponse toResponse(PeriodoAtividadeQuadro entity) {
        return new PeriodoAtividadeQuadroResponse(
                entity.getId(),
                entity.getAno(),
                entity.getPeriodo(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
