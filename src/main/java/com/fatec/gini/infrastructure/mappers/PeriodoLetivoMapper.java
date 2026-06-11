package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.PeriodoLetivo;
import com.fatec.gini.dto.periodoLetivo.PeriodoLetivoRequest;
import com.fatec.gini.dto.periodoLetivo.PeriodoLetivoResponse;

public class PeriodoLetivoMapper {

    public static PeriodoLetivo toEntity(PeriodoLetivoRequest request) {

        if (request == null) {
            return null;
        }

        PeriodoLetivo periodoLetivo = new PeriodoLetivo();

        periodoLetivo.setAno(request.ano());
        periodoLetivo.setPeriodo(request.periodo());
        periodoLetivo.setDataInicio(request.dataInicio());
        periodoLetivo.setDataFim(request.dataFim());
        periodoLetivo.setStatus(request.status());

        return periodoLetivo;
    }

    public static PeriodoLetivoResponse toResponse(PeriodoLetivo periodoLetivo) {

        if (periodoLetivo == null) {
            return null;
        }

        return new PeriodoLetivoResponse(
                periodoLetivo.getId(),
                periodoLetivo.getAno(),
                periodoLetivo.getPeriodo(),
                periodoLetivo.getDataInicio(),
                periodoLetivo.getDataFim(),
                periodoLetivo.getStatus(),
                periodoLetivo.getCreatedAt(),
                periodoLetivo.getUpdatedAt());
    }
}