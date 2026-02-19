package com.fatec.horario.mappers;

import com.fatec.horario.entities.Periodicity;
import com.fatec.horario.dtos.PeriodicityRequest;
import com.fatec.horario.dtos.PeriodicityResponse;

public class PeriodicityMapper {

    public static Periodicity toEntity(PeriodicityRequest request) {

        Periodicity periodicity = new Periodicity();
        periodicity.setDescription(request.description());
        return periodicity;
    }

    public static PeriodicityResponse toResponse(Periodicity entity) {
       return new PeriodicityResponse(
            entity.getId(), 
            entity.getDescription()
        );
    }        
}
