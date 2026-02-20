package com.fatec.horario.dto;

public record ShiftScheduleResponse(
                Long id,
                String shiftDescription,
                Integer startTime,
                Integer lessonCount,
                Integer lessonDuration,
                Boolean includedSaturday) {

}
