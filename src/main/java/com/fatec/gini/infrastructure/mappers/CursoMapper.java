package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.dto.curso.CursoRequest;
import com.fatec.gini.dto.curso.CursoResponse;

public class CursoMapper {

    public static Curso toEntity(CursoRequest request) {
        Curso entity = new Curso();
        entity.setNome(request.nome());
        entity.setPeriodicidade(request.periodicidade());
        entity.setStatus(request.status());
        entity.setDuracao(request.duracao());
        return entity;
    }

    public static CursoResponse toResponse(Curso entity) {
        return new CursoResponse(
                entity.getId(),
                entity.getNome(),
                entity.getPeriodicidade(),
                entity.getStatus(),
                entity.getDuracao(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}