package com.fatec.horario.dto;

import java.time.LocalTime;

public record ShiftScheduleDetailResponse(
                Long id,
                ShiftScheduleResponse shiftSchedule,
                Integer lessonNumber,
                LocalTime startTime,
                LocalTime endTime) {
}
