package com.fatec.horario.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ShiftScheduleRequest(
        @NotBlank String shiftDescription,

        @NotNull(message = "Start time is required") @Min(value = 0, message = "Start time must be at least 0") @Max(value = 23, message = "Start time must be at most 23") Integer startTime,

        @NotNull(message = "Lesson count is required") @Positive(message = "Lesson count must be positive") Integer lessonCount,

        @NotNull(message = "Lesson duration is required") @Positive(message = "Lesson duration must be positive") Integer lessonDuration,
        Boolean includedSaturday

) {
}
