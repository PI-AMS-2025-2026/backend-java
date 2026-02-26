package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.TechAxis;
import com.fatec.horario.dto.TechAxis.TechAxisRequest;
import com.fatec.horario.dto.TechAxis.TechAxisResponse;

public class TechAxisMapper {

    public static TechAxis toEntity(TechAxisRequest request) {
        TechAxis techAxis = new TechAxis();
        techAxis.setName(request.name());
        return techAxis;
    }

    public static TechAxisResponse toResponse(TechAxis entity) {

        return new TechAxisResponse(
                entity.getId(),
                entity.getName());
    }
}
