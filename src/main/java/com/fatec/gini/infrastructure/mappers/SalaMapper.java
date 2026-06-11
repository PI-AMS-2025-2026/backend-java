package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.dto.sala.SalaRequest;
import com.fatec.gini.dto.sala.SalaResponse;

public class SalaMapper {

    public static Sala toEntity(SalaRequest request) {
        Sala entity = new Sala();
        entity.setCodigo(request.codigo());
        entity.setCapacidade(request.capacidade());
        return entity;
    }

    public static SalaResponse toResponse(Sala entity) {
        return new SalaResponse(
                entity.getId(),
                entity.getCodigo(),
                entity.getCapacidade(),
                entity.getTipoSala() != null ? TipoSalaMapper.toResponse(entity.getTipoSala()) : null,
                entity.getCreated_at(),
                entity.getUpdated_at()
        );
    }
}