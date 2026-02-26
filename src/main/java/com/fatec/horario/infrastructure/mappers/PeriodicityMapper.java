package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Periodicity;
import com.fatec.horario.dto.Periodicity.PeriodicityRequest;
import com.fatec.horario.dto.Periodicity.PeriodicityResponse;

public class PeriodicityMapper {

    public static Periodicity toEntity(PeriodicityRequest request) {

        Periodicity periodicity = new Periodicity();
        periodicity.setDescription(request.description());
        return periodicity;
    }

    public static PeriodicityResponse toResponse(Periodicity entity) {
        return new PeriodicityResponse(
                entity.getId(),
                entity.getDescription());
    }
}
