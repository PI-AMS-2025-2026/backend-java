package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.PeriodoAtividadeQuadro;
import com.fatec.gini.dto.periodoAtividadeQuadro.PeriodoAtividadeQuadroRequest;
import com.fatec.gini.dto.periodoAtividadeQuadro.PeriodoAtividadeQuadroResponse;

public class PeriodoAtividadeQuadroMapper {

    public static PeriodoAtividadeQuadro toEntity(PeriodoAtividadeQuadroRequest request) {
        PeriodoAtividadeQuadro periodoAtividadeQuadro = new PeriodoAtividadeQuadro();
        periodoAtividadeQuadro.setAno(request.ano());
        periodoAtividadeQuadro.setPeriodo(request.periodo());
        periodoAtividadeQuadro.setDataInicio(request.dataInicio());
        periodoAtividadeQuadro.setDataFim(request.dataFim());
        periodoAtividadeQuadro.setStatus(request.status());
        return periodoAtividadeQuadro;

    }

    public static PeriodoAtividadeQuadroResponse toResponse(PeriodoAtividadeQuadro periodoAtividadeQuadro) {
        return new PeriodoAtividadeQuadroResponse(
                periodoAtividadeQuadro.getId(),
                periodoAtividadeQuadro.getAno(),
                periodoAtividadeQuadro.getPeriodo(),
                periodoAtividadeQuadro.getDataInicio(),
                periodoAtividadeQuadro.getDataFim(),
                periodoAtividadeQuadro.getStatus());
    }
}
