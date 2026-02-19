package com.fatec.horario.mappers;

import com.fatec.horario.dtos.TechAxisRequest;
import com.fatec.horario.dtos.TechAxisResponse;
import com.fatec.horario.entities.TechAxis;

public class TechAxisMapper {

    public static TechAxis toEntity(TechAxisRequest request) {
        TechAxis techAxis = new TechAxis();
        techAxis.setName(request.name());
        return techAxis;
    }

    public static TechAxisResponse toResponse(TechAxis entity) {

        return new TechAxisResponse(
            entity.getId(),
            entity.getName()
        );
    }
}
