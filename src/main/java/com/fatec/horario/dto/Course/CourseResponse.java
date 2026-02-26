package com.fatec.horario.dto.Course;

import com.fatec.horario.dto.Modality.ModalityResponse;
import com.fatec.horario.dto.Periodicity.PeriodicityResponse;

public record CourseResponse(
        Long id,
        String name,
        String description,
        ModalityResponse modality,
        PeriodicityResponse periodicity) {
}
