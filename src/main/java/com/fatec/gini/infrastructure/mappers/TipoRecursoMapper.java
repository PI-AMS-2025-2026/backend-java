package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.TipoRecurso;
import com.fatec.gini.dto.tipoRecurso.TipoRecursoRequest;
import com.fatec.gini.dto.tipoRecurso.TipoRecursoResponse;

public class TipoRecursoMapper {

    public static TipoRecurso toEntity(TipoRecursoRequest request) {
        TipoRecurso entity = new TipoRecurso();
        entity.setNome(request.nome());
        return entity;
    }

    public static TipoRecursoResponse toResponse(TipoRecurso entity) {
        return new TipoRecursoResponse(
                entity.getId(),
                entity.getNome());
    }
}
