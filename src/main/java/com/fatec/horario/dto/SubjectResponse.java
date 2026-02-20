package com.fatec.horario.dto;

public record SubjectResponse(
        Long id,
        String name,
        String acronym,
        TechAxisResponse techAxis,
        ModalityResponse modality

) {
}
