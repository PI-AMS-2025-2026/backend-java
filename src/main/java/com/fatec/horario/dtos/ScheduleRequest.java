package com.fatec.horario.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ScheduleRequest(

    @NotNull(message = "Lesson Number is required")
    @Positive(message = "Lesson Number must be positive")
    Integer lessonNumber,

    @NotNull(message = "Weekday is required")
    @Positive(message = "Weekday must be positive")
    Integer weekday,
    Long shiftScheduleId,
    Long classroomId,
    Long academicSemesterId,
    Long subjectId,
    Long professorId
) {}
