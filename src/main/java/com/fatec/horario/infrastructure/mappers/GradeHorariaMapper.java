package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.dto.gradeHoraria.GradeHorariaRequest;
import com.fatec.horario.dto.gradeHoraria.GradeHorariaResponse;

public class GradeHorariaMapper {

    public static GradeHoraria toEntity(GradeHorariaRequest request) {

        GradeHoraria entity = new GradeHoraria();

        entity.setVersao(request.versao());
        entity.setStatus(request.status());

        return entity;
    }

    public static GradeHorariaResponse toResponse(GradeHoraria entity) {

        return new GradeHorariaResponse(
                entity.getId(),
                entity.getVersao(),
                entity.getDataCriacao(),
                entity.getStatus(),
                entity.getCurso() != null ? CursoMapper.toResponse(entity.getCurso()) : null,
                entity.getPeriodoLetivo() != null ? PeriodoLetivoMapper.toResponse(entity.getPeriodoLetivo()) : null
        );
    }
}