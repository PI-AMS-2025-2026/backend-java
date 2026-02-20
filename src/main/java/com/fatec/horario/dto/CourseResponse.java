package com.fatec.horario.dto;

public record CourseResponse(
        Long id,
        String name,
        String description,
        ModalityResponse modality,
        PeriodicityResponse periodicity) {
}
