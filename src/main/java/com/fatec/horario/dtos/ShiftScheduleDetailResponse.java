package com.fatec.horario.dtos;

import java.time.LocalTime;

public record ShiftScheduleDetailResponse(
        Long id,
        ShiftScheduleResponse shiftSchedule,
        Integer lessonNumber,
        LocalTime startTime,
        LocalTime endTime
) {
}
