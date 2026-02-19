package com.fatec.horario.dtos;

public record CourseResponse(
    Long id,
    String name,
    String description,
    ModalityResponse modality,
    PeriodicityResponse periodicity
) {}
