package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.TipoSala;
import com.fatec.horario.dto.tipoSala.TipoSalaRequest;
import com.fatec.horario.dto.tipoSala.TipoSalaResponse;

public class TipoSalaMapper {

    public static TipoSala toEntity(TipoSalaRequest request) {
        TipoSala entity = new TipoSala();
        entity.setNome(request.nome());
        return entity;
    }

    public static TipoSalaResponse toResponse(TipoSala entity) {
        return new TipoSalaResponse(
            entity.getid_tipo_sala(),
            entity.getNome()
        );
    }
}