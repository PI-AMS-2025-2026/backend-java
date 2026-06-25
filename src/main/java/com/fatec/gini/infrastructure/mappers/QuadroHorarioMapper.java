package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioRequest;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioResponse;

public class QuadroHorarioMapper {

    public static QuadroHorario toEntity(QuadroHorarioRequest request) {

        QuadroHorario entity = new QuadroHorario();

        entity.setVersao(request.versao());
        entity.setStatus(request.status());

        return entity;
    }

    public static QuadroHorarioResponse toResponse(QuadroHorario entity) {

        return new QuadroHorarioResponse(
                entity.getId(),
                entity.getVersao(),
                entity.getDataCriacao(),
                entity.getStatus(),
                entity.getCurso() != null ? CursoMapper.toResponse(entity.getCurso()) : null,
                entity.getPeriodoAtividadeQuadro() != null
                        ? PeriodoAtividadeQuadroMapper.toResponse(entity.getPeriodoAtividadeQuadro())
                        : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}