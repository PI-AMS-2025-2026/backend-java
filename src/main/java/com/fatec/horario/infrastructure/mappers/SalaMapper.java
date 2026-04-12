package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Sala;
import com.fatec.horario.domain.entities.TipoSala;
import com.fatec.horario.dto.Sala.SalaRequest;
import com.fatec.horario.dto.Sala.SalaResponse;

public class SalaMapper {

    public static Sala toEntity(SalaRequest request, TipoSala tipoSala) {
        Sala sala = new Sala();
        sala.setCodigo(request.getCodigo());
        sala.setCapacidade(request.getCapacidade());
        sala.setTipoSala(tipoSala);
        return sala;
    }

    public static SalaResponse toResponse(Sala entity) {
        return new SalaResponse(
            entity.getId(),
            entity.getCodigo(),
            entity.getCapacidade(),
            entity.getTipoSala().getId()
        );
    }
}