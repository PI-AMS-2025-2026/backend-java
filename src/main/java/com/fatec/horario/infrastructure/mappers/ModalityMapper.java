package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Modality;
import com.fatec.horario.dto.Modality.ModalityRequest;
import com.fatec.horario.dto.Modality.ModalityResponse;

public class ModalityMapper {

    public static Modality toEntity(ModalityRequest request) {
        if (request == null) {
            return null;
        }
        Modality Modality = new Modality();
        Modality.setName(request.name());
        return Modality;
    }

    public static ModalityResponse toResponse(Modality Modality) {
        return new ModalityResponse(
                Modality.getId(),
                Modality.getName());
    }
}
