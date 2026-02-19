package com.fatec.horario.mappers;

import com.fatec.horario.dtos.ModalityRequest;
import com.fatec.horario.dtos.ModalityResponse;
import com.fatec.horario.entities.Modality;

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
            Modality.getName()
        );
    }
}