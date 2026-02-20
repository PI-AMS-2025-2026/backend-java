package com.fatec.horario.dto.ShiftScheduleDetail;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalTime;

public record ShiftScheduleDetailRequest(
                @NotNull(message = "Shift schedule ID is required") @Positive Long shiftScheduleId,

                @NotNull(message = "Lesson number is required") @Positive Integer lessonNumber,

                @NotNull(message = "Start time is required") LocalTime startTime,

                @NotNull(message = "End time is required") LocalTime endTime) {
}
