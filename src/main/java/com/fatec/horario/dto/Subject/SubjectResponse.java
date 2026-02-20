package com.fatec.horario.dto.Subject;

import com.fatec.horario.dto.Modality.ModalityResponse;
import com.fatec.horario.dto.TechAxis.TechAxisResponse;

public record SubjectResponse(
        Long id,
        String name,
        String acronym,
        TechAxisResponse techAxis,
        ModalityResponse modality

) {
}
