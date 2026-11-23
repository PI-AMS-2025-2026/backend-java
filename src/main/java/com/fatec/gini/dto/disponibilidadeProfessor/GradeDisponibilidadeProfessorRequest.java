package com.fatec.gini.dto.disponibilidadeProfessor;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record GradeDisponibilidadeProfessorRequest(
        @NotNull(message = "A lista de horários é obrigatória.")
        List<@Valid HorarioDisponibilidadeProfessorRequest> horarios) {
}
