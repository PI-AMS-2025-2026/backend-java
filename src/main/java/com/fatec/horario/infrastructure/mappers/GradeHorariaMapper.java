package com.fatec.horario.infrastructure.mappers;

import java.time.LocalDateTime;

import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.dto.GradeHoraria.GradeHorariaRequest;
import com.fatec.horario.dto.GradeHoraria.GradeHorariaResponse;

public class GradeHorariaMapper {

    public static GradeHoraria toEntity(GradeHorariaRequest request) {

        GradeHoraria grade = new GradeHoraria();

        grade.setVersao(request.versao());
        grade.setStatus(request.status());
        grade.setDataCriacao(LocalDateTime.now());

        return grade;
    }

    public static GradeHorariaResponse toResponse(GradeHoraria grade) {

        return new GradeHorariaResponse(
                grade.getId(),
                grade.getVersao(),
                grade.getDataCriacao(),
                grade.getStatus(),
                grade.getCurso().getId(),
                grade.getPeriodoLetivo().getId()
        );
    }
}