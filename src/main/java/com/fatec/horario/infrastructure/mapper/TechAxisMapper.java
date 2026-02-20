package com.fatec.horario.infrastructure.mapper;

import com.fatec.horario.domain.model.TechAxis;
import com.fatec.horario.dto.TechAxisRequest;
import com.fatec.horario.dto.TechAxisResponse;

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
