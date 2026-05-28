package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.TipoSala;
import com.fatec.gini.dto.tipoSala.TipoSalaRequest;
import com.fatec.gini.dto.tipoSala.TipoSalaResponse;

public class TipoSalaMapper {

    public static TipoSala toEntity(TipoSalaRequest request) {
        TipoSala entity = new TipoSala();
        entity.setNome(request.nome());
        return entity;
    }

    public static TipoSalaResponse toResponse(TipoSala entity) {
        return new TipoSalaResponse(
                entity.getId(),
                entity.getNome());
    }
}