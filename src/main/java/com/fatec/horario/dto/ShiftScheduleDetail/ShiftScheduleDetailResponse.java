package com.fatec.horario.dto.ShiftScheduleDetail;

import java.time.LocalTime;

import com.fatec.horario.dto.ShiftSchedule.ShiftScheduleResponse;

public record ShiftScheduleDetailResponse(
                Long id,
                ShiftScheduleResponse shiftSchedule,
                Integer lessonNumber,
                LocalTime startTime,
                LocalTime endTime) {
}
