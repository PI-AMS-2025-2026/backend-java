package com.fatec.horario.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserAvailabilityRequest(

    @NotNull
    @Positive(message = "Weekday must be positive.")
    Integer weekday,

    @NotNull
    @Positive(message = "LessonNumber must be positive.")
    Integer lessonNumber,
    Long userId
) {

}
