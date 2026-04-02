package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.PeriodoLetivo;
import com.fatec.horario.dto.PeriodoLetivo.PeriodoLetivoRequest;
import com.fatec.horario.dto.PeriodoLetivo.PeriodoLetivoResponse;

public class PeriodoLetivoMapper {

    public static PeriodoLetivo toEntity(PeriodoLetivoRequest request) {
        PeriodoLetivo periodoLetivo = new PeriodoLetivo();
        periodoLetivo.setAno(request.ano());
        periodoLetivo.setPeriodo(request.periodo());
        periodoLetivo.setDataInicio(request.dataInicio());
        periodoLetivo.setDataFim(request.dataFim());
        periodoLetivo.setStatus(request.status());

        return periodoLetivo;

    }

    public static PeriodoLetivoResponse toResponse(PeriodoLetivo periodoLetivo) {
        return new PeriodoLetivoResponse(
                periodoLetivo.getIdPeriodoLetivo(),
                periodoLetivo.getAno(),
                periodoLetivo.getPeriodo(),
                periodoLetivo.getDataInicio(),
                periodoLetivo.getDataFim(),
                periodoLetivo.getStatus());
    }
}
